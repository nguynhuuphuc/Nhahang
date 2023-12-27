package com.example.nhahang.Models;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Date;

public class ReservationModel implements Serializable {



    private int id;
    private int customer_id;
    private String customer_name;
    private Date reservation_time;
    private boolean in_progress;
    private int table_id;
    private int quantity_people;
    private String before_available;
    private String after_available;
    private String status;
    private Date update_time;
    private int order_id;




    public ReservationModel() {
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isIn_progress() {
        return in_progress;
    }

    public void setIn_progress(boolean in_progress) {
        this.in_progress = in_progress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public Date getReservation_time() {
        return reservation_time;
    }

    public void setReservation_time(Date reservation_time) {
        this.reservation_time = reservation_time;
    }

    public int getTable_id() {
        return table_id;
    }

    public void setTable_id(int table_id) {
        this.table_id = table_id;
    }

    public int getQuantity_people() {
        return quantity_people;
    }

    public void setQuantity_people(int quantity_people) {
        this.quantity_people = quantity_people;
    }

    public String getBefore_available() {
        return before_available;
    }

    public void setBefore_available(String before_available) {
        this.before_available = before_available;
    }

    public String getAfter_available() {
        return after_available;
    }

    public void setAfter_available(String after_available) {
        this.after_available = after_available;
    }


}
