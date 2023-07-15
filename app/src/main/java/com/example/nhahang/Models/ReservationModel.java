package com.example.nhahang.Models;

public class ReservationModel {
    private String currentDate;
    private String currentTime;
    private String customerId;
    private String staffId;
    private String totalPrice;
    private String totalQuantity;


    public ReservationModel() {
    }




    public ReservationModel(String currentDate, String currentTime, String customerId, String staffId, String totalPrice, String totalQuantity) {
        this.currentDate = currentDate;
        this.currentTime = currentTime;
        this.customerId = customerId;
        this.staffId = staffId;
        this.totalPrice = totalPrice;
        this.totalQuantity = totalQuantity;
    }


    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(String totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
}
