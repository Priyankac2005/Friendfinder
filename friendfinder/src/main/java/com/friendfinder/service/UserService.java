package com.friendfinder.service;

import com.friendfinder.dto.LoginRequest;
import com.friendfinder.dto.RegisterRequest;
import com.friendfinder.dto.UserDto;
import com.friendfinder.model.Interest;
import com.friendfinder.model.User;
import com.friendfinder.repository.InterestRepository;
import com.friendfinder.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final InterestRepository interestRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, InterestRepository interestRepository, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.interestRepository = interestRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public String registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setVerified(true); // Automatically verify

        userRepository.save(user);

        return "Registration successful. You can now log in!";
    }

    @Transactional
    public String verifyOtp(String email, String otp) {
        return "Email verified successfully"; // Stubbed out
    }

    public UserDto login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            user.setLastLogin(java.time.LocalDateTime.now());
            user.setActivityScore(user.getActivityScore() + 1); // 1 point for logging in
            userRepository.save(user);
            return convertToDto(user);
        }

        throw new RuntimeException("Invalid credentials");
    }
    
    public UserDto getUserDto(Long userId) {
        User user = getUserById(userId);
        return convertToDto(user);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    @Transactional
    public UserDto addInterestToUser(Long userId, String interestName) {
        User user = getUserById(userId);
        
        Interest interest = interestRepository.findByName(interestName)
                .orElseGet(() -> interestRepository.save(new Interest(null, interestName)));

        user.getInterests().add(interest);
        userRepository.save(user);
        
        return convertToDto(user);
    }

    @Transactional
    public UserDto removeInterestFromUser(Long userId, String interestName) {
        User user = getUserById(userId);
        
        Interest interest = interestRepository.findByName(interestName)
                .orElseThrow(() -> new RuntimeException("Interest not found"));

        user.getInterests().remove(interest);
        userRepository.save(user);

        return convertToDto(user);
    }

    @Transactional
    public void addFriend(User user1, User user2) {
        user1.getFriends().add(user2);
        user2.getFriends().add(user1);
        userRepository.save(user1);
        userRepository.save(user2);
    }

    @Transactional
    public void removeFriend(Long userId, Long friendId) {
        User user1 = getUserById(userId);
        User user2 = getUserById(friendId);

        user1.getFriends().remove(user2);
        user2.getFriends().remove(user1);

        userRepository.save(user1);
        userRepository.save(user2);
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setProfilePicture(user.getProfilePicture());
        dto.setActivityScore(user.getActivityScore());
        dto.setLastLogin(user.getLastLogin());
        Set<String> interestNames = user.getInterests().stream()
                .map(Interest::getName)
                .collect(Collectors.toSet());
        dto.setInterests(interestNames);
        return dto;
    }
}
