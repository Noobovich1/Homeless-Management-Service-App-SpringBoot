package com.example.webappproject.controller;

import com.example.webappproject.model.Models.*;
import com.example.webappproject.repository.Repos.*;
import com.example.webappproject.service.Services.AuditService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ApiController {

    @Autowired private UserRepository userRepo;
    @Autowired private ResourceRepository resourceRepo;
    @Autowired private ClientRepository clientRepo;
    @Autowired private TaskRepository taskRepo;
    @Autowired private DonationRepository donationRepo;
    @Autowired private AuditLogRepository auditRepo;
    @Autowired private AuditService audit;

    // --- LOGIN & SEEDING ---
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> creds) {
        String username = creds.get("username");
        String password = creds.get("password");
        
        System.out.println("DEBUG: Login attempt for " + username); // This will show in your VS Code terminal
        
        User user = userRepo.findByUsernameAndPassword(username, password);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(401).body(Map.of("message", "Invalid Credentials"));
    }

    @PostMapping("/admin/seed-users")
    public ResponseEntity<?> seedUsers() {
        if(userRepo.count() == 0) {
            User admin = new User(); admin.setUsername("admin"); admin.setPassword("123"); admin.setRole("Admin");
            userRepo.save(admin);
            return ResponseEntity.ok(Map.of("message", "Test admin created. User: admin, Pass: 123"));
        }
        return ResponseEntity.ok(Map.of("message", "Users already exist"));
    }

    // --- RESOURCES ---
    @GetMapping("/resources")
    public List<Resource> getResources() { return resourceRepo.findAll(); }

    @PostMapping("/admin/init-resources")
    public ResponseEntity<?> initResources() {
        if(resourceRepo.count() == 0) {
            Resource meals = new Resource(); meals.setCategory("Meals"); meals.setQuantity(100); meals.setPublicFundingSource(500.0);
            Resource beds = new Resource(); beds.setCategory("Beds"); beds.setQuantity(20); beds.setPublicFundingSource(2000.0);
            resourceRepo.save(meals); resourceRepo.save(beds);
        }
        return ResponseEntity.ok(Map.of("message", "Resources Initialized"));
    }

    @PatchMapping("/resources/{id}/deduct")
    public Resource deductResource(@PathVariable Long id) {
        Resource r = resourceRepo.findById(id).orElseThrow();
        if (r.getQuantity() > 0) r.setQuantity(r.getQuantity() - 1);
        audit.log("SYSTEM", "DEDUCT", "Resource", r.getCategory());
        return resourceRepo.save(r);
    }

    // --- CLIENTS ---
    @GetMapping("/clients")
    public List<Client> getClients() { return clientRepo.findAll(); }

    @PostMapping("/clients")
    public Client addClient(@RequestBody Client c) {
        Client saved = clientRepo.save(c);
        audit.log("USER", "CREATE", "Client", saved.getId().toString());
        return saved;
    }

    // --- TASKS ---
    @GetMapping("/tasks")
    public List<Task> getTasks() { return taskRepo.findAll(); }

    @PostMapping("/tasks")
    public Task addTask(@RequestBody Task t) {
        Task saved = taskRepo.save(t);
        audit.log("USER", "CREATE", "Task", saved.getId().toString());
        return saved;
    }

    @PatchMapping("/tasks/{id}/complete")
    public Task completeTask(@PathVariable Long id) {
        Task t = taskRepo.findById(id).orElseThrow();
        t.setIsCompleted(true);
        return taskRepo.save(t);
    }

    @DeleteMapping("/tasks/{id}")
    public void deleteTask(@PathVariable Long id) { taskRepo.deleteById(id); }

    // --- DONATIONS ---
    @GetMapping("/donations")
    public List<Donation> getDonations() { return donationRepo.findAll(); }

    @PostMapping("/donate")
    public ResponseEntity<?> donate(@RequestBody Donation d) {
        donationRepo.save(d);
        Resource r = resourceRepo.findByCategory(d.getCategory());
        if (r != null) {
            r.setDonorContributions(r.getDonorContributions() + d.getAmount());
            r.setQuantity(r.getQuantity() + (int) (d.getAmount() / 10));
            resourceRepo.save(r);
        }
        audit.log("ANON", "DONATE", "Resource", d.getCategory());
        return ResponseEntity.ok(Map.of("success", true));
    }

    // --- ADMIN / AUDIT / EXPORT ---
    @GetMapping("/admin/users")
    public List<User> getUsers() { return userRepo.findAll(); }

    @PostMapping("/admin/users")
    public User addUser(@RequestBody User u) { return userRepo.save(u); }

    @GetMapping("/audit-logs")
    public List<AuditLog> getLogs() { return auditRepo.findAll(); }

    @GetMapping("/export")
    public void exportData(@RequestParam String resource, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=" + resource + ".csv");
        PrintWriter out = response.getWriter();
        if ("clients".equals(resource)) {
            out.println("ID,FirstName,LastName,Status");
            clientRepo.findAll().forEach(c -> out.println(c.getId()+","+c.getFirstName()+","+c.getLastName()+","+c.getStatus()));
        }
        else if ("users".equals(resource)) {
            out.println("ID,Username,Role");
            userRepo.findAll().forEach(u -> out.println(u.getId()+","+u.getUsername()+","+u.getRole()));
        }
    }
}