package com.example.nhahang.Models;

public class MenuModels {
    String detail;
    String name;
    String img;
    String price;
    String quantity;
    String type;

    public MenuModels() {
    }

    public MenuModels(String detail, String name, String img, String price, String quantity, String type) {
        this.detail = detail;
        this.name = name;
        this.img = img;
        this.price = price;
        this.quantity = quantity;
        this.type = type;
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
