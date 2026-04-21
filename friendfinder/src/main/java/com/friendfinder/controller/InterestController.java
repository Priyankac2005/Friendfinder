package com.friendfinder.controller;

import com.friendfinder.dto.UserDto;
import com.friendfinder.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interests")
public class InterestController {

    private final UserService userService;

    public InterestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/add")
    public ResponseEntity<UserDto> addInterest(@RequestParam Long userId, @RequestParam String interestName) {
        return ResponseEntity.ok(userService.addInterestToUser(userId, interestName));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<UserDto> removeInterest(@RequestParam Long userId, @RequestParam String interestName) {
        return ResponseEntity.ok(userService.removeInterestFromUser(userId, interestName));
    }
}
