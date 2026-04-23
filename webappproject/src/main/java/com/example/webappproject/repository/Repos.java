package com.example.webappproject.repository;

import com.example.webappproject.model.Models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;

public class Repos {
    public interface UserRepository extends JpaRepository<User, Long> {
        User findByUsernameAndPassword(String username, String password);
    }
    public interface ClientRepository extends JpaRepository<Client, Long> {}
    public interface ResourceRepository extends JpaRepository<Resource, Long> {
        Resource findByCategory(String category);
    }
    public interface TaskRepository extends JpaRepository<Task, Long> {}
    public interface DonationRepository extends JpaRepository<Donation, Long> {}
    public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
        void deleteByTimestampBefore(LocalDateTime cutoff);
    }
}