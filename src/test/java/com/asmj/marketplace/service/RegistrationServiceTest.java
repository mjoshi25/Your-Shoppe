package com.asmj.marketplace.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.asmj.marketplace.dto.RegistrationForm;
import com.asmj.marketplace.model.Role;
import com.asmj.marketplace.model.UserAccount;
import com.asmj.marketplace.model.VendorProfile;
import com.asmj.marketplace.model.VendorStatus;
import com.asmj.marketplace.repository.UserAccountRepository;
import com.asmj.marketplace.repository.VendorProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class RegistrationServiceTest {
    private UserAccountRepository users;
    private VendorProfileRepository profiles;
    private RegistrationService service;
    private BCryptPasswordEncoder encoder;

    @BeforeEach
    void setUp() {
        users = mock(UserAccountRepository.class);
        profiles = mock(VendorProfileRepository.class);
        encoder = new BCryptPasswordEncoder();
        service = new RegistrationService(users, profiles, encoder);
    }

    private RegistrationForm form() {
        RegistrationForm form = new RegistrationForm();
        form.setFullName("Test User");
        form.setEmail(" TEST@example.com ");
        form.setPassword("StrongPass123");
        form.setMobile("9876543210");
        form.setBusinessName("Test Shop");
        form.setBusinessDescription("Test business");
        form.setAddressLine("Main Road");
        form.setCity("Bhawanipatna");
        form.setState("Odisha");
        form.setPinCode("766001");
        return form;
    }

    @Test
    void registersCustomerWithNormalizedEmailAndEncodedPassword() {
        when(users.existsByEmailIgnoreCase("test@example.com")).thenReturn(false);
        service.registerCustomer(form());

        verify(users).save(argThat(account ->
                account.getEmail().equals("test@example.com")
                && account.getRole() == Role.ROLE_CUSTOMER
                && account.getVendorStatus() == VendorStatus.NOT_APPLICABLE
                && encoder.matches("StrongPass123", account.getPassword())));
        verifyNoInteractions(profiles);
    }

    @Test
    void registersVendorInPendingStatusAndStoresBusinessProfile() {
        when(users.existsByEmailIgnoreCase("test@example.com")).thenReturn(false);
        when(users.save(any(UserAccount.class))).thenAnswer(invocation -> {
            UserAccount saved = invocation.getArgument(0);
            saved.setId("vendor-123");
            return saved;
        });

        service.registerVendor(form());

        verify(users).save(argThat(account ->
                account.getRole() == Role.ROLE_VENDOR
                && account.getVendorStatus() == VendorStatus.PENDING));
        verify(profiles).save(argThat(profile ->
                profile.getUserId().equals("vendor-123")
                && profile.getBusinessName().equals("Test Shop")
                && profile.getPinCode().equals("766001")));
    }

    @Test
    void rejectsVendorRegistrationWithoutRequiredBusinessFields() {
        RegistrationForm form = form();
        form.setBusinessName("");
        assertThrows(IllegalArgumentException.class, () -> service.registerVendor(form));
        verifyNoInteractions(users);
    }

    @Test
    void rejectsDuplicateEmail() {
        when(users.existsByEmailIgnoreCase("test@example.com")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> service.registerCustomer(form()));
        verify(users, never()).save(any(UserAccount.class));
    }
}
