package com.example.nhahang.Models.Requests;

import java.util.Date;

public class RevenueRequest {
    private String interval;
    private String day_start, day_end;


    public RevenueRequest() {
    }

    public RevenueRequest(String interval) {
        this.interval = interval;

    }

    public RevenueRequest(String day_start, String day_end) {
        this.day_start = day_start;
        this.day_end = day_end;
    }

    public String getDay_start() {
        return day_start;
    }

    public void setDay_start(String day_start) {
        this.day_start = day_start;
    }

    public String getDay_end() {
        return day_end;
    }

    public void setDay_end(String day_end) {
        this.day_end = day_end;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

}
