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
import org.springframework.security.config.Customizer;

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
                .requestMatchers("/", "/login", "/register", "/register/**", "/terms",
                        "/css/**", "/js/**", "/images/**", "/products/**", "/api/categories/**", "/api/products/**", "/api/master-data/**", "/api/rentals/categories", "/guest-enquiries", "/contact-queries", "/shops", "/shops/**", "/cart", "/cart/**", "/ads/**", "/jobs", "/jobs/*", "/businesses", "/classifieds", "/error").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/events", "/courses", "/wholesale", "/delivery", "/freelancers").permitAll()
                // Guest product enquiries are supported, but conversation access is additionally
                // restricted by ProductChatController to the guest session key or authenticated participant.
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/chat/product/*").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/chat/*", "/chat/*/messages").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/chat/*/message").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/emergency/requests", "/emergency/requests/**").hasRole("CUSTOMER")
                .requestMatchers("/emergency/providers/**").hasRole("VENDOR")
                .requestMatchers("/emergency").permitAll()
                .requestMatchers("/my-approval-requests", "/my-approval-requests/**").authenticated()
                .requestMatchers("/community-listings/save", "/businesses/**", "/classifieds/**", "/my-community-listings").authenticated()
                .requestMatchers("/marketplace/new/**", "/marketplace/save", "/marketplace/*/request", "/marketplace/*/edit", "/marketplace/*/delete", "/my-marketplace-listings", "/my-marketplace-listings/**", "/marketplace-extension-requests").authenticated()
                .requestMatchers("/jobs/*/apply", "/candidate/job-applications").hasRole("CUSTOMER")
                .requestMatchers("/employer/**").authenticated()
                .requestMatchers("/services/*/book", "/customer/service-bookings/**").hasRole("CUSTOMER")
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/services", "/services/**").permitAll()
                .requestMatchers("/service-provider/**", "/provider/services/**").authenticated()
                .requestMatchers("/rentals/*/book", "/customer/rentals", "/customer/rentals/**", "/api/rentals/**").hasRole("CUSTOMER")
                .requestMatchers("/rentals", "/rentals/**").permitAll()
                .requestMatchers("/checkout", "/customer/**", "/coupon/apply", "/coupon/remove", "/api/delivery/**").hasRole("CUSTOMER")
                .requestMatchers("/vendor/marketplace-requests/**").hasRole("VENDOR")
                .requestMatchers("/vendor/rentals", "/vendor/rentals/**").access((Supplier<Authentication> authentication, RequestAuthorizationContext context) -> { Authentication current=authentication.get(); if(current==null||!current.isAuthenticated()) return new AuthorizationDecision(false); UserAccount account=users.findByEmailIgnoreCase(current.getName()).orElse(null); return new AuthorizationDecision(account!=null && account.getRole()==com.asmj.marketplace.model.Role.ROLE_VENDOR && account.getVendorStatus()==VendorStatus.APPROVED && account.isEnabled()); })
                .requestMatchers("/vendor/orders", "/vendor/orders/**").access((Supplier<Authentication> authentication, RequestAuthorizationContext context) -> { Authentication current=authentication.get(); if(current==null||!current.isAuthenticated()) return new AuthorizationDecision(false); UserAccount account=users.findByEmailIgnoreCase(current.getName()).orElse(null); return new AuthorizationDecision(account!=null && account.isEnabled() && account.getRole()==com.asmj.marketplace.model.Role.ROLE_VENDOR && account.getVendorStatus()==VendorStatus.APPROVED); })
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
                .requestMatchers("/orders/**").access((Supplier<Authentication> authentication, RequestAuthorizationContext context) -> {
                    Authentication current=authentication.get(); if(current==null||!current.isAuthenticated()) return new AuthorizationDecision(false);
                    UserAccount account=users.findByEmailIgnoreCase(current.getName()).orElse(null);
                    return new AuthorizationDecision(account!=null && account.isEnabled() && (account.getRole()==com.asmj.marketplace.model.Role.ROLE_ADMIN || account.getRole()==com.asmj.marketplace.model.Role.ROLE_CUSTOMER || (account.getRole()==com.asmj.marketplace.model.Role.ROLE_VENDOR && account.getVendorStatus()==VendorStatus.APPROVED)));
                })
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("email")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error")
                .permitAll())
            .logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll())
            .exceptionHandling(ex -> ex.accessDeniedPage("/access-denied"));

        http.sessionManagement(session -> session.sessionFixation(fixation -> fixation.migrateSession()));
        http.headers(headers -> headers
            .contentTypeOptions(Customizer.withDefaults())
            .frameOptions(frame -> frame.deny())
            .referrerPolicy(referrer -> referrer.policy(org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
            .permissionsPolicy(permissions -> permissions.policy("camera=(), microphone=(), geolocation=(self), payment=()")));

        // Configure HSTS and CSP independently; each header configurer has its own return type.
        http.headers(headers -> headers
            .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000)));
        http.headers(headers -> headers
            .contentSecurityPolicy(csp -> csp
                .policyDirectives("default-src 'self'; base-uri 'self'; object-src 'none'; frame-ancestors 'none'; form-action 'self' https:; upgrade-insecure-requests")
                .reportOnly()));
        return http.build();
    }
}
