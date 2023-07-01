package com.example.nhahang.Interfaces;

import android.widget.ImageView;

import com.example.nhahang.Models.MenuModels;

public interface IItemMenu {
    void loadImgItem(String url, ImageView imgV);
    void onClickItemMenuListener(MenuModels models);

}
