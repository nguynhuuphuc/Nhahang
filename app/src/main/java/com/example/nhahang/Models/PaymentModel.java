package com.example.nhahang.Models;

public class PaymentModel {
    private String discountUnit;
    private String discountValue;
    private String customerPay;
    private String tableId;
    private String timeCheckIn;
    private String timeCheckOut;
    private String price;
    private String theChange;
    private String paymentUnit;
    private String totalPrice;
    private String documentId;


    public PaymentModel() {
    }

    public PaymentModel(String discountUnit, String discountValue, String customerPay, String tableId, String timeCheckIn, String timeCheckOut, String price, String theChange, String paymentUnit, String totalPrice) {
        this.discountUnit = discountUnit;
        this.discountValue = discountValue;
        this.customerPay = customerPay;
        this.tableId = tableId;
        this.timeCheckIn = timeCheckIn;
        this.timeCheckOut = timeCheckOut;
        this.price = price;
        this.theChange = theChange;
        this.paymentUnit = paymentUnit;
        this.totalPrice = totalPrice;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTheChange() {
        return theChange;
    }

    public void setTheChange(String theChange) {
        this.theChange = theChange;
    }

    public String getPaymentUnit() {
        return paymentUnit;
    }

    public void setPaymentUnit(String paymentUnit) {
        this.paymentUnit = paymentUnit;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public PaymentModel(String discountUnit, String discountValue, String customerPay, String tableId, String timeCheckIn, String timeCheckOut, String price, String theChange) {
        this.discountUnit = discountUnit;
        this.discountValue = discountValue;
        this.customerPay = customerPay;
        this.tableId = tableId;
        this.timeCheckIn = timeCheckIn;
        this.timeCheckOut = timeCheckOut;
        this.price = price;
        this.theChange = theChange;
    }

    public PaymentModel(String discountUnit, String discountValue, String customerPay, String tableId, String timeCheckIn, String timeCheckOut) {
        this.discountUnit = discountUnit;
        this.discountValue = discountValue;
        this.customerPay = customerPay;
        this.tableId = tableId;
        this.timeCheckIn = timeCheckIn;
        this.timeCheckOut = timeCheckOut;
    }

    public String getTimeCheckIn() {
        return timeCheckIn;
    }

    public void setTimeCheckIn(String timeCheckIn) {
        this.timeCheckIn = timeCheckIn;
    }

    public String getTimeCheckOut() {
        return timeCheckOut;
    }

    public void setTimeCheckOut(String timeCheckOut) {
        this.timeCheckOut = timeCheckOut;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getDiscountUnit() {
        return discountUnit;
    }

    public void setDiscountUnit(String discountUnit) {
        this.discountUnit = discountUnit;
    }

    public String getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(String discountValue) {
        this.discountValue = discountValue;
    }

    public String getCustomerPay() {
        return customerPay;
    }

    public void setCustomerPay(String customerPay) {
        this.customerPay = customerPay;
    }
}
