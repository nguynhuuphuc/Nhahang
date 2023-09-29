package com.example.nhahang.Models.Requests;

public class CreatePasswordRequest {
    private String user_uid;
    private String password;

    public CreatePasswordRequest(String user_uid, String password) {
        this.user_uid = user_uid;
        this.password = password;
    }

    public String getUser_uid() {
        return user_uid;
    }

    public void setUser_uid(String user_uid) {
        this.user_uid = user_uid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
