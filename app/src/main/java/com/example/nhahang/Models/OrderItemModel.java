package com.example.nhahang.Models;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;

public class OrderItemModel implements Serializable,Comparable<OrderItemModel>,Cloneable  {
    private int order_id;
    private String menu_item_id;
    private int quantity;
    private double item_price;
    private Date order_time;
    private double discount_amount;
    private int discount_percentage;
    private String note;
    private boolean isEnableSplit;
    private int quantitySplit;
    private int quantity_confirm;
    private int quantity_serv;
    private int quantity_notify;
    private Date notify_time;
    private String status;
    private int status_id;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getStatus_id() {
        return status_id;
    }

    public void setStatus_id(int status_id) {
        this.status_id = status_id;
    }

    public Date getNotify_time() {
        return notify_time;
    }

    public void setNotify_time(Date notify_time) {
        this.notify_time = notify_time;
    }

    public OrderItemModel() {
    }

    public OrderItemModel(int order_id, String menu_item_id, int quantity, double item_price, Date order_time, double discount_amount, int discount_percentage, String note) {
        this.order_id = order_id;
        this.menu_item_id = menu_item_id;
        this.quantity = quantity;
        this.item_price = item_price;
        this.order_time = order_time;
        this.discount_amount = discount_amount;
        this.discount_percentage = discount_percentage;
        this.note = note;
    }

    public int getQuantitySplit() {
        return quantitySplit;
    }

    public void setQuantitySplit(int quantitySplit) {
        this.quantitySplit = quantitySplit;
    }

    public boolean isEnableSplit() {
        return isEnableSplit;
    }

    public void setEnableSplit(boolean enableSplit) {
        isEnableSplit = enableSplit;
        quantitySplit = 0;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public String getMenu_item_id() {
        return menu_item_id;
    }

    public void setMenu_item_id(String menu_item_id) {
        this.menu_item_id = menu_item_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getItem_price() {
        return item_price;
    }

    public void setItem_price(double item_price) {
        this.item_price = item_price;
    }

    public Date getOrder_time() {
        return order_time;
    }

    public void setOrder_time(Date order_time) {
        this.order_time = order_time;
    }

    public double getDiscount_amount() {
        return discount_amount;
    }

    public void setDiscount_amount(double discount_amount) {
        this.discount_amount = discount_amount;
        this.discount_percentage = 0;
    }

    public int getDiscount_percentage() {
        return discount_percentage;
    }

    public void setDiscount_percentage(int discount_percentage) {
        this.discount_percentage = discount_percentage;
        this.discount_amount = 0;
    }

    public int getQuantity_notify() {
        return quantity_notify;
    }

    public void setQuantity_notify(int quantity_notify) {
        this.quantity_notify = quantity_notify;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setNoneDiscount(){
        this.discount_amount = 0;
        this.discount_percentage = 0;
    }

    public int getQuantity_confirm() {
        return quantity_confirm;
    }

    public void setQuantity_confirm(int quantity_confirm) {
        this.quantity_confirm = quantity_confirm;
    }

    public int getQuantity_serv() {
        return quantity_serv;
    }

    public void setQuantity_serv(int quantity_serv) {
        this.quantity_serv = quantity_serv;
    }

    public OrderItemModel clone1(){
        return new OrderItemModel(this.getOrder_id(),this.getMenu_item_id(),this.getQuantity(),this.getItem_price(),this.getOrder_time(),this
                .getDiscount_amount(),this.getDiscount_percentage(),this.getNote());
    }
    @NonNull
    @Override
    public OrderItemModel clone() throws CloneNotSupportedException {
        return (OrderItemModel) super.clone();
    }

    public boolean isChange(OrderItemModel model){
        if(this.discount_percentage != model.getDiscount_percentage())
            return true;
        if(this.discount_amount != model.getDiscount_amount())
            return true;
        if(this.note == null && model.getNote() != null){
            return true;
        }
        if(this.note!= null && !this.note.equals(model.getNote()))
            return true;
        if(this.quantity != model.getQuantity())
            return true;

        return false;
    }

    @Override
    public int compareTo(OrderItemModel o) {
        return this.order_time.compareTo(o.order_time);
    }
}
