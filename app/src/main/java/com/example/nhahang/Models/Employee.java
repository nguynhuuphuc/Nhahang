package com.example.nhahang.Models;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.Date;

public class Employee implements Serializable {
    private int employee_id;
    private String full_name;
    private Date date_of_birth;
    private String gender;
    private String address;
    private String email;
    private String user_uid;
    private String position_id;
    private String avatar;
    private String phone_number;
    private boolean is_delete;

    public Employee() {
    }

    public Employee(int employee_id, String full_name, Date date_of_birth, String gender, String address, String email, String user_uid, String position_id, String avatar) {
        this.employee_id = employee_id;
        this.full_name = full_name;
        this.date_of_birth = date_of_birth;
        this.gender = gender;
        this.address = address;
        this.email = email;
        this.user_uid = user_uid;
        this.position_id = position_id;
        this.avatar = avatar;
    }

    public Employee(int employee_id, String full_name, Date date_of_birth, String gender, String address, String email, String user_uid, String position_id, String avatar, String phone_number) {
        this.employee_id = employee_id;
        this.full_name = full_name;
        this.date_of_birth = date_of_birth;
        this.gender = gender;
        this.address = address;
        this.email = email;
        this.user_uid = user_uid;
        this.position_id = position_id;
        this.avatar = avatar;
        this.phone_number = phone_number;
    }

    public boolean isIs_delete() {
        return is_delete;
    }

    public void setIs_delete(boolean is_delete) {
        this.is_delete = is_delete;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public int getEmployee_id() {
        return employee_id;
    }

    public void setEmployee_id(int employee_id) {
        this.employee_id = employee_id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public Date getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(Date date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public JsonObject toJson() {
        Gson gson = new Gson();
        String jsonString = gson.toJson(this);
        return gson.fromJson(jsonString, JsonObject.class);
    }
}
