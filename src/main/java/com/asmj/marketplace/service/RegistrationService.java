package com.asmj.marketplace.service;

import com.asmj.marketplace.dto.RegistrationForm;
import com.asmj.marketplace.model.Role;
import com.asmj.marketplace.model.UserAccount;
import com.asmj.marketplace.model.VendorProfile;
import com.asmj.marketplace.model.VendorStatus;
import com.asmj.marketplace.repository.UserAccountRepository;
import com.asmj.marketplace.repository.VendorProfileRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.time.Instant;

@Service
public class RegistrationService {
    private final UserAccountRepository users;
    private final VendorProfileRepository vendorProfiles;
    private final PasswordEncoder encoder;

    public RegistrationService(UserAccountRepository users,
                               VendorProfileRepository vendorProfiles,
                               PasswordEncoder encoder) {
        this.users = users;
        this.vendorProfiles = vendorProfiles;
        this.encoder = encoder;
    }

    public void registerCustomer(RegistrationForm form) {
        createAccount(form, Role.ROLE_CUSTOMER, VendorStatus.NOT_APPLICABLE);
    }

    public void registerVendor(RegistrationForm form) {
        if (!StringUtils.hasText(form.getBusinessName())
                || !StringUtils.hasText(form.getAddressLine())
                || !StringUtils.hasText(form.getCity())
                || !StringUtils.hasText(form.getState())
                || !StringUtils.hasText(form.getPinCode())) {
            throw new IllegalArgumentException("Complete the shop name, address, city, state and PIN code for vendor registration.");
        }

        UserAccount account = createAccount(form, Role.ROLE_VENDOR, VendorStatus.PENDING);
        VendorProfile profile = new VendorProfile();
        profile.setUserId(account.getId());
        profile.setBusinessName(form.getBusinessName().trim());
        profile.setBusinessDescription(clean(form.getBusinessDescription()));
        profile.setAddressLine(form.getAddressLine().trim());
        profile.setCity(form.getCity().trim());
        profile.setState(form.getState().trim());
        profile.setPinCode(form.getPinCode().trim());
        try {
            VendorProfile savedProfile = vendorProfiles.save(profile);
            if (savedProfile == null || !StringUtils.hasText(savedProfile.getId())) {
                throw new IllegalStateException("Vendor profile was not acknowledged by the database.");
            }
        } catch (RuntimeException ex) {
            // MongoDB transactions are not assumed to be enabled on Atlas free/shared clusters.
            // Avoid leaving a vendor login without its required business profile.
            try {
                users.deleteById(account.getId());
            } catch (RuntimeException cleanupError) {
                ex.addSuppressed(cleanupError);
            }
            if (ex instanceof DataAccessException) {
                throw new IllegalStateException("The vendor account could not be completed because the business profile could not be saved. Please retry.", ex);
            }
            throw ex;
        }
    }

    private UserAccount createAccount(RegistrationForm form, Role role, VendorStatus status) {
        String email = form.getEmail().trim().toLowerCase();
        if (users.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }
        if (!form.isTermsAccepted()) throw new IllegalArgumentException("You must agree to the Terms & Conditions before registering.");
        UserAccount account = new UserAccount();
        account.setFullName(form.getFullName().trim());
        account.setEmail(email);
        account.setMobile(form.getMobile() == null ? "" : form.getMobile().trim());
        account.setPassword(encoder.encode(form.getPassword()));
        account.setRole(role);
        account.setVendorStatus(status);
        account.setTermsAccepted(true);
        account.setAcceptedTermsVersion("1.0");
        account.setTermsAcceptedAt(Instant.now());
        UserAccount saved = users.save(account);
        if (saved == null || !StringUtils.hasText(saved.getId())) {
            throw new IllegalStateException("The database did not confirm account creation. Please retry.");
        }
        return saved;
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
