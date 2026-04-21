package com.friendfinder.controller;

import com.friendfinder.model.FriendRequest;
import com.friendfinder.model.User;
import com.friendfinder.service.FriendRequestService;
import com.friendfinder.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/friends")
public class FriendController {

    private final FriendRequestService friendRequestService;
    private final UserService userService;

    public FriendController(FriendRequestService friendRequestService, UserService userService) {
        this.friendRequestService = friendRequestService;
        this.userService = userService;
    }

    @PostMapping("/request")
    public ResponseEntity<String> sendRequest(@RequestParam Long senderId, @RequestParam Long receiverId) {
        return ResponseEntity.ok(friendRequestService.sendRequest(senderId, receiverId));
    }

    @PostMapping("/accept")
    public ResponseEntity<String> acceptRequest(@RequestParam Long requestId, @RequestParam Long receiverId) {
        return ResponseEntity.ok(friendRequestService.acceptRequest(requestId, receiverId));
    }

    @PostMapping("/reject")
    public ResponseEntity<String> rejectRequest(@RequestParam Long requestId, @RequestParam Long receiverId) {
        return ResponseEntity.ok(friendRequestService.rejectRequest(requestId, receiverId));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<java.util.Map<String, Object>>> getPendingRequests(@RequestParam Long userId) {
        List<FriendRequest> requests = friendRequestService.getPendingRequestsForUser(userId);
        List<java.util.Map<String, Object>> safeRequests = requests.stream()
                .map(r -> java.util.Map.<String, Object>of(
                        "id", r.getId(),
                        "sender", java.util.Map.of("id", r.getSender().getId(), "name", r.getSender().getName())
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(safeRequests);
    }

    @GetMapping("/list")
    public ResponseEntity<List<java.util.Map<String, Object>>> listFriends(@RequestParam Long userId) {
        User user = userService.getUserById(userId);
        List<java.util.Map<String, Object>> friends = user.getFriends().stream()
                .map(f -> java.util.Map.<String, Object>of("id", f.getId(), "name", f.getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(friends);
    }
    
    @DeleteMapping("/remove")
    public ResponseEntity<String> removeFriend(@RequestParam Long userId, @RequestParam Long friendId) {
        userService.removeFriend(userId, friendId);
        return ResponseEntity.ok("Friend removed successfully");
    }
}
