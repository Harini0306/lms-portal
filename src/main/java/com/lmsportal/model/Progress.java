
package com.lmsportal.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "progress")
public class Progress {

    @Id
    private String id;

    private String studentId;
    private String courseId;
    private List<String> completedMaterialIds = new ArrayList<>();
    private double completionPercentage;
    private LocalDateTime lastUpdated;

    public Progress() {
        this.lastUpdated = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public List<String> getCompletedMaterialIds() {
        return completedMaterialIds;
    }

    public void setCompletedMaterialIds(List<String> completedMaterialIds) {
        this.completedMaterialIds = completedMaterialIds;
    }

    public double getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(double completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}

