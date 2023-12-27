package com.example.nhahang.Models;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class NotificationModel implements Serializable {
    private long id;
    private String message;
    private Date notify_time;
    private String created_by;
    private String read_by_users;
    private Message messageObject;
    private long notify_count;
    private String position_id;
    private boolean for_customer;

    public boolean isFor_customer() {
        return for_customer;
    }

    public void setFor_customer(boolean for_customer) {
        this.for_customer = for_customer;
    }

    private String activity;
    private int toCustomer;
    private ReservationModel reservation;
    private MessageModel conversationMessage;

    public MessageModel getConversationMessage() {
        return conversationMessage;
    }

    public void setConversationMessage(MessageModel conversationMessage) {
        this.conversationMessage = conversationMessage;
    }

    public String getPosition_id() {
        return position_id;
    }

    public void setPosition_id(String position_id) {
        this.position_id = position_id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    private String action;

    public String getPosition() {
        return position_id;
    }

    public void setPositionId(String position_id) {
        this.position_id = position_id;
    }

    public NotificationModel() {
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public ReservationModel getReservation() {
        return reservation;
    }

    public void setReservation(ReservationModel reservation) {
        this.reservation = reservation;
    }

    public NotificationModel(long id, String message, Date notify_time, String created_by, String read_by_users) {
        this.id = id;
        this.message = message;
        this.notify_time = notify_time;
        this.created_by = created_by;
        this.read_by_users = read_by_users;
        parseMessage();
    }

    public Message parseMessage(){
        if(this.message != null){
            Gson gson = new Gson();
            Message messageObject = gson.fromJson(message, Message.class);
            setMessageObject(messageObject);
            return messageObject;
        }
        return null;
    }

    public long getNotify_count() {
        return notify_count;
    }

    public void setNotify_count(long notify_count) {
        this.notify_count = notify_count;
    }

    public Message getMessageObject() {
        return messageObject;
    }

    public void setMessageObject(Message messageObject) {
        this.messageObject = messageObject;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {

        this.message = message;
        parseMessage();
    }

    public Date getNotify_time() {
        return notify_time;
    }

    public void setNotify_time(Date notify_time) {
        this.notify_time = notify_time;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getRead_by_users() {
        return read_by_users;
    }

    public void setRead_by_users(String read_by_users) {
        this.read_by_users = read_by_users;
    }

    public static class Message{
        private long order_id;
        private String status;
        private long old_table_id, new_table_id;
        private List<TableModel> updateTables;
        private List<OrderKitchenModel> updateKitchenCheckList;

        public Message(long order_id, String status) {
            this.order_id = order_id;
            this.status = status;
        }

        public List<OrderKitchenModel> getUpdateKitchenCheckList() {
            return updateKitchenCheckList;
        }

        public void setUpdateKitchenCheckList(List<OrderKitchenModel> updateKitchenCheckList) {
            this.updateKitchenCheckList = updateKitchenCheckList;
        }

        public long getOld_table_id() {
            return old_table_id;
        }

        public List<TableModel> getUpdateTables() {
            return updateTables;
        }

        public void setUpdateTables(List<TableModel> updateTables) {
            this.updateTables = updateTables;
        }

        public void setOld_table_id(long old_table_id) {
            this.old_table_id = old_table_id;
        }

        public long getNew_table_id() {
            return new_table_id;
        }

        public void setNew_table_id(long new_table_id) {
            this.new_table_id = new_table_id;
        }

        public long getOrder_id() {
            return order_id;
        }

        public void setOrder_id(long order_id) {
            this.order_id = order_id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
