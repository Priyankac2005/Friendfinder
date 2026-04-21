package com.friendfinder.service;

import com.friendfinder.model.FriendRequest;
import com.friendfinder.model.FriendRequestStatus;
import com.friendfinder.model.User;
import com.friendfinder.repository.FriendRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final UserService userService;
    private final EmailService emailService;

    public FriendRequestService(FriendRequestRepository friendRequestRepository, UserService userService, EmailService emailService) {
        this.friendRequestRepository = friendRequestRepository;
        this.userService = userService;
        this.emailService = emailService;
    }

    @Transactional
    public String sendRequest(Long senderId, Long receiverId) {
        if (senderId.equals(receiverId)) {
            throw new RuntimeException("Cannot send friend request to yourself");
        }

        User sender = userService.getUserById(senderId);
        User receiver = userService.getUserById(receiverId);

        if (sender.getFriends().contains(receiver)) {
            throw new RuntimeException("Already friends");
        }

        Optional<FriendRequest> existingRequest = friendRequestRepository.findBySenderAndReceiver(sender, receiver);
        if (existingRequest.isPresent()) {
            throw new RuntimeException("Friend request already sent");
        }

        FriendRequest request = new FriendRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setStatus(FriendRequestStatus.PENDING);
        
        friendRequestRepository.save(request);

        emailService.sendNotification(receiver.getEmail(), 
                "New Friend Request", 
                sender.getName() + " has sent you a friend request!");

        return "Friend request sent successfully";
    }

    @Transactional
    public String acceptRequest(Long requestId, Long receiverId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        if (!request.getReceiver().getId().equals(receiverId)) {
            throw new RuntimeException("Unauthorized to accept this request");
        }

        if (request.getStatus() != FriendRequestStatus.PENDING) {
            throw new RuntimeException("Request is not pending it is " + request.getStatus());
        }

        request.setStatus(FriendRequestStatus.ACCEPTED);
        friendRequestRepository.save(request);

        userService.addFriend(request.getSender(), request.getReceiver());

        emailService.sendNotification(request.getSender().getEmail(), 
                "Friend Request Accepted", 
                request.getReceiver().getName() + " accepted your friend request!");

        return "Friend request accepted";
    }

    @Transactional
    public String rejectRequest(Long requestId, Long receiverId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        if (!request.getReceiver().getId().equals(receiverId)) {
            throw new RuntimeException("Unauthorized to reject this request");
        }

        request.setStatus(FriendRequestStatus.REJECTED);
        friendRequestRepository.save(request);

        return "Friend request rejected";
    }

    public List<FriendRequest> getPendingRequestsForUser(Long userId) {
        User user = userService.getUserById(userId);
        return friendRequestRepository.findByReceiverAndStatus(user, FriendRequestStatus.PENDING);
    }
}
