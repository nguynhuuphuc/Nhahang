package com.example.nhahang.Models.Requests;

import com.example.nhahang.Models.OrderItemModel;
import com.example.nhahang.Models.OrderModel;

import java.util.List;

public class OrderRequest {
    private List<OrderItemModel> orderItemModels;
    private String user_uid;
    private int table_id;
    private double total_amount;
    private int order_id;
    private OrderModel orderModel;
    private boolean is_paid;
    private int payment_method_id;

    public OrderRequest(){}

    public OrderRequest(int order_id, boolean is_paid) {
        this.order_id = order_id;
        this.is_paid = is_paid;
    }

    public int getPayment_method_id() {
        return payment_method_id;
    }

    public void setPayment_method_id(int payment_method_id) {
        this.payment_method_id = payment_method_id;
    }

    public boolean isIs_paid() {
        return is_paid;
    }

    public void setIs_paid(boolean is_paid) {
        this.is_paid = is_paid;
    }

    public OrderRequest(OrderModel orderModel) {
        this.orderModel = orderModel;
    }

    public OrderModel getOrderModel() {
        return orderModel;
    }

    public void setOrderModel(OrderModel orderModel) {
        this.orderModel = orderModel;
    }

    public OrderRequest(List<OrderItemModel> orderItemModels, String user_uid) {
        this.orderItemModels = orderItemModels;
        this.user_uid = user_uid;
    }

    public void setTable_id(int table_id) {
        this.table_id = table_id;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getTable_id() {
        return table_id;
    }

    public OrderRequest(List<OrderItemModel> orderItemModels, String user_uid, int table_id) {
        this.orderItemModels = orderItemModels;
        this.user_uid = user_uid;
        this.table_id = table_id;
    }
    public OrderRequest(List<OrderItemModel> orderItemModels, double total_amount) {
        this.orderItemModels = orderItemModels;
        this.total_amount = total_amount;
    }

    public OrderRequest(List<OrderItemModel> orderItemModels, String user_uid, int table_id, double total_amount) {
        this.orderItemModels = orderItemModels;
        this.user_uid = user_uid;
        this.table_id = table_id;
        this.total_amount = total_amount;
    }

    public double getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(double total_amount) {
        this.total_amount = total_amount;
    }

    public OrderRequest(int table_id, int order_id){
        this.table_id = table_id;
        this.order_id = order_id;
    }

    public List<OrderItemModel> getOrderItemModels() {
        return orderItemModels;
    }

    public void setOrderItemModels(List<OrderItemModel> orderItemModels) {
        this.orderItemModels = orderItemModels;
    }

    public String getUser_uid() {
        return user_uid;
    }

    public void setUser_uid(String user_uid) {
        this.user_uid = user_uid;
    }
}
