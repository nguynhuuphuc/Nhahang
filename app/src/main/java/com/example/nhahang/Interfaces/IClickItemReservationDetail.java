package com.example.nhahang.Interfaces;

import com.example.nhahang.Models.ProductInReservationModel;

public interface IClickItemReservationDetail {
    public static final  String REMOVE_ITEM = "remove item";
    public void onClickItem(ProductInReservationModel model,String command);
}
