package com.example.nhahang.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.bumptech.glide.request.RequestOptions;
import com.example.nhahang.Interfaces.IItemMenu;
import com.example.nhahang.Models.MenuModel;
import com.example.nhahang.Models.VirtualTable;
import com.example.nhahang.R;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> implements Filterable {
    private List<MenuModel> menuModelsList;
    private IItemMenu iItemMenu;
    private Context context;
    private List<VirtualTable> virtualTableList = new ArrayList<>();
    private List<MenuModel> menuModelsListOld;

    public MenuAdapter(List<MenuModel> menuModelsList, Context context, IItemMenu iItemMenu) {
        this.menuModelsList = menuModelsList;
        this.iItemMenu = iItemMenu;
        this.context = context;
        this.menuModelsListOld = menuModelsList;
    }

    public void setVirtualTableList(List<VirtualTable> virtualTableList) {
        this.virtualTableList = virtualTableList;
    }

    @NonNull
    @Override
    public MenuAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_in_menu,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MenuAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MenuModel models = menuModelsList.get(position);
        if(!virtualTableList.isEmpty()){
            for(VirtualTable vt : virtualTableList){
                if(vt.getDocumentId().equals(models.getDocumentId())){
                    holder.quantityEt.setText(vt.getQuantity());
                }
            }
        }
        holder.name.setText(models.getName());
        holder.price.setText(models.getPrice());
        if(models.isAdded()){
            holder.quantityLl.setVisibility(View.VISIBLE);
            holder.checkIv.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(models.getImg())
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3)))
                    .into(holder.imageView);
        }else{
            holder.quantityLl.setVisibility(View.GONE);
            holder.checkIv.setVisibility(View.GONE);
            Glide.with(context).load(models.getImg()).into(holder.imageView);
        }

        iItemMenu.loadImgItem(
                models,
                holder.imageView,
                null,
                null);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!models.isSelected()){
                   models.setSelected(true);
                    iItemMenu.loadImgItem(
                            models,
                            holder.imageView,
                            holder.quantityLl,
                            holder.checkIv);
                    iItemMenu.onClickItemMenuListener(models);
                    return;
                }
                iItemMenu.onClickItemMenuListener(models);
            }
        });
        holder.minusIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String signal = "";
                int newNumber = 1;
                if (holder.quantityEt.getText().toString().equals("1")) {
                    holder.quantityLl.setVisibility(View.GONE);
                    holder.checkIv.setVisibility(View.GONE);
                    Glide.with(context).load(models.getImg()).into(holder.imageView);
                    signal = "delete";
                    newNumber = 0;
                } else {
                    newNumber = Integer.parseInt(holder.quantityEt.getText().toString()) - 1;
                    holder.quantityEt.setText(String.valueOf(newNumber));
                }
                iItemMenu.onClickMinusListener(models, signal, newNumber);

            }
        });
        holder.plusIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newNumber = Integer.parseInt(holder.quantityEt.getText().toString()) +1;
                holder.quantityEt.setText(String.valueOf(newNumber));
                iItemMenu.onClickPlusListener(models,newNumber);
            }
        });


    }

    @Override
    public int getItemCount() {
        return menuModelsList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView,plusIv,minusIv,checkIv;
        TextView name,price;
        LinearLayout quantityLl;
        EditText quantityEt;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageItemInMenuIv);
            name = itemView.findViewById(R.id.nameItemInMenuTv);
            price = itemView.findViewById(R.id.priceItemInMenuTv);
            quantityLl = itemView.findViewById(R.id.selectQuantityLl);
            plusIv = itemView.findViewById(R.id.plussIv);
            minusIv = itemView.findViewById(R.id.minusIv);
            quantityEt = itemView.findViewById(R.id.quantityNumberEt);
            checkIv = itemView.findViewById(R.id.checkIv);

        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String sSearch = constraint.toString();
                if(sSearch.isEmpty()){
                    menuModelsList = menuModelsListOld;
                }else{
                    List<MenuModel> list = new ArrayList<>();
                    for (MenuModel menuModel : menuModelsListOld){
                        if(menuModel.getName().toLowerCase().contains(sSearch.toLowerCase())){
                            list.add(menuModel);
                        }
                    }
                    menuModelsList = list;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = menuModelsList;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                menuModelsList = (List<MenuModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
