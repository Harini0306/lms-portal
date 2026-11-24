
package com.lmsportal.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.lmsportal.model.LearningMaterial;

public interface LearningMaterialRepository
        extends MongoRepository<LearningMaterial, String> {

    List<LearningMaterial> findByCourseId(String courseId);
}
