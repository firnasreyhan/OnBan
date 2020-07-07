package com.andorid.go_bengkel.model;

public class UserAppModel {
    private String userKey;
    private String email;
    private String status;

    public UserAppModel(String userKey, String email, String status) {
        this.userKey = userKey;
        this.email = email;
        this.status = status;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
