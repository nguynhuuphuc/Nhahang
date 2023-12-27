package com.example.nhahang.Models;

import java.util.List;

public class MessageEvent {
    private NotificationModel notificationModel;
    private String activitySimpleName;
    private List<TableModel> changeTables;
    private List<ReservationModel> reservationModels;
    private String toActivity;

    public MessageEvent() {
    }

    public MessageEvent(String activitySimpleName) {
        this.activitySimpleName = activitySimpleName;
    }

    public MessageEvent(NotificationModel notificationModel, String activitySimpleName) {
        this.notificationModel = notificationModel;
        this.activitySimpleName = activitySimpleName;
    }

    public String getToActivity() {
        return toActivity;
    }

    public void setToActivity(String toActivity) {
        this.toActivity = toActivity;
    }

    public List<ReservationModel> getReservationModels() {
        return reservationModels;
    }

    public void setReservationModels(List<ReservationModel> reservationModels) {
        this.reservationModels = reservationModels;
    }

    public MessageEvent(List<TableModel> changeTables, String activitySimpleName) {
        this.changeTables = changeTables;
        this.activitySimpleName = activitySimpleName;
    }

    public List<TableModel> getChangeTables() {
        return changeTables;
    }

    public void setChangeTables(List<TableModel> changeTables) {
        this.changeTables = changeTables;
    }

    public NotificationModel getNotificationModel() {
        return notificationModel;
    }

    public void setNotificationModel(NotificationModel notificationModel) {
        this.notificationModel = notificationModel;
    }

    public String getActivitySimpleName() {
        return activitySimpleName;
    }

    public void setActivitySimpleName(String activitySimpleName) {
        this.activitySimpleName = activitySimpleName;
    }
}
