package com.friendfinder.service;

import com.friendfinder.model.User;
import com.friendfinder.repository.FriendRequestRepository;
import com.friendfinder.repository.MessageRepository;
import com.friendfinder.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final FriendRequestRepository friendRequestRepository;

    public AdminService(UserRepository userRepository, MessageRepository messageRepository, FriendRequestRepository friendRequestRepository) {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.friendRequestRepository = friendRequestRepository;
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. Delete all messages sent or received by the user
        messageRepository.deleteBySenderOrReceiver(user, user);

        // 2. Delete all friend requests sent or received by the user
        friendRequestRepository.deleteBySenderOrReceiver(user, user);

        // 3. Remove user from all other users' friends and blocked lists safely
        List<User> allUsers = userRepository.findAll();
        for (User other : allUsers) {
            if (!other.getId().equals(user.getId())) {
                other.getFriends().remove(user);
                other.getBlockedUsers().remove(user);
                userRepository.save(other);
            }
        }

        // 4. Clear the user's own mappings so JPA can delete the base row
        user.getFriends().clear();
        user.getBlockedUsers().clear();
        user.getInterests().clear();
        userRepository.save(user); // Flush clearing

        // 5. Delete the user
        userRepository.delete(user);
    }

    @Transactional
    public void deleteUserImage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setProfilePicture(null);
        userRepository.save(user);
    }
}
