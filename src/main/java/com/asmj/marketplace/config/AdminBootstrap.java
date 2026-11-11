package com.asmj.marketplace.config;

import com.asmj.marketplace.model.Role;
import com.asmj.marketplace.model.UserAccount;
import com.asmj.marketplace.model.VendorStatus;
import com.asmj.marketplace.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

@Configuration
public class AdminBootstrap {
    @Bean
    CommandLineRunner createInitialAdmin(
            UserAccountRepository users,
            PasswordEncoder encoder,
            @Value("${app.bootstrap-admin.email:}") String email,
            @Value("${app.bootstrap-admin.password:}") String password,
            @Value("${app.bootstrap-admin.name:Marketplace Admin}") String name) {
        return args -> {
            if (!StringUtils.hasText(email) || !StringUtils.hasText(password)) {
                System.out.println("Initial admin not created: set APP_ADMIN_EMAIL and APP_ADMIN_PASSWORD.");
                return;
            }
            if (users.existsByEmailIgnoreCase(email)) return;
            UserAccount admin = new UserAccount();
            admin.setFullName(name);
            admin.setEmail(email.trim().toLowerCase());
            admin.setPassword(encoder.encode(password));
            admin.setRole(Role.ROLE_ADMIN);
            admin.setVendorStatus(VendorStatus.NOT_APPLICABLE);
            users.save(admin);
            System.out.println("Initial ASMJ Shoppe admin account created.");
        };
    }
}
