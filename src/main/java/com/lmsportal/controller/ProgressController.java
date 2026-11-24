package com.lmsportal.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lmsportal.model.Progress;
import com.lmsportal.model.Student;
import com.lmsportal.repository.StudentRepository;
import com.lmsportal.service.ProgressService;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    private final ProgressService progressService;
    private final StudentRepository studentRepository;

    public ProgressController(
            ProgressService progressService,
            StudentRepository studentRepository) {
        this.progressService = progressService;
        this.studentRepository = studentRepository;
    }

    private Student getLoggedInStudent(Jwt jwt) {
        return studentRepository.findByEmail(jwt.getSubject())
                .orElseThrow(() ->
                        new IllegalArgumentException("Student not found"));
    }

    @PostMapping("/complete")
    public ResponseEntity<?> markCompleted(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam String courseId,
            @RequestParam String materialId) {

        try {
            Student student = getLoggedInStudent(jwt);

            Progress progress = progressService.markMaterialCompleted(
                    student.getId(), courseId, materialId);

            return ResponseEntity.ok(progress);

        } catch (IllegalStateException exception) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(exception.getMessage());

        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest()
                    .body(exception.getMessage());
        }
    }

    @GetMapping("/me")
    public List<Progress> getMyProgress(
            @AuthenticationPrincipal Jwt jwt) {

        Student student = getLoggedInStudent(jwt);

        return progressService.getStudentProgress(student.getId());
    }

    @GetMapping("/me/course/{courseId}")
    public Progress getMyCourseProgress(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String courseId) {

        Student student = getLoggedInStudent(jwt);

        return progressService.getCourseProgress(
                student.getId(), courseId);
    }
}
