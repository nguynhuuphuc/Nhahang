package com.example.nhahang.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nhahang.Interfaces.IClickItemRecommendMenuCateListener;
import com.example.nhahang.Models.MenuCategoryModel;
import com.example.nhahang.Models.MenuModel;
import com.example.nhahang.R;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class MenuCategoryArrayAdapter extends RecyclerView.Adapter<MenuCategoryArrayAdapter.ViewHolder> implements Filterable {
    private List<MenuCategoryModel> menuCategoryModelList,menuCategoryModelListOld;
    private IClickItemRecommendMenuCateListener iClickItemRecommendMenuCateListener;

    public MenuCategoryArrayAdapter(List<MenuCategoryModel> menuCategoryModelList,IClickItemRecommendMenuCateListener iClickItemRecommendMenuCateListener) {
        this.menuCategoryModelList = menuCategoryModelList;
        this.menuCategoryModelListOld = menuCategoryModelList;
        this.iClickItemRecommendMenuCateListener = iClickItemRecommendMenuCateListener;
    }

    @NonNull
    @Override
    public MenuCategoryArrayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_menu_category_for_drop_down,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MenuCategoryArrayAdapter.ViewHolder holder, int position) {
        MenuCategoryModel model = menuCategoryModelList.get(position);
        if (model == null) return;
        holder.nameCate.setText(model.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClickItemRecommendMenuCateListener.onClickItem(model);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(menuCategoryModelList == null)
            return 0;

        return menuCategoryModelList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String sSearch = constraint.toString();
                if(sSearch.isEmpty()){
                    menuCategoryModelList = menuCategoryModelListOld;
                }else{
                    List<MenuCategoryModel> list = new ArrayList<>();
                    for (MenuCategoryModel menuModel : menuCategoryModelList){
                        if(menuModel.getName().toLowerCase().contains(sSearch.toLowerCase())){
                            list.add(menuModel);
                        }
                    }
                    menuCategoryModelList = list;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = menuCategoryModelList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                menuCategoryModelList = (List<MenuCategoryModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameCate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameCate = itemView.findViewById(R.id.menuCategoryNameTv);
        }
    }
}
