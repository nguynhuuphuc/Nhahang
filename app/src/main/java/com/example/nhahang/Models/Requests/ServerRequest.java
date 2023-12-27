package com.example.nhahang.Models.Requests;

import com.example.nhahang.Models.OrderItemModel;
import com.example.nhahang.Models.OrderKitchenModel;

import java.time.LocalDate;
import java.util.Date;

public class ServerRequest {
    private String day;
    private String status;
    private int reservation_id;
    private Date update_time;
    private OrderKitchenModel orderKitchenModel;
    private OrderItemModel orderItemModel;
    private Date date;
    private int conversation_id;

    public ServerRequest(int conversation_id) {
        this.conversation_id = conversation_id;
    }

    public ServerRequest(Date date, int conversation_id) {
        this.date = date;
        this.conversation_id = conversation_id;
    }

    public ServerRequest(OrderItemModel orderItemModel) {
        this.orderItemModel = orderItemModel;
    }

    public ServerRequest(OrderKitchenModel orderKitchenModel) {
        this.orderKitchenModel = orderKitchenModel;
    }

    public ServerRequest(OrderKitchenModel orderKitchenModel, OrderItemModel orderItemModel) {
        this.orderKitchenModel = orderKitchenModel;
        this.orderItemModel = orderItemModel;
    }

    public ServerRequest(String status, int reservation_id) {
        this.status = status;
        this.reservation_id = reservation_id;
    }

    public ServerRequest(String status, int reservation_id, Date update_time) {
        this.status = status;
        this.reservation_id = reservation_id;
        this.update_time = update_time;
    }

    public ServerRequest(String day) {
        this.day = day;
    }
}
