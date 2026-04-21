package com.friendfinder.controller;

import com.friendfinder.dto.UserDto;
import com.friendfinder.service.MatchingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchingService matchingService;

    public MatchController(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @GetMapping("/suggest")
    public ResponseEntity<List<UserDto>> suggestFriends(@RequestParam Long userId) {
        return ResponseEntity.ok(matchingService.suggestFriends(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam Long userId, @RequestParam String keyword) {
        return ResponseEntity.ok(matchingService.searchUsers(userId, keyword));
    }
}
