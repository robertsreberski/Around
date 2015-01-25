package com.StrapleGroup.around.database.base;

public class FriendsInfo extends ModelBase {
    private String login_friend;
    private double x_friend;
    private double y_friend;
    private byte[] profilePhoto;
    private int activities;
    private String status;
    private int score;
    private boolean poked;

    public boolean isPoked() {
        return poked;
    }

    public void setPoked(boolean poked) {
        this.poked = poked;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public byte[] getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(byte[] profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public void setLoginFriend(String login_friend) {
        this.login_friend = login_friend;
    }

    public String getLoginFriend() {
        return login_friend;
    }

    public void setXFriend(double x_friend) {
        this.x_friend = x_friend;
    }

    public double getXFriend() {
        return x_friend;
    }

    public void setYFriend(double y_friend) {
        this.y_friend = y_friend;
    }

    public double getYFriend() {
        return y_friend;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public int getActivities() {
        return activities;
    }

    public void setActivities(int activities) {
        this.activities = activities;
    }
}
