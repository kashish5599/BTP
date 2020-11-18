package com.project.btp.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    public String instiName;
    public String depName;
    public String email;
    public String phone;
    public String username;
    private String userId;
    private String displayName;
    private boolean isTeacher;

    public LoggedInUser() {

    }

    public LoggedInUser(String userId, String username, String phone, String instiName, String depName,
                        String email) {
        this.userId = userId;
        this.username = username;
        this.phone = phone;
        this.instiName = instiName;
        this.depName = depName;
        this.email = email;
        this.displayName = username;
        this.isTeacher = false;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean getIsTeacher() {
        return isTeacher;
    }

    public void setTeacher() {
        this.isTeacher = true;
    }
}