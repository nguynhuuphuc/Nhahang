package com.example.nhahang.Models;

import java.io.Serializable;
import java.util.Date;

public class TableModel implements Serializable {
    String documentId;
    String id;
    String description;
    String status;
    String location;

    private int table_id;
    private int capacity;
    private boolean is_occupied;
    private int location_id;
    private String table_name;
    private Date order_date;
    private double total_amount;
    private String created_by;
    private int order_id;


    public TableModel() {
    }

    public TableModel(int table_id, int capacity, boolean is_occupied, int location_id, String table_name, Date order_date, double total_amount) {
        this.table_id = table_id;
        this.capacity = capacity;
        this.is_occupied = is_occupied;
        this.location_id = location_id;
        this.table_name = table_name;
        this.order_date = order_date;
        this.total_amount = total_amount;
    }

    public TableModel(int table_id, int capacity, boolean is_occupied, int location_id, String table_name, Date order_date, double total_amount, int order_id) {
        this.table_id = table_id;
        this.capacity = capacity;
        this.is_occupied = is_occupied;
        this.location_id = location_id;
        this.table_name = table_name;
        this.order_date = order_date;
        this.total_amount = total_amount;
        this.order_id = order_id;
    }

    public TableModel(int table_id, int capacity, boolean is_occupied, int location_id, String table_name, Date order_date, double total_amount, String created_by) {
        this.table_id = table_id;
        this.capacity = capacity;
        this.is_occupied = is_occupied;
        this.location_id = location_id;
        this.table_name = table_name;
        this.order_date = order_date;
        this.total_amount = total_amount;
        this.created_by = created_by;
    }
    public TableModel(int table_id){
        this.table_id = table_id;
    }


    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public TableModel(int table_id, int capacity, boolean is_occupied, int location_id, String table_name) {
        this.table_id = table_id;
        this.capacity = capacity;
        this.is_occupied = is_occupied;
        this.location_id = location_id;
        this.table_name = table_name;
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

    public int getTable_id() {
        return table_id;
    }

    public void setTable_id(int table_id) {
        this.table_id = table_id;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean is_occupied() {
        return is_occupied;
    }

    public void setIs_occupied(boolean is_occupied) {
        this.is_occupied = is_occupied;
    }

    public int getLocation_id() {
        return location_id;
    }

    public void setLocation_id(int location_id) {
        this.location_id = location_id;
    }

    public String getTable_name() {
        return table_name;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
