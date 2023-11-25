package com.example.nhahang.Models.Requests;

import com.example.nhahang.Models.OrderItemModel;

import java.util.List;

public class JoinOrderTablesRequest {
    private int order1_id;
    private int order2_id;
    private String user_uid;
    private List<OrderItemModel> orderItemModels;


    public JoinOrderTablesRequest(int order1_id, int order2_id) {
        this.order1_id = order1_id;
        this.order2_id = order2_id;
    }

    public JoinOrderTablesRequest(int order1_id, int order2_id, List<OrderItemModel> orderItemModels) {
        this.order1_id = order1_id;
        this.order2_id = order2_id;
        this.orderItemModels = orderItemModels;
    }

    public String getUser_uid() {
        return user_uid;
    }

    public void setUser_uid(String user_uid) {
        this.user_uid = user_uid;
    }

    public List<OrderItemModel> getOrderItemModels() {
        return orderItemModels;
    }

    public void setOrderItemModels(List<OrderItemModel> orderItemModels) {
        this.orderItemModels = orderItemModels;
    }

    public int getOrder1_id() {
        return order1_id;
    }

    public void setOrder1_id(int order1_id) {
        this.order1_id = order1_id;
    }

    public int getOrder2_id() {
        return order2_id;
    }

    public void setOrder2_id(int order2_id) {
        this.order2_id = order2_id;
    }
}
