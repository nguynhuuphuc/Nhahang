package com.example.nhahang.Models.Requests;

import java.util.Date;

public class NotificationRequest {
    private Date time_start;

    public NotificationRequest(Date time_start) {
        this.time_start = time_start;
    }

    public Date getTime_start() {
        return time_start;
    }

    public void setTime_start(Date time_start) {
        this.time_start = time_start;
    }
}
