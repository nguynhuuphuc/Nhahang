package com.example.nhahang.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nhahang.Interfaces.IClickItemReservationDetail;
import com.example.nhahang.Models.MenuModel;
import com.example.nhahang.Models.ProductInReservationModel;
import com.example.nhahang.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zerobranch.layout.SwipeLayout;

import java.util.List;

public class ReservationDetailAdapter extends RecyclerView.Adapter<ReservationDetailAdapter.ViewHolder> {
    private List<ProductInReservationModel> productList;
    private IClickItemReservationDetail iClickItemReservationDetail;
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ReservationDetailAdapter(List<ProductInReservationModel> productList, Context context, IClickItemReservationDetail iClickItemReservationDetail) {
        this.productList = productList;
        this.iClickItemReservationDetail = iClickItemReservationDetail;
        this.context = context;
    }

    @NonNull
    @Override
    public ReservationDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_in_reservation_detail,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationDetailAdapter.ViewHolder holder, int position) {
        ProductInReservationModel model = productList.get(position);
        db.collection("menu").document(model.getProductId())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            MenuModel menuModel = document.toObject(MenuModel.class);
                            Glide.with(context).load(menuModel.getImg()).into(holder.imgV);
                            holder.name.setText(menuModel.getName());
                        }
                    }
                });
        holder.price.setText(model.getProductTotalPrice());
        holder.quantity.setText(model.getProductQuantity());
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClickItemReservationDetail.onClickItem(model,null);
            }
        });
        holder.deleteIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClickItemReservationDetail.onClickItem(model,IClickItemReservationDetail.REMOVE_ITEM);
            }
        });

        holder.swipeLayout.setOnActionsListener(new SwipeLayout.SwipeActionsListener() {
            @Override
            public void onOpen(int direction, boolean isContinuous) {
                if (direction == SwipeLayout.RIGHT) {
                    // was executed swipe to the right

                } else if (direction == SwipeLayout.LEFT) {
                    // was executed swipe to the left
                    holder.deleteIv.setEnabled(true);
                }
            }
            @Override
            public void onClose() {
                holder.deleteIv.setEnabled(false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private SwipeLayout swipeLayout;
        private ImageView imgV,deleteIv;
        private TextView name,price,quantity;
        private CardView item;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgV = itemView.findViewById(R.id.imageItemInMenuIv);
            name = itemView.findViewById(R.id.nameItemInMenuTv);
            price = itemView.findViewById(R.id.priceItemInMenuTv);
            quantity = itemView.findViewById(R.id.quantityTv);
            swipeLayout = itemView.findViewById(R.id.swipe_layout);
            deleteIv = itemView.findViewById(R.id.deleteIv);
            item = itemView.findViewById(R.id.drag_item);
        }
    }
}
