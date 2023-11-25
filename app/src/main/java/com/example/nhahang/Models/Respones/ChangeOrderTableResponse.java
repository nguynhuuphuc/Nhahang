package com.example.nhahang.Models.Respones;

import java.io.Serializable;

public class ChangeOrderTableResponse implements Serializable {
    private int old_table_id;
    private int new_table_id;
    private int order_id;

    public ChangeOrderTableResponse(int old_table_id, int new_table_id, int order_id) {
        this.old_table_id = old_table_id;
        this.new_table_id = new_table_id;
        this.order_id = order_id;
    }

    public int getOld_table_id() {
        return old_table_id;
    }

    public void setOld_table_id(int old_table_id) {
        this.old_table_id = old_table_id;
    }

    public int getNew_table_id() {
        return new_table_id;
    }

    public void setNew_table_id(int new_table_id) {
        this.new_table_id = new_table_id;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }
}
