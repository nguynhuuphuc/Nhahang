package com.example.nhahang.Models;

import java.util.Date;

public class MessageModel extends CustomerModel {
    private int id;
    private int conversation_id;
    private int sender_id;
    private String sender_name;
    private String receiver_name;
    private int receiver_id;
    private String content;
    private Date timestamp;
    private boolean is_read;

    public boolean isIs_read() {
        return is_read;
    }

    public void setIs_read(boolean is_read) {
        this.is_read = is_read;
    }

    public MessageModel(int sender_id, String content) {
        this.sender_id = sender_id;
        this.content = content;
        this.timestamp = new Date();
    }

    public MessageModel(int sender_id, String content,Date timestamp) {
        this.sender_id = sender_id;
        this.content = content;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public String getReceiver_name() {
        return receiver_name;
    }

    public void setReceiver_name(String receiver_name) {
        this.receiver_name = receiver_name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getConversation_id() {
        return conversation_id;
    }

    public void setConversation_id(int conversation_id) {
        this.conversation_id = conversation_id;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
