package com.example.nhahang.Models;

public class UserModel {
    String name;
    String phone;
    String email;
    String dateofbirth;
    String sex;
    String avatar;

    public UserModel() {
    }

    public UserModel(String name,String phone, String email, String dateofbirth, String sex, String avatar) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.dateofbirth = dateofbirth;
        this.sex = sex;
        this.avatar = avatar;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateofbirth() {
        return dateofbirth;
    }

    public void setDateofbirth(String dateofbirth) {
        this.dateofbirth = dateofbirth;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
