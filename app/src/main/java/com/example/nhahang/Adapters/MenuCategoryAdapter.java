package com.example.nhahang.Adapters;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nhahang.Interfaces.IClickItemMenuCategoryListener;
import com.example.nhahang.Models.MenuCategoryModel;
import com.example.nhahang.R;


import java.util.List;

public class MenuCategoryAdapter extends RecyclerView.Adapter<MenuCategoryAdapter.ViewHolder> {

    List<MenuCategoryModel> menuCategoryModelList;
    IClickItemMenuCategoryListener iClickItemMenuCategoryListener;
    int selectedPosition = 0;

    public MenuCategoryAdapter(List<MenuCategoryModel> menuCategoryModelList, IClickItemMenuCategoryListener iClickItemMenuCategoryListener) {
        this.menuCategoryModelList = menuCategoryModelList;
        this.iClickItemMenuCategoryListener = iClickItemMenuCategoryListener;
    }

    @NonNull
    @Override
    public MenuCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MenuCategoryAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {


        if(selectedPosition == position){
            //Selected
            holder.cardView.setCardBackgroundColor(Color.parseColor("#00BFFF"));
            holder.categoryNameTv.setTextColor(Color.WHITE);
        }
        else{
            //Not selected
            holder.cardView.setCardBackgroundColor(Color.WHITE);
            holder.categoryNameTv.setTextColor(Color.parseColor("#808080"));
        }

        holder.categoryNameTv.setText(menuCategoryModelList.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedPosition == position)
                {
                    selectedPosition = 0;
                    notifyDataSetChanged();
                    return;
                }
                selectedPosition = position;
                notifyDataSetChanged();

                iClickItemMenuCategoryListener.onClickItemMenuCategoryListener(menuCategoryModelList.get(position));

            }
        });

    }

    @Override
    public int getItemCount() {
        return menuCategoryModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryNameTv;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.itemCategoryCv);
            categoryNameTv = itemView.findViewById(R.id.categoryNameTv);
        }
    }
}
