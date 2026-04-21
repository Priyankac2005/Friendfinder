package com.friendfinder.dto;

import java.util.Set;

public class UserDto {
    private Long id;
    private String name;
    private String email;
    private Set<String> interests;
    private Set<String> sharedInterests;
    private int matchScore;
    private int mutualConnections;
    private String profilePicture;
    private java.time.LocalDateTime lastLogin;
    private int activityScore;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Set<String> getInterests() { return interests; }
    public void setInterests(Set<String> interests) { this.interests = interests; }
    public Set<String> getSharedInterests() { return sharedInterests; }
    public void setSharedInterests(Set<String> sharedInterests) { this.sharedInterests = sharedInterests; }
    public int getMatchScore() { return matchScore; }
    public void setMatchScore(int matchScore) { this.matchScore = matchScore; }
    public int getMutualConnections() { return mutualConnections; }
    public void setMutualConnections(int mutualConnections) { this.mutualConnections = mutualConnections; }
    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }
    public java.time.LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(java.time.LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
    public int getActivityScore() { return activityScore; }
    public void setActivityScore(int activityScore) { this.activityScore = activityScore; }
}
