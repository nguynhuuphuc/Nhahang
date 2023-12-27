package com.example.nhahang.Models;

import java.io.Serializable;
import java.util.Date;

public class ConversationModel implements Serializable {
    private int id;
    private int customer_id;
    private String full_name;
    private String last_message;
    private String avatar;
    private int sender_id;
    private int receiver_id;
    private Date timestamp;
    private int un_read;

    public int getUn_read() {
        return un_read;
    }

    public void setUn_read(int un_read) {
        this.un_read = un_read;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getLastMessage() {
        return last_message;
    }

    public void setContent(String content) {
        this.last_message = content;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getSender_id() {
        return sender_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public int getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(int receiver_id) {
        this.receiver_id = receiver_id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
