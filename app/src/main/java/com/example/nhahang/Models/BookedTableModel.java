package com.example.nhahang.Models;

public class BookedTableModel {
    String idTable;
    String idCustomer;
    String idStaff;
    String time;
    String quantityProduct;
    String totalPrice;

    public BookedTableModel() {
    }

    public BookedTableModel(String idTable, String idCustomer, String idStaff, String time, String quantityProduct, String totalPrice) {
        this.idTable = idTable;
        this.idCustomer = idCustomer;
        this.idStaff = idStaff;
        this.time = time;
        this.quantityProduct = quantityProduct;
        this.totalPrice = totalPrice;
    }

    public String getIdTable() {
        return idTable;
    }

    public void setIdTable(String idTable) {
        this.idTable = idTable;
    }

    public String getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(String idCustomer) {
        this.idCustomer = idCustomer;
    }

    public String getIdStaff() {
        return idStaff;
    }

    public void setIdStaff(String idStaff) {
        this.idStaff = idStaff;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getQuantityProduct() {
        return quantityProduct;
    }

    public void setQuantityProduct(String quantityProduct) {
        this.quantityProduct = quantityProduct;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }
}
