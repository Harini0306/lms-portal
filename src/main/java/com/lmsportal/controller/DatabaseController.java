
package com.lmsportal.controller;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DatabaseController {

    private final MongoTemplate mongoTemplate;

    public DatabaseController(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @GetMapping("/api/db-check")
    public Map<String, Object> checkDatabase() {
        return mongoTemplate.executeCommand(
            new org.bson.Document("ping", 1)
        );
    }
}
