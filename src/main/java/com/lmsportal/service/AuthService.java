package com.lmsportal.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lmsportal.model.AuthResponse;
import com.lmsportal.model.LoginRequest;
import com.lmsportal.model.Student;
import com.lmsportal.repository.StudentRepository;

@Service
public class AuthService {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            StudentRepository studentRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse login(LoginRequest request) {

        Student student = studentRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid email or password"));

        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                student.getPassword()
        );

        if (!passwordMatches) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtService.generateToken(
                student.getEmail(),
                student.getRole()
        );

        return new AuthResponse(
                token,
                student.getEmail(),
                student.getRole()
        );
    }
}