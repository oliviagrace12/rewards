package com.example.rewards.domain;

import java.util.ArrayList;
import java.util.List;

public class Profile {

    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String department;
    private String position;
    private String story;
    private String bit46EncodedPhoto = "";
    private List<Reward> rewards = new ArrayList<>();
    private int pointsToAward;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public List<Reward> getRewards() {
        return rewards;
    }

    public void setRewards(List<Reward> rewards) {
        this.rewards = rewards;
    }

    public String getBit46EncodedPhoto() {
        return bit46EncodedPhoto;
    }

    public void setBit46EncodedPhoto(String bit46EncodedPhoto) {
        this.bit46EncodedPhoto = bit46EncodedPhoto;
    }

    public int getPointsToAward() {
        return pointsToAward;
    }

    public void setPointsToAward(int pointsToAward) {
        this.pointsToAward = pointsToAward;
    }

    public int getPointsAwarded() {
        return rewards.isEmpty() ? 0
                : rewards.stream().map(Reward::getAmount).reduce(Integer::sum).get();
    }
}
