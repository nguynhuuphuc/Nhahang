package com.example.nhahang.Models.Respones;

public class LoginResponse {
    private String user_uid;
    private boolean is_verify;

    public LoginResponse(String user_uid) {
        this.user_uid = user_uid;
    }

    public LoginResponse(String user_uid, boolean is_verify) {
        this.user_uid = user_uid;
        this.is_verify = is_verify;
    }

    public boolean isIs_verify() {
        return is_verify;
    }

    public void setIs_verify(boolean is_verify) {
        this.is_verify = is_verify;
    }

    public String getUser_uid() {
        return user_uid;
    }

    public void setUser_uid(String user_uid) {
        this.user_uid = user_uid;
    }
}
