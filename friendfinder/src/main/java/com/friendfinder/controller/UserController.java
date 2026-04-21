package com.friendfinder.controller;

import com.friendfinder.dto.UserDto;
import com.friendfinder.model.User;
import com.friendfinder.repository.UserRepository;
import com.friendfinder.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @PostMapping("/{userId}/upload-pfp")
    public ResponseEntity<?> uploadProfilePicture(@PathVariable Long userId, @RequestBody Map<String, String> payload) {
        User user = userService.getUserById(userId);
        user.setProfilePicture(payload.get("profilePicture"));
        user.setActivityScore(user.getActivityScore() + 10); // Reward for uploading PFP
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/block/{blockId}")
    public ResponseEntity<?> blockUser(@PathVariable Long userId, @PathVariable Long blockId) {
        User user = userService.getUserById(userId);
        User userToBlock = userService.getUserById(blockId);
        
        user.getBlockedUsers().add(userToBlock);
        user.getFriends().remove(userToBlock); // Remove from friends if blocked
        userToBlock.getFriends().remove(user); // Mutual remove
        
        userRepository.save(user);
        userRepository.save(userToBlock);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/report/{reportedId}")
    public ResponseEntity<?> reportUser(@PathVariable Long userId, @PathVariable Long reportedId) {
        User userToReport = userService.getUserById(reportedId);
        userToReport.setReportCount(userToReport.getReportCount() + 1);
        userRepository.save(userToReport);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<UserDto>> getLeaderboard() {
        List<UserDto> topUsers = userRepository.findAll().stream()
                .sorted(Comparator.comparingInt(User::getActivityScore).reversed())
                .limit(5)
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(topUsers);
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserInfo(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(convertToDto(user));
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setProfilePicture(user.getProfilePicture());
        dto.setActivityScore(user.getActivityScore());
        dto.setLastLogin(user.getLastLogin());
        return dto;
    }
}
