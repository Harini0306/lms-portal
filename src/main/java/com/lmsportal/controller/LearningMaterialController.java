
package com.lmsportal.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lmsportal.model.LearningMaterial;
import com.lmsportal.service.LearningMaterialService;

@RestController
@RequestMapping("/api/materials")
public class LearningMaterialController {

    private final LearningMaterialService service;

    public LearningMaterialController(
            LearningMaterialService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<LearningMaterial> addMaterial(
            @RequestBody LearningMaterial material) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.addMaterial(material));
    }

    @GetMapping("/course/{courseId}")
    public List<LearningMaterial> getMaterials(
            @PathVariable String courseId) {

        return service.getMaterialsByCourse(courseId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMaterial(
            @PathVariable String id,
            @RequestBody LearningMaterial material) {

        try {
            return ResponseEntity.ok(service.updateMaterial(id, material));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaterial(
            @PathVariable String id) {

        service.deleteMaterial(id);
        return ResponseEntity.noContent().build();
    }
}

