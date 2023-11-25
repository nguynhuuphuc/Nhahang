package com.example.nhahang.Adapters;

import android.app.Application;
import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nhahang.Models.MenuModel;
import com.example.nhahang.Models.OrderItemModel;
import com.example.nhahang.Models.ProductInReservationModel;
import com.example.nhahang.Models.ReservationModel;
import com.example.nhahang.R;
import com.example.nhahang.Utils.Util;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

public class ItemInTemporaryPaymentAdapter extends RecyclerView.Adapter<ItemInTemporaryPaymentAdapter.ViewHolder> {

    private List<OrderItemModel> orderItemModels;
    private List<MenuModel> menuModels;
    private double orderTotalAmount = 0.0;
    private int orderTotalQuantity = 0;
    private OnDataAdapterChange iOnChange;


    public interface OnDataAdapterChange{
        public void onChange();
    }

    public void setIOnChange(OnDataAdapterChange iOnChange){
        this.iOnChange = iOnChange;
    }

    public ItemInTemporaryPaymentAdapter(List<OrderItemModel> orderItemModels, List<MenuModel> menuModels) {
        this.orderItemModels = orderItemModels;
        this.menuModels = menuModels;
    }

    @NonNull
    @Override
    public ItemInTemporaryPaymentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_in_temporary_payment,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemInTemporaryPaymentAdapter.ViewHolder holder, int position) {
        OrderItemModel model = orderItemModels.get(position);
        if(model == null){
            return;
        }
        for (MenuModel item: menuModels){
            if(item.getDocumentId().equals(model.getMenu_item_id())){
                holder.nameProduct.setText(item.getName());
            }
        }

        double oldPrice = model.getItem_price();
        int quantity = model.getQuantity();

        double newPrice = oldPrice;
        if(model.getDiscount_amount() > 0){
            newPrice = oldPrice - model.getDiscount_amount();
        }else if(model.getDiscount_percentage() > 0){
            newPrice = oldPrice * (100 - model.getDiscount_percentage()) / 100;
        }
        if(model.getItem_price() == newPrice){
            holder.oldPrice.setVisibility(View.GONE);
        }else {
            Util.updateMoneyLabel(holder.oldPrice,oldPrice);
            holder.oldPrice.setPaintFlags(holder.oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        Util.updateMoneyLabel(holder.newPrice,newPrice);
        holder.quantity.setText(String.valueOf(quantity));
        double totalPrice = newPrice * quantity;
        setOrderTotalQuantity(quantity);
        setOrderTotalAmount(totalPrice);
        Util.updateMoneyLabel(holder.totalPrice,totalPrice);

        if(position == orderItemModels.size() - 1){
            iOnChange.onChange();
        }


    }

    private void setOrderTotalAmount(double value){
        this.orderTotalAmount += value;
    }
    private void setOrderTotalQuantity(int value){
        this.orderTotalQuantity +=value;
    }

    @Override
    public int getItemCount() {
        return orderItemModels.size();
    }

    public double getOrderTotalAmount(){
        return this.orderTotalAmount;
    }

    public int getOrderTotalQuantity(){
        return this.orderTotalQuantity;
    }




    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView oldPrice, newPrice, totalPrice, nameProduct, quantity;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            oldPrice = itemView.findViewById(R.id.oldPrice);
            newPrice = itemView.findViewById(R.id.newPrice);
            totalPrice = itemView.findViewById(R.id.totalPrice);
            nameProduct = itemView.findViewById(R.id.nameProductTv);
            quantity = itemView.findViewById(R.id.quantity);
        }
    }
}
