package com.smartappointment.config;

import com.smartappointment.model.entity.User;
import com.smartappointment.model.enums.Role;
import com.smartappointment.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Seeds the database with an initial admin user on first startup.
 * Credentials are read from ADMIN_EMAIL and ADMIN_PASSWORD environment variables.
 * No action is taken if an admin user already exists.
 */
@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    // Maps to ADMIN_EMAIL env variable via Spring relaxed binding
    @Value("${admin.email:admin@smartappointment.com}")
    private String adminEmail;

    // Maps to ADMIN_PASSWORD env variable — no default, must be set explicitly
    @Value("${admin.password}")
    private String adminPassword;

    @Bean
    public CommandLineRunner initializeAdminUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail(adminEmail).isPresent()) {
                logger.info("Admin user already exists: {}", adminEmail);
                return;
            }

            User admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setPhone("+90 555 000 0000");
            admin.setRole(Role.ADMIN);
            admin.setActive(true);

            userRepository.save(admin);

            logger.info("Admin user created successfully. Email: {}", adminEmail);
            logger.warn("Remember to change the admin password after first login!");
        };
    }
}
