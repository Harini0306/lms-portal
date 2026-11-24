package com.lmsportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lmsportal.model.Enrollment;
import com.lmsportal.repository.EnrollmentRepository;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    public Enrollment enrollStudent(String studentId, String courseId) {

        if (enrollmentRepository.existsByStudentIdAndCourseId(
                studentId, courseId)) {

            throw new IllegalStateException(
                    "Student is already enrolled in this course");
        }

        Enrollment enrollment = new Enrollment(studentId, courseId);

        return enrollmentRepository.save(enrollment);
    }

    public List<Enrollment> getStudentEnrollments(String studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    public void unenrollStudent(String studentId, String courseId) {
        enrollmentRepository.deleteByStudentIdAndCourseId(
                studentId, courseId);
    }
}
