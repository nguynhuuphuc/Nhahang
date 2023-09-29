package com.example.nhahang.Models.Requests;

public class PositionIdRequest {
    private String position_id;

    public PositionIdRequest(String position_id) {
        this.position_id = position_id;
    }

    public String getPosition_id() {
        return position_id;
    }

    public void setPosition_id(String position_id) {
        this.position_id = position_id;
    }
}
