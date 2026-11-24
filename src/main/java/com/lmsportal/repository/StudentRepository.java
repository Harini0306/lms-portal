package com.lmsportal.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.lmsportal.model.Student;

public interface StudentRepository
        extends MongoRepository<Student, String> {

    Optional<Student> findByEmail(String email);

    boolean existsByEmail(String email);
}
