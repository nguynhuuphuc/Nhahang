package com.example.nhahang.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.nhahang.BottomSheetDialogFragments.NoteOrderItemFragment;
import com.example.nhahang.Interfaces.INoteOrderItem;
import com.example.nhahang.Interfaces.IOrderItemDetail;
import com.example.nhahang.Models.MenuModel;
import com.example.nhahang.Models.OrderItemModel;
import com.example.nhahang.R;
import com.example.nhahang.Utils.Util;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zerobranch.layout.SwipeLayout;

import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {
    private List<OrderItemModel> orderItemModels;
    private Context context;
    private FirebaseFirestore firestore;
    private List<MenuModel> menuModels;
    private FragmentManager fragmentManager;
    private IOrderItemDetail iOrderItemDetail;


    public OrderItemAdapter( Context context, List<OrderItemModel> orderItemModels,List<MenuModel> menuModels,FragmentManager fragmentManager) {
        this.orderItemModels = orderItemModels;
        this.context = context;
        firestore = FirebaseFirestore.getInstance();
        this.menuModels = menuModels;
        this.fragmentManager = fragmentManager;
    }

    public void setOnChangeOrderItemListener(IOrderItemDetail iOrderItemDetail){
        this.iOrderItemDetail = iOrderItemDetail;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_in_reservation_detail,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        OrderItemModel model = orderItemModels.get(position);
        if(model == null){
            return;
        }
        holder.quantity.setText(String.valueOf(model.getQuantity()));
        if(model.getDiscount_amount() == 0 && model.getDiscount_percentage() == 0){
            Util.updateMoneyLabel(holder.price,model.getItem_price());
        }else{
            if(model.getDiscount_amount() > 0){
                double priceDiscount = model.getItem_price() - model.getDiscount_amount();
                Util.updateMoneyLabel(holder.price,priceDiscount);
            }
            if(model.getDiscount_percentage() > 0){
                double priceDiscount = model.getItem_price() * (100 - model.getDiscount_percentage())/ 100;
                Util.updateMoneyLabel(holder.price,priceDiscount);
            }
        }
        MenuModel menuModel = null;
        for (MenuModel item : menuModels){
            if(item.getDocumentId().equals(model.getMenu_item_id())){
                menuModel = item;
                break;
            }
        }
        if(menuModel != null){
        Glide.with(context).load(menuModel.getImg()).into(holder.imgV);
        holder.name.setText(menuModel.getName());}


        MenuModel finalMenuModel = menuModel;
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderItemModel orderItemOld = model.clone();
                NoteOrderItemFragment bottomSheet = new NoteOrderItemFragment(model, finalMenuModel);
                bottomSheet.show(fragmentManager,bottomSheet.getTag());
                bottomSheet.setINoteOrderItem(new INoteOrderItem() {
                    @Override
                    public void setOnDismissListener() {
                        if(orderItemOld.isChange(model)){
                            notifyItemChanged(position);
                            iOrderItemDetail.onOrderItemChangeListener();
                        }
                    }
                });
                    
            }
        });

    }

    @Override
    public int getItemCount() {
        return orderItemModels.size();
    }

    public Integer getQuantity(){
        int itemQuantity = 0;
        for (OrderItemModel model : orderItemModels){
            itemQuantity += model.getQuantity();
        }
        return itemQuantity;
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
