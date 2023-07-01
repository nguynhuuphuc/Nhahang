package com.example.nhahang.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.example.nhahang.Interfaces.IItemMenu;
import com.example.nhahang.Models.MenuModels;
import com.example.nhahang.R;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
    private List<MenuModels> menuModelsList;
    private IItemMenu iItemMenu;

    public MenuAdapter(List<MenuModels> menuModelsList, IItemMenu iItemMenu) {
        this.menuModelsList = menuModelsList;
        this.iItemMenu = iItemMenu;
    }

    @NonNull
    @Override
    public MenuAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_in_menu,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MenuAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(menuModelsList.get(position).getName());
        holder.price.setText(menuModelsList.get(position).getPrice());
        iItemMenu.loadImgItem(menuModelsList.get(position).getImg(),holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iItemMenu.onClickItemMenuListener(menuModelsList.get(position));
            }
        });


    }

    @Override
    public int getItemCount() {
        return menuModelsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView name,price;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageItemInMenuIv);
            name = itemView.findViewById(R.id.nameItemInMenuTv);
            price = itemView.findViewById(R.id.priceItemInMenuTv);
        }
    }
}
