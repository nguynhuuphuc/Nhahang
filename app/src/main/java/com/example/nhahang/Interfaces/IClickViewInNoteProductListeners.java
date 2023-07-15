package com.example.nhahang.Interfaces;

import com.example.nhahang.Models.MenuModel;
import com.example.nhahang.Models.ProductInReservationModel;
import com.example.nhahang.Models.VirtualTable;

public interface IClickViewInNoteProductListeners {
    public final static String REMOVE_PRODUCT = "remove";
    public final static String SAVED_NOTE = "saved";
    void onClickListener(ProductInReservationModel model, String command);
}
