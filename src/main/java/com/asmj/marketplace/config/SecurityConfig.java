package com.asmj.marketplace.config;

import com.asmj.marketplace.model.UserAccount;
import com.asmj.marketplace.model.VendorStatus;
import com.asmj.marketplace.repository.UserAccountRepository;
import com.asmj.marketplace.security.MarketplaceUserDetailsService;
import java.util.function.Supplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider(
            MarketplaceUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            DaoAuthenticationProvider authenticationProvider,
            UserAccountRepository users) throws Exception {

        http.authenticationProvider(authenticationProvider)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/register", "/register/**",
                        "/css/**", "/js/**", "/images/**", "/products/**", "/api/categories/**", "/chat/**", "/guest-enquiries", "/cart", "/cart/**", "/error").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/checkout", "/customer/orders", "/customer/orders/**").hasRole("CUSTOMER")
                .requestMatchers("/vendor/orders", "/vendor/orders/**").access((Supplier<Authentication> authentication, RequestAuthorizationContext context) -> { Authentication current=authentication.get(); if(current==null||!current.isAuthenticated()) return new AuthorizationDecision(false); UserAccount account=users.findByEmailIgnoreCase(current.getName()).orElse(null); return new AuthorizationDecision(account!=null && account.getRole()==com.asmj.marketplace.model.Role.ROLE_VENDOR && account.getVendorStatus()==VendorStatus.APPROVED); })
                .requestMatchers("/vendor/pending", "/vendor/application-chat").hasRole("VENDOR")
                .requestMatchers("/vendor/**").access((Supplier<Authentication> authentication,
                                                         RequestAuthorizationContext context) -> {
                    Authentication current = authentication.get();
                    if (current == null || !current.isAuthenticated()
                            || current.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_VENDOR"))) {
                        return new AuthorizationDecision(false);
                    }
                    UserAccount account = users.findByEmailIgnoreCase(current.getName()).orElse(null);
                    return new AuthorizationDecision(account != null
                            && account.isEnabled()
                            && account.getVendorStatus() == VendorStatus.APPROVED);
                })
                .requestMatchers("/customer/**", "/my-enquiries").hasRole("CUSTOMER")
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("email")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error")
                .permitAll())
            .logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll())
            .exceptionHandling(ex -> ex.accessDeniedPage("/access-denied"));

        return http.build();
    }
}
