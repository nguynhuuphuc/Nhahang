package com.example.nhahang.Models;

import java.util.Date;
import java.util.List;

public class OrderItemsHisModel {
    private int id;
    private String action_type;
    private Date timestamp;
    private String menu_item_id;
    private int quantity;
    private int order_id;
    private String changed_by;
    private List<Item> items;
    private String dateHeader;
    private long dateHeaderId;

    public OrderItemsHisModel() {
    }

    public OrderItemsHisModel(Date timestamp, int order_id, String changed_by) {
        this.timestamp = timestamp;
        this.order_id = order_id;
        this.changed_by = changed_by;
    }


    public OrderItemsHisModel(int id, String action_type, Date timestamp, String menu_item_id, int quantity, int order_id, String changed_by) {
        this.id = id;
        this.action_type = action_type;
        this.timestamp = timestamp;
        this.menu_item_id = menu_item_id;
        this.quantity = quantity;
        this.order_id = order_id;
        this.changed_by = changed_by;
    }

    public long getDateHeaderId() {
        return dateHeaderId;
    }

    public void setDateHeaderId(long dateHeaderId) {
        this.dateHeaderId = dateHeaderId;
    }

    public String getDateHeader() {
        return dateHeader;
    }

    public void setDateHeader(String dateHeader) {
        this.dateHeader = dateHeader;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAction_type() {
        return action_type;
    }

    public void setAction_type(String action_type) {
        this.action_type = action_type;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getMenu_item_id() {
        return menu_item_id;
    }

    public void setMenu_item_id(String menu_item_id) {
        this.menu_item_id = menu_item_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public String getChanged_by() {
        return changed_by;
    }

    public void setChanged_by(String changed_by) {
        this.changed_by = changed_by;
    }

    public static class Item{
        private int quantity;
        private String menu_item_id;
        private String action_type;

        public Item() {
        }

        public Item(int quantity, String menu_item_id) {
            this.quantity = quantity;
            this.menu_item_id = menu_item_id;
        }

        public Item(int quantity, String menu_item_id, String action_type) {
            this.quantity = quantity;
            this.menu_item_id = menu_item_id;
            this.action_type = action_type;
        }

        public String getAction_type() {
            return action_type;
        }

        public void setAction_type(String action_type) {
            this.action_type = action_type;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public String getMenu_item_id() {
            return menu_item_id;
        }

        public void setMenu_item_id(String menu_item_id) {
            this.menu_item_id = menu_item_id;
        }
    }
}
