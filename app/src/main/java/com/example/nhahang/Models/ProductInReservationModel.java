package com.example.nhahang.Models;

public class ProductInReservationModel {
    private boolean productDiscountUnint;
    private String productDiscountValue;
    private String productId;
    private String productNote;
    private String productQuantity;
    private String productTotalPrice;
    private String documentId;

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public ProductInReservationModel() {
    }

    public ProductInReservationModel(boolean productDiscountUnint, String productDiscountValue, String productId, String productNote, String productQuantity, String productTotalPrice) {
        this.productDiscountUnint = productDiscountUnint;
        this.productDiscountValue = productDiscountValue;
        this.productId = productId;
        this.productNote = productNote;
        this.productQuantity = productQuantity;
        this.productTotalPrice = productTotalPrice;
    }

    public boolean isProductDiscountUnint() {
        return productDiscountUnint;
    }

    public void setProductDiscountUnint(boolean productDiscountUnint) {
        this.productDiscountUnint = productDiscountUnint;
    }

    public String getProductDiscountValue() {
        return productDiscountValue;
    }

    public void setProductDiscountValue(String productDiscountValue) {
        this.productDiscountValue = productDiscountValue;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductNote() {
        return productNote;
    }

    public void setProductNote(String productNote) {
        this.productNote = productNote;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getProductTotalPrice() {
        return productTotalPrice;
    }

    public void setProductTotalPrice(String productTotalPrice) {
        this.productTotalPrice = productTotalPrice;
    }
}
