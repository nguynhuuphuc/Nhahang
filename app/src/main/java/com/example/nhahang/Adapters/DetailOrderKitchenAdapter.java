package com.example.nhahang.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nhahang.DetailOrderKitchen;
import com.example.nhahang.Models.MenuModel;
import com.example.nhahang.Models.OrderItemModel;
import com.example.nhahang.R;
import com.example.nhahang.Utils.Util;
import com.zerobranch.layout.SwipeLayout;

import java.util.List;

public class DetailOrderKitchenAdapter extends RecyclerView.Adapter<DetailOrderKitchenAdapter.ViewHolder> {
    private Context context;
    private List<OrderItemModel> orderItemModels;
    private List<MenuModel> menuModels;
    private OnClickItem onClickItem;
    private OnListEmpty onListEmpty;

    public void setOnListEmpty(OnListEmpty onListEmpty) {
        this.onListEmpty = onListEmpty;
    }

    public void setOnClickItem(OnClickItem onClickItem) {
        this.onClickItem = onClickItem;
    }

    public interface OnClickItem{
        void onClick(OrderItemModel item,MenuModel dish);
        void onDelete(OrderItemModel item);
    }

    public interface OnListEmpty{
        void listEmpty();
    }

    public DetailOrderKitchenAdapter(Context context, List<OrderItemModel> orderItemModels, List<MenuModel> menuModels) {
        this.context = context;
        this.orderItemModels = orderItemModels;
        this.menuModels = menuModels;
    }

    public void itemRemoving(OrderItemModel item){
        int index = orderItemModels.indexOf(item);
        orderItemModels.remove(item);
        notifyItemRemoved(index);
        if(orderItemModels.isEmpty()){
            onListEmpty.listEmpty();
        }
    }

    @NonNull
    @Override
    public DetailOrderKitchenAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_in_reservation_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DetailOrderKitchenAdapter.ViewHolder holder, int position) {
        OrderItemModel itemModel = orderItemModels.get(position);
        if(itemModel == null) return;
        MenuModel itemMenu = null;
        for(MenuModel item : menuModels){
            if(item.getDocumentId().equals(itemModel.getMenu_item_id())){
                itemMenu = item;
                break;
            }
        }
        if(itemMenu == null) return;

        holder.swipeLayout.setEnabledSwipe(itemModel.getStatus_id() == 1);
        holder.item.setEnabled(itemModel.getStatus_id() == 1);
        Glide.with(context).load(itemMenu.getImg()).into(holder.itemImg);
        holder.itemName.setText(itemMenu.getName());
        holder.notes.setText(itemModel.getNote());
        holder.quantity.setText(String.valueOf(itemModel.getQuantity()));
        Util.updateMoneyLabel(holder.price,itemModel.getItem_price());
        MenuModel finalItemMenu = itemMenu;
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickItem.onClick(itemModel, finalItemMenu);
            }
        });
        
        holder.deleteIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickItem.onDelete(itemModel);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(orderItemModels != null) return orderItemModels.size();
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private SwipeLayout swipeLayout;
        private ImageView itemImg,deleteIv;
        private TextView itemName,quantity,notes,price;
        private RelativeLayout item;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            swipeLayout = itemView.findViewById(R.id.swipe_layout);
            itemImg = itemView.findViewById(R.id.imageItemInMenuIv);
            itemName = itemView.findViewById(R.id.nameItemInMenuTv);
            quantity = itemView.findViewById(R.id.quantityTv);
            notes = itemView.findViewById(R.id.noteItemTv);
            item = itemView.findViewById(R.id.drag_item);
            price = itemView.findViewById(R.id.priceItemInMenuTv);
            deleteIv = itemView.findViewById(R.id.deleteIv);
        }
    }
}
