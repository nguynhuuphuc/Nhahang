package com.example.nhahang.Interfaces;

import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.nhahang.Models.MenuModel;

public interface IItemMenu {
    void loadImgItem(
            MenuModel models,
            ImageView imgV,
            LinearLayout quantityLl,
            ImageView checkIv);
    void onClickItemMenuListener(MenuModel models,int position);
    void onClickPlusListener(MenuModel models,int value);
    void onClickMinusListener(MenuModel models, String signal,int value);

}
