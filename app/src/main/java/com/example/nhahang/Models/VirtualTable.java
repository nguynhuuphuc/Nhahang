package com.example.nhahang.Models;

public class VirtualTable {
    private String documentId;
    private String quantity;
    private String note;
    private boolean discountUnit;
    private String valueDiscount = "";
    private String totalPriceProduct;

    public VirtualTable() {
    }

    public VirtualTable(String documentId) {
        this.documentId = documentId;
    }

    public VirtualTable(String documentId, String quantity) {
        this.documentId = documentId;
        this.quantity = quantity;
    }
    public VirtualTable(String documentId, String quantity,String totalPriceProduct) {
        this.documentId = documentId;
        this.quantity = quantity;
        this.totalPriceProduct = totalPriceProduct;
    }

    public VirtualTable(String documentId, String quantity, String note, boolean discountUnit, String valueDiscount, String totalPriceProduct) {
        this.documentId = documentId;
        this.quantity = quantity;
        this.note = note;
        this.discountUnit = discountUnit;
        this.valueDiscount = valueDiscount;
        this.totalPriceProduct = totalPriceProduct;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isDiscountUnit() {
        return discountUnit;
    }

    public void setDiscountUnit(boolean discountUnit) {
        this.discountUnit = discountUnit;
    }

    public String getValueDiscount() {
        return valueDiscount;
    }

    public void setValueDiscount(String valueDiscount) {
        this.valueDiscount = valueDiscount;
    }

    public String getTotalPriceProduct() {
        return totalPriceProduct;
    }

    public void setTotalPriceProduct(String totalPriceProduct) {
        this.totalPriceProduct = totalPriceProduct;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
