package com.friendfinder.controller;

import com.friendfinder.model.User;
import com.friendfinder.repository.UserRepository;
import com.friendfinder.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final UserRepository userRepository;

    public AdminController(AdminService adminService, UserRepository userRepository) {
        this.adminService = adminService;
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<Map<String, Object>> users = userRepository.findAll().stream()
            .map(u -> Map.<String, Object>of(
                "id", u.getId(),
                "name", u.getName(),
                "email", u.getEmail(),
                "profilePicture", u.getProfilePicture() == null ? "" : u.getProfilePicture(),
                "reportCount", u.getReportCount()
            ))
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    @DeleteMapping("/users/{id}/image")
    public ResponseEntity<String> deleteUserImage(@PathVariable Long id) {
        adminService.deleteUserImage(id);
        return ResponseEntity.ok("Image deleted successfully");
    }
}
