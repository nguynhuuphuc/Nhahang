package com.example.nhahang.Models;

import java.io.Serializable;
import java.util.Date;

public class OrderModel implements Serializable {
    private int order_id;
    private Date order_date;
    private double total_amount;
    private String created_by;
    private String discount_code;
    private double discount_amount;
    private int discount_percent;
    private int table_id;


    public OrderModel() {
    }

    public OrderModel(int order_id, Date order_date, double total_amount, String created_by, int table_id) {
        this.order_id = order_id;
        this.order_date = order_date;
        this.total_amount = total_amount;
        this.created_by = created_by;
        this.table_id = table_id;
    }



    public OrderModel(int order_id, Date order_date, double total_amount, String created_by, String discount_code, double discount_amount, int discount_percent, int table_id) {
        this.order_id = order_id;
        this.order_date = order_date;
        this.total_amount = total_amount;
        this.created_by = created_by;
        this.discount_code = discount_code;
        this.discount_amount = discount_amount;
        this.discount_percent = discount_percent;
        this.table_id = table_id;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public Date getOrder_date() {
        return order_date;
    }

    public void setOrder_date(Date order_date) {
        this.order_date = order_date;
    }

    public double getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(double total_amount) {
        this.total_amount = total_amount;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getDiscount_code() {
        return discount_code;
    }

    public void setDiscount_code(String discount_code) {
        this.discount_code = discount_code;
    }

    public double getDiscount_amount() {
        return discount_amount;
    }

    public void setDiscount_amount(double discount_amount) {
        this.discount_amount = discount_amount;
    }

    public int getDiscount_percent() {
        return discount_percent;
    }

    public void setDiscount_percent(int discount_percent) {
        this.discount_percent = discount_percent;
    }

    public int getTable_id() {
        return table_id;
    }

    public void setTable_id(int table_id) {
        this.table_id = table_id;
    }

    public void update(OrderModel orderModel){
        this.discount_amount = orderModel.discount_amount;
        this.discount_percent = orderModel.discount_percent;
        this.discount_code = orderModel.discount_code;
    }
}
