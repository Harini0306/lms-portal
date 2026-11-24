
package com.lmsportal.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.lmsportal.model.Course;
import com.lmsportal.service.CourseService;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // 1. Create a new course
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Course createCourse(@RequestBody Course course) {
        return courseService.createCourse(course);
    }

    // 2. Get all courses
    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    // 3. Get course by ID
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(
            @PathVariable String id) {

        Optional<Course> course = courseService.getCourseById(id);

        return course.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 4. Update an existing course
    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(
            @PathVariable String id,
            @RequestBody Course updatedCourse) {

        Optional<Course> existingCourse =
                courseService.getCourseById(id);

        if (existingCourse.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Course course = existingCourse.get();

        course.setTitle(updatedCourse.getTitle());
        course.setDescription(updatedCourse.getDescription());
        course.setInstructor(updatedCourse.getInstructor());
        course.setDurationHours(updatedCourse.getDurationHours());
        course.setPublished(updatedCourse.isPublished());

        Course savedCourse = courseService.createCourse(course);

        return ResponseEntity.ok(savedCourse);
    }

    // 5. Delete a course
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable String id) {

        if (!courseService.getCourseById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        courseService.deleteCourse(id);

        return ResponseEntity.noContent().build();
    }
}
