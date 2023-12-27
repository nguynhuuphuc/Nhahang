package com.example.nhahang.Models;

import java.io.Serializable;
import java.util.Date;

public class OrderKitchenModel implements Serializable {
    public static final int ORDER_NOT_CONFIRM = 1;
    public static final int ORDER_CONFIRM = 2;
    public static final int ORDER_SERV = 3;
    public static final int ORDER_DELETED = 4;
    public static final int ORDER_KITCHEN_EMPTY = 5;


    private int order_id;
    private int status_id;
    private String status;
    private int table_id;
    private String table_name;
    private int quantity;
    private Date notify_time;
    private int order_item_id;

    public Date getNotify_time() {
        return notify_time;
    }

    public void setNotify_time(Date notify_time) {
        this.notify_time = notify_time;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getStatus_id() {
        return status_id;
    }

    public int getOrder_item_id() {
        return order_item_id;
    }

    public void setOrder_item_id(int order_item_id) {
        this.order_item_id = order_item_id;
    }

    public void setStatus_id(int status_id) {
        this.status_id = status_id;
        switch (this.status_id){
            case ORDER_NOT_CONFIRM:
                setStatus("Chưa xác nhận");
                return;
            case ORDER_CONFIRM:
                setStatus("Đã xác nhận");
                return;
            case ORDER_SERV:
                setStatus("Đã trả món");
        }

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTable_id() {
        return table_id;
    }

    public void setTable_id(int table_id) {
        this.table_id = table_id;
    }

    public String getTable_name() {
        return table_name;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
