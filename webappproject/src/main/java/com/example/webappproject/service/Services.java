package com.example.webappproject.service;

import com.example.webappproject.model.Models.AuditLog;
import com.example.webappproject.repository.Repos.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

public class Services {

    @Service
    public static class AuditService {
        @Autowired private AuditLogRepository repo;
        
        public void log(String actor, String action, String target, String entityId) {
            AuditLog log = new AuditLog();
            log.setActor(actor); log.setAction(action);
            log.setTargetEntity(target); log.setEntityId(entityId);
            repo.save(log);
        }
    }

    @Service
    public static class CleanupService {
        @Autowired private AuditLogRepository repo;

        @Scheduled(cron = "0 0 0 * * *") // Midnight every day
        public void cleanupOldLogs() {
            LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
            repo.deleteByTimestampBefore(cutoff);
            System.out.println("Cleaned up old audit logs");
        }
    }
}