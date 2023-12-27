package com.example.nhahang.Models.Respones;

public class ServerResponse {
    private String message;
    private boolean is_occupied;
    private boolean is_confirm;
    private boolean is_servdish;

    public String getMessage() {
        return message;
    }

    public boolean isOccupied() {
        return is_occupied;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setIs_occupied(boolean is_occupied) {
        this.is_occupied = is_occupied;
    }

    public void setIs_confirm(boolean is_confirm) {
        this.is_confirm = is_confirm;
    }

    public void setIs_servDish(boolean is_servDish) {
        this.is_servdish = is_servDish;
    }

    public boolean isIs_servDish() {
        return is_servdish;
    }

    public boolean isIs_occupied() {
        return is_occupied;
    }

    public boolean isIs_confirm() {
        return is_confirm;
    }
}
