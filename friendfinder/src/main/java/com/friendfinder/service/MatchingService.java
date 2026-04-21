package com.friendfinder.service;

import com.friendfinder.dto.UserDto;
import com.friendfinder.model.Interest;
import com.friendfinder.model.User;
import com.friendfinder.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchingService {

    private final UserRepository userRepository;
    private final UserService userService;

    public MatchingService(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public List<UserDto> suggestFriends(Long userId) {
        User currentUser = userService.getUserById(userId);
        List<User> allUsers = userRepository.findAll();
        
        return rankAndConvertUsers(currentUser, allUsers);
    }

    public List<UserDto> searchUsers(Long userId, String keyword) {
        User currentUser = userService.getUserById(userId);
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return suggestFriends(userId);
        }
        
        String lowerKeyword = keyword.toLowerCase();
        
        List<User> searchResults = userRepository.findAll().stream()
            .filter(user -> 
                user.getName().toLowerCase().contains(lowerKeyword) ||
                user.getInterests().stream().anyMatch(i -> i.getName().toLowerCase().contains(lowerKeyword))
            )
            .collect(Collectors.toList());
            
        return rankAndConvertUsers(currentUser, searchResults);
    }
    
    private List<UserDto> rankAndConvertUsers(User currentUser, List<User> usersToRank) {
        Set<Interest> currentInterests = currentUser.getInterests();
        Set<User> currentFriends = currentUser.getFriends();

        List<UserDto> rankedUsers = new ArrayList<>();

        for (User user : usersToRank) {
            // Skip the current user, their existing friends, and blocked users
            if (user.getId().equals(currentUser.getId()) || currentFriends.contains(user) || currentUser.getBlockedUsers().contains(user)) {
                continue;
            }

            // Calculate intersection of interests
            Set<Interest> commonInterests = new HashSet<>(user.getInterests());
            commonInterests.retainAll(currentInterests);
            
            // Calculate mutual friends
            Set<User> mutualFriends = new HashSet<>(user.getFriends());
            mutualFriends.retainAll(currentFriends);

            int interestScore = commonInterests.size();
            int mutualFriendScore = mutualFriends.size();
            int totalScore = interestScore + mutualFriendScore;
            
            // If we are suggesting, we require at least 1 score point. If searching, we show them anyway.
            if (totalScore > 0 || usersToRank.size() < userRepository.count() - 1) {
                UserDto dto = convertToDto(user);
                
                Set<String> sharedInterestNames = commonInterests.stream()
                        .map(Interest::getName)
                        .collect(Collectors.toSet());
                
                dto.setSharedInterests(sharedInterestNames);
                dto.setMutualConnections(mutualFriendScore);
                dto.setMatchScore(totalScore);
                
                rankedUsers.add(dto);
            }
        }

        // Sort by highest score first
        rankedUsers.sort(Comparator.comparingInt(UserDto::getMatchScore).reversed());
        return rankedUsers;
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        Set<String> interestNames = user.getInterests().stream()
                .map(Interest::getName)
                .collect(Collectors.toSet());
        dto.setInterests(interestNames);
        return dto;
    }
}
