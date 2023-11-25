package com.example.nhahang.Models;

import java.util.Date;

public class PaymentMethodModel {
    private int payment_method_id;
    private String payment_method_name;
    private String description;
    private Date created_at;

    public PaymentMethodModel(int payment_method_id, String payment_method_name, String description, Date created_at) {
        this.payment_method_id = payment_method_id;
        this.payment_method_name = payment_method_name;
        this.description = description;
        this.created_at = created_at;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
}
