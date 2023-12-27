package com.example.nhahang.Models.Requests;

import java.util.Date;

public class OrderKitchenRequest {
    private int order_id;
    private Date notify_time;
    private int status_id;

    public OrderKitchenRequest() {
    }

    public OrderKitchenRequest(int order_id, Date notify_time, int status_id) {
        this.order_id = order_id;
        this.notify_time = notify_time;
        this.status_id = status_id;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public Date getNotify_time() {
        return notify_time;
    }

    public void setNotify_time(Date notify_time) {
        this.notify_time = notify_time;
    }

    public int getStatus_id() {
        return status_id;
    }

    public void setStatus_id(int status_id) {
        this.status_id = status_id;
    }
}
