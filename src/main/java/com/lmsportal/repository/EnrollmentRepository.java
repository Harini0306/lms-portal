
package com.lmsportal.repository;

import com.lmsportal.model.Enrollment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EnrollmentRepository extends MongoRepository<Enrollment, String> {

    List<Enrollment> findByStudentId(String studentId);

    boolean existsByStudentIdAndCourseId(String studentId, String courseId);

    void deleteByStudentIdAndCourseId(String studentId, String courseId);
}
