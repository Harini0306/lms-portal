
package com.lmsportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.lmsportal.model.Progress;

public interface ProgressRepository
        extends MongoRepository<Progress, String> {

    Optional<Progress> findByStudentIdAndCourseId(
            String studentId, String courseId);

    List<Progress> findByStudentId(String studentId);
}

