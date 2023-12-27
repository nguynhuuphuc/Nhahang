package com.example.nhahang.Models;

import java.util.List;

public class OrderItemModel2 {
    private String status;
    private List<OrderItemModel> orderItemModels;

    public OrderItemModel2(String status, List<OrderItemModel> orderItemModels) {
        this.status = status;
        this.orderItemModels = orderItemModels;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrderItemModel> getOrderItemModels() {
        return orderItemModels;
    }

    public void setOrderItemModels(List<OrderItemModel> orderItemModels) {
        this.orderItemModels = orderItemModels;
    }
}
