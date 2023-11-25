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

import com.example.nhahang.Interfaces.IClickItemCategoryTableListener;
import com.example.nhahang.Models.CategoryTableModel;
import com.example.nhahang.Models.LocationModel;
import com.example.nhahang.R;


import java.util.List;


public class CategoryTableAdapter extends RecyclerView.Adapter<CategoryTableAdapter.ViewHolder> {

    private IClickItemCategoryTableListener iClickItemListener;
    private List<LocationModel> locationList;

    int selectedPosition = 0;

    public CategoryTableAdapter(List<LocationModel> locationList,IClickItemCategoryTableListener iClickItemListener ) {
        this.iClickItemListener = iClickItemListener;
        this.locationList = locationList;
    }

    @NonNull
    @Override
    public CategoryTableAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category,parent,false));
    }


    @Override
    public void onBindViewHolder(@NonNull CategoryTableAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        LocationModel location = locationList.get(position);
        if(location == null){
            return;
        }
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
            holder.categoryNameTv.setText(location.getLocation_name());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(selectedPosition == position)
                    {
                        selectedPosition = 0;
                        notifyDataSetChanged();
                        return;
                    }
                    selectedPosition = position;
                    notifyDataSetChanged();
                    iClickItemListener.onClickItemCategoryTableListener(location);
                }
            });
    }

    @Override
    public int getItemCount() {
       return locationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryNameTv;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.itemCategoryCv);
            categoryNameTv = itemView.findViewById(R.id.categoryNameTv);

        }
    }
}
