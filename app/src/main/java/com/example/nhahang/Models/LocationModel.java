package com.example.nhahang.Models;

public class LocationModel {
    private int location_id;
    private String location_name;

    public LocationModel(int location_id, String location_name) {
        this.location_id = location_id;
        this.location_name = location_name;
    }

    public int getLocation_id() {
        return location_id;
    }

    public void setLocation_id(int location_id) {
        this.location_id = location_id;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }
}
