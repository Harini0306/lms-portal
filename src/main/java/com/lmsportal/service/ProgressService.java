
package com.lmsportal.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lmsportal.model.Progress;
import com.lmsportal.repository.EnrollmentRepository;
import com.lmsportal.repository.LearningMaterialRepository;
import com.lmsportal.repository.ProgressRepository;

@Service
public class ProgressService {

    private final ProgressRepository progressRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LearningMaterialRepository materialRepository;

    public ProgressService(
            ProgressRepository progressRepository,
            EnrollmentRepository enrollmentRepository,
            LearningMaterialRepository materialRepository) {

        this.progressRepository = progressRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.materialRepository = materialRepository;
    }

    public Progress markMaterialCompleted(
            String studentId,
            String courseId,
            String materialId) {

        if (!enrollmentRepository.existsByStudentIdAndCourseId(
                studentId, courseId)) {
            throw new IllegalStateException(
                    "Student is not enrolled in this course");
        }

        boolean materialExists = materialRepository.findByCourseId(courseId)
                .stream()
                .anyMatch(material -> material.getId().equals(materialId));

        if (!materialExists) {
            throw new IllegalArgumentException(
                    "Material does not belong to this course");
        }

        Progress progress = progressRepository
                .findByStudentIdAndCourseId(studentId, courseId)
                .orElseGet(() -> {
                    Progress newProgress = new Progress();
                    newProgress.setStudentId(studentId);
                    newProgress.setCourseId(courseId);
                    newProgress.setCompletedMaterialIds(new ArrayList<>());
                    return newProgress;
                });

        List<String> completed = progress.getCompletedMaterialIds();

        if (!completed.contains(materialId)) {
            completed.add(materialId);
        }

        int totalMaterials =
                materialRepository.findByCourseId(courseId).size();

        double percentage = totalMaterials == 0 ? 0 :
                (completed.size() * 100.0) / totalMaterials;

        progress.setCompletionPercentage(
                Math.round(percentage * 100.0) / 100.0);

        progress.setLastUpdated(LocalDateTime.now());

        return progressRepository.save(progress);
    }

    public List<Progress> getStudentProgress(String studentId) {
        return progressRepository.findByStudentId(studentId);
    }

    public Progress getCourseProgress(
            String studentId, String courseId) {

        return progressRepository
                .findByStudentIdAndCourseId(studentId, courseId)
                .orElseGet(() -> {
                    Progress progress = new Progress();
                    progress.setStudentId(studentId);
                    progress.setCourseId(courseId);
                    return progress;
                });
    }
}
