package com.lmsportal.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lmsportal.model.Student;
import com.lmsportal.repository.StudentRepository;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentService(
            StudentRepository studentRepository,
            PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Student createStudent(Student student) {

        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new IllegalStateException("Email is already registered");
        }

        student.setPassword(
                passwordEncoder.encode(student.getPassword())
        );

        student.setRole("STUDENT");

        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentById(String id) {
        return studentRepository.findById(id);
    }

    public Optional<Student> updateStudent(
            String id, Student updatedStudent) {

        return studentRepository.findById(id).map(student -> {

            if (!student.getEmail().equals(updatedStudent.getEmail())
                    && studentRepository.existsByEmail(
                            updatedStudent.getEmail())) {
                throw new IllegalStateException(
                        "Email is already registered");
            }

            student.setName(updatedStudent.getName());
            student.setEmail(updatedStudent.getEmail());

            // Role and password cannot be changed through this endpoint.
            return studentRepository.save(student);
        });
    }

    public void deleteStudent(String id) {
        studentRepository.deleteById(id);
    }
}
