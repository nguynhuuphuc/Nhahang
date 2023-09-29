package com.example.nhahang.Models;

public class AccountModel {
    private String phone_number;
    private String password;
    private String user_uid;
    private String position_id;
    private boolean is_verify;

    public AccountModel(String phone_number, String password, String user_uid, String position_id, boolean is_verify) {
        this.phone_number = phone_number;
        this.password = password;
        this.user_uid = user_uid;
        this.position_id = position_id;
        this.is_verify = is_verify;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser_uid() {
        return user_uid;
    }

    public void setUser_uid(String user_uid) {
        this.user_uid = user_uid;
    }

    public String getPosition_id() {
        return position_id;
    }

    public void setPosition_id(String position_id) {
        this.position_id = position_id;
    }

    public boolean isIs_verify() {
        return is_verify;
    }

    public void setIs_verify(boolean is_verify) {
        this.is_verify = is_verify;
    }
}
