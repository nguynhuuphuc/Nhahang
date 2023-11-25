package com.example.nhahang.Models.Respones;

import java.util.Date;

public class RevenueResponse {
    private Date interval;
    private double total;
    private long quantity;
    private String quarter;
    private String year;
    private Date day;
    private char day_of_week_number;
    private Date date;
    private long quantity_forecast;
    private double total_forecast;

    public RevenueResponse() {
    }



    public RevenueResponse(Date day,char day_of_week_number,double total,long quantity){
        this.day = day;
        this.day_of_week_number = day_of_week_number;
        this.total = total;
        this.quantity = quantity;
    }



    public RevenueResponse(String year,double total, long quantity){
        this.year = year;
        this.total = total;
        this.quantity = quantity;
    }

    public RevenueResponse(double total, long quantity, String quarter) {
        this.total = total;
        this.quantity = quantity;
        this.quarter = quarter;
    }

    public RevenueResponse(Date interval, double total, long quantity) {
        this.interval = interval;
        this.total = total;
        this.quantity = quantity;
    }

    public Date getDate() {
        return date;
    }

    public long getQuantity_forecast() {
        return quantity_forecast;
    }

    public void setQuantity_forecast(long quantity_forecast) {
        this.quantity_forecast = quantity_forecast;
    }

    public double getTotal_forecast() {
        return total_forecast;
    }

    public void setTotal_forecast(double total_forecast) {
        this.total_forecast = total_forecast;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    public char getDay_of_week_number() {
        return day_of_week_number;
    }

    public void setDay_of_week_number(char day_of_week_number) {
        this.day_of_week_number = day_of_week_number;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getQuarter() {
        return quarter;
    }

    public void setQuarter(String quarter) {
        this.quarter = quarter;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public Date getInterval() {
        return interval;
    }

    public void setInterval(Date interval) {
        this.interval = interval;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
