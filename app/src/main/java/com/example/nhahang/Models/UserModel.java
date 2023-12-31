package com.example.nhahang.Models;

import java.io.Serializable;

public class UserModel implements Serializable {
    String name;
    String phone;
    String email;
    String dateofbirth;
    String sex;
    String avatar;
    String position;
    String documentId;

    public UserModel() {
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public UserModel(String name, String phone, String email, String dateofbirth, String sex, String avatar, String position) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.dateofbirth = dateofbirth;
        this.sex = sex;
        this.avatar = avatar;
        this.position = position;
    }

    public UserModel(String name, String phone, String email, String dateofbirth, String sex, String avatar) {
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
