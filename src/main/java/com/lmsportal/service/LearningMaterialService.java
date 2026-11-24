
package com.lmsportal.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.lmsportal.model.LearningMaterial;
import com.lmsportal.repository.LearningMaterialRepository;

@Service
public class LearningMaterialService {

    private final LearningMaterialRepository repository;

    public LearningMaterialService(
            LearningMaterialRepository repository) {
        this.repository = repository;
    }

    public LearningMaterial addMaterial(LearningMaterial material) {
        return repository.save(material);
    }

    public List<LearningMaterial> getMaterialsByCourse(String courseId) {
        return repository.findByCourseId(courseId);
    }

    public Optional<LearningMaterial> getMaterialById(String id) {
        return repository.findById(id);
    }

    public LearningMaterial updateMaterial(
            String id, LearningMaterial updatedMaterial) {

        LearningMaterial material = repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Material not found"));

        material.setTitle(updatedMaterial.getTitle());
        material.setDescription(updatedMaterial.getDescription());
        material.setType(updatedMaterial.getType());
        material.setUrl(updatedMaterial.getUrl());

        return repository.save(material);
    }

    public void deleteMaterial(String id) {
        repository.deleteById(id);
    }
}

