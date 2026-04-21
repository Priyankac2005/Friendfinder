package com.friendfinder.controller;

import com.friendfinder.model.Message;
import com.friendfinder.model.User;
import com.friendfinder.repository.MessageRepository;
import com.friendfinder.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageRepository messageRepository;
    private final UserService userService;
    private final com.friendfinder.repository.UserRepository userRepository;

    public MessageController(MessageRepository messageRepository, UserService userService, com.friendfinder.repository.UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestParam Long senderId, @RequestParam Long receiverId, @RequestBody Map<String, String> payload) {
        User sender = userService.getUserById(senderId);
        User receiver = userService.getUserById(receiverId);

        String content = payload.get("content");
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Message content cannot be empty.");
        }

        Message message = new Message(sender, receiver, content);
        messageRepository.save(message);

        sender.setActivityScore(sender.getActivityScore() + 5); // Boost for sending msg
        userRepository.save(sender);

        return ResponseEntity.ok("Message sent successfully!");
    }

    @GetMapping("/history")
    public ResponseEntity<List<Map<String, Object>>> getHistory(@RequestParam Long user1Id, @RequestParam Long user2Id) {
        User user1 = userService.getUserById(user1Id);
        User user2 = userService.getUserById(user2Id);

        List<Message> messages = messageRepository.findConversation(user1, user2);

        List<Map<String, Object>> response = messages.stream()
                .map(m -> Map.<String, Object>of(
                        "id", m.getId(),
                        "senderId", m.getSender().getId(),
                        "receiverId", m.getReceiver().getId(),
                        "content", m.getContent(),
                        "timestamp", m.getTimestamp().toString()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
