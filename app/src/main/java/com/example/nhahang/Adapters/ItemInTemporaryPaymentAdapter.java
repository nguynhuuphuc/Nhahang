package com.example.nhahang.Adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nhahang.Models.MenuModel;
import com.example.nhahang.Models.ProductInReservationModel;
import com.example.nhahang.Models.ReservationModel;
import com.example.nhahang.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

public class ItemInTemporaryPaymentAdapter extends RecyclerView.Adapter<ItemInTemporaryPaymentAdapter.ViewHolder> {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<ProductInReservationModel> productList;

    public ItemInTemporaryPaymentAdapter(List<ProductInReservationModel> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ItemInTemporaryPaymentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_in_temporary_payment,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemInTemporaryPaymentAdapter.ViewHolder holder, int position) {
        ProductInReservationModel model = productList.get(position);
        if(model == null){
            return;
        }
        double totalPrice = Double.parseDouble(model.getProductTotalPrice().replace(",","").trim());
        int quantity = Integer.parseInt(model.getProductQuantity().trim());
        Double newPrice = totalPrice/quantity;
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator(',');
        DecimalFormat decimalFormat = new DecimalFormat("#,###",symbols);

        db.collection("menu").document(model.getProductId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                MenuModel menuModel = documentSnapshot.toObject(MenuModel.class);
                if(menuModel == null) return;
                if(menuModel.getPrice().equals(decimalFormat.format(newPrice))){
                    holder.oldPrice.setVisibility(View.GONE);
                }else {
                    holder.oldPrice.setText(menuModel.getPrice());
                    holder.oldPrice.setPaintFlags(holder.oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
                holder.nameProduct.setText(menuModel.getName());
                holder.newPrice.setText(decimalFormat.format(newPrice));
                holder.quantity.setText(model.getProductQuantity());
                holder.totalPrice.setText(model.getProductTotalPrice());
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
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
