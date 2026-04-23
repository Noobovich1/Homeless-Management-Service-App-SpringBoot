package com.example.webappproject.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

public class Models {

    @Entity @Data @Table(name="users")
    public static class User {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
        private String username;
        private String password;
        private String role; // Admin, Manager, SocialWorker, Volunteer
    }

    @Entity @Data @Table(name="clients")
    public static class Client {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
        private String firstName;
        private String lastName;
        private String status; // Pending, Sheltered, Housed
    }

    @Entity @Data @Table(name="resources")
    public static class Resource {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
        private String category;
        private Integer quantity;
        private Double publicFundingSource;
        private Double donorContributions = 0.0;
    }

    @Entity @Data @Table(name="tasks")
    public static class Task {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
        private String title;
        private String taskType;
        private Boolean isCompleted = false;
    }

    @Entity @Data @Table(name="donations")
    public static class Donation {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
        private String nickname;
        private Double amount;
        private String category;
        private Boolean isAnonymous;
        private LocalDateTime date = LocalDateTime.now();
    }

    @Entity @Data @Table(name="audit_logs")
    public static class AuditLog {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
        private String actor;
        private String action;
        private String targetEntity;
        private String entityId;
        private LocalDateTime timestamp = LocalDateTime.now();
    }
}