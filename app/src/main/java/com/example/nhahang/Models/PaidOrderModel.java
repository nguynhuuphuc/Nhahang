package com.example.nhahang.Models;

import java.util.Date;

public class PaidOrderModel {
    private int order_id;
    private Date paid_time;
    private String created_by;
    private double total_amount;
    private int payment_method_id;
    private String payment_method_name;
    private int total_quantity;
    private String dateHeader;

    public PaidOrderModel() {
    }

    public PaidOrderModel(int order_id, Date paid_time, String created_by, double total_amount, int payment_method_id, String payment_method_name, int total_quantity) {
        this.order_id = order_id;
        this.paid_time = paid_time;
        this.created_by = created_by;
        this.total_amount = total_amount;
        this.payment_method_id = payment_method_id;
        this.payment_method_name = payment_method_name;
        this.total_quantity = total_quantity;
    }

    public String getDateHeader() {
        return dateHeader;
    }

    public void setDateHeader(String dateHeader) {
        this.dateHeader = dateHeader;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public Date getPaid_time() {
        return paid_time;
    }

    public void setPaid_time(Date paid_time) {
        this.paid_time = paid_time;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public double getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(double total_amount) {
        this.total_amount = total_amount;
    }

    public int getPayment_method_id() {
        return payment_method_id;
    }

    public void setPayment_method_id(int payment_method_id) {
        this.payment_method_id = payment_method_id;
    }

    public String getPayment_method_name() {
        return payment_method_name;
    }

    public void setPayment_method_name(String payment_method_name) {
        this.payment_method_name = payment_method_name;
    }

    public int getTotal_quantity() {
        return total_quantity;
    }

    public void setTotal_quantity(int total_quantity) {
        this.total_quantity = total_quantity;
    }
}
