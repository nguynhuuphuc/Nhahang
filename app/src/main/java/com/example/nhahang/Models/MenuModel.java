package com.example.nhahang.Models;

import java.io.Serializable;

public class MenuModel implements Serializable {
    private String detail;
    private String name;
    private String img;
    private String price;
    private String quantity;
    private String type;
    private String documentId;
    private Boolean isDelete;
    int quantitySelected = 0;
    private boolean isAdded = false, isSelected = false;

    public MenuModel() {
    }

    public MenuModel(String detail, String name, String img, String price, String quantity, String type) {
        this.detail = detail;
        this.name = name;
        this.img = img;
        this.price = price;
        this.quantity = quantity;
        this.type = type;
    }

    public Boolean getDelete() {
        return isDelete;
    }

    public void setDelete(Boolean delete) {
        isDelete = delete;
    }

    public int getQuantitySelected() {
        return quantitySelected;
    }

    public void setQuantitySelected(int quantitySelected) {
        this.quantitySelected = quantitySelected;
    }

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean added) {
        isAdded = added;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
