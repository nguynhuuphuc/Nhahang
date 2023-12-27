package com.example.nhahang.Models;

public class EventKitchenMess {
    private int order_item;
    private String action;

    public EventKitchenMess(int order_item, String action) {
        this.order_item = order_item;
        this.action = action;
    }

    public int getOrder_item() {
        return order_item;
    }

    public void setOrder_item(int order_item) {
        this.order_item = order_item;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
