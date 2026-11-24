
package com.lmsportal.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.lmsportal.model.Student;
import com.lmsportal.repository.StudentRepository;

@Configuration
public class AdminAccountInitializer {

    @Value("${lms.admin.email:}")
    private String adminEmail;

    @Value("${lms.admin.password:}")
    private String adminPassword;

    @Value("${lms.admin.reset-password:false}")
    private boolean resetAdminPassword;

    @Bean
    public ApplicationRunner createAdmin(
            StudentRepository studentRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            if (adminEmail.isBlank() || adminPassword.isBlank()) {
                System.out.println("Admin setup skipped: credentials not configured.");
                return;
            }

            var existingAdmin = studentRepository.findByEmail(adminEmail);

            if (existingAdmin.isPresent()) {
                if (resetAdminPassword) {
                    Student admin = existingAdmin.get();
                    admin.setPassword(passwordEncoder.encode(adminPassword));
                    studentRepository.save(admin);
                    System.out.println("Admin password reset successfully.");
                } else {
                    System.out.println("Admin account already exists.");
                }
                return;
            }

            Student admin = new Student();
            admin.setName("LMS Administrator");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole("ADMIN");

            studentRepository.save(admin);

            System.out.println("Admin account created successfully.");
        };
    }
}
