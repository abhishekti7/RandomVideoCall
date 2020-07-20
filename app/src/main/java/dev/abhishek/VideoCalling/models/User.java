package dev.abhishek.VideoCalling.models;

public class User {

    private String userName;
    private String userId;
    private String userEmail;
    private String userProfileImg;
    private long created_on;


    public User(String userId, String userName, String userEmail, String userProfileImg, long created_on) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userProfileImg = userProfileImg;
        this.created_on = created_on;
    }

    public long getCreated_on() {
        return created_on;
    }

    public void setCreated_on(long created_on) {
        this.created_on = created_on;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserProfileImg() {
        return userProfileImg;
    }

    public void setUserProfileImg(String userProfileImg) {
        this.userProfileImg = userProfileImg;
    }
}
