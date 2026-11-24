package com.lmsportal.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lmsportal.model.Enrollment;
import com.lmsportal.model.Student;
import com.lmsportal.repository.StudentRepository;
import com.lmsportal.service.EnrollmentService;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final StudentRepository studentRepository;

    public EnrollmentController(
            EnrollmentService enrollmentService,
            StudentRepository studentRepository) {
        this.enrollmentService = enrollmentService;
        this.studentRepository = studentRepository;
    }

    private Student getLoggedInStudent(Jwt jwt) {
        return studentRepository.findByEmail(jwt.getSubject())
                .orElseThrow(() ->
                        new IllegalArgumentException("Student not found"));
    }

    @PostMapping
    public ResponseEntity<?> enrollStudent(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam String courseId) {
        try {
            Student student = getLoggedInStudent(jwt);

            Enrollment enrollment = enrollmentService.enrollStudent(
                    student.getId(), courseId);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(enrollment);

        } catch (IllegalStateException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(exception.getMessage());
        }
    }

    @GetMapping("/me")
    public List<Enrollment> getMyEnrollments(
            @AuthenticationPrincipal Jwt jwt) {

        Student student = getLoggedInStudent(jwt);

        return enrollmentService.getStudentEnrollments(student.getId());
    }

    @DeleteMapping("/course/{courseId}")
    public ResponseEntity<Void> unenrollStudent(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String courseId) {

        Student student = getLoggedInStudent(jwt);

        enrollmentService.unenrollStudent(
                student.getId(), courseId);

        return ResponseEntity.noContent().build();
    }
}

