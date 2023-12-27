package com.example.nhahang.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nhahang.DetailOrderKitchen;
import com.example.nhahang.Models.OrderKitchenModel;
import com.example.nhahang.Models.TableModel;
import com.example.nhahang.R;
import com.example.nhahang.Utils.Util;

import java.util.Iterator;
import java.util.List;

public class OrderCheckListAdapter extends RecyclerView.Adapter<OrderCheckListAdapter.ViewHolder> {

    private Context context;
    private List<OrderKitchenModel> orderKitchenModels;
    private ActivityResultLauncher<Intent> launcher;

    public OrderCheckListAdapter(Context context, List<OrderKitchenModel> orderKitchenModels,ActivityResultLauncher<Intent> launcher ) {
        this.context = context;
        this.orderKitchenModels = orderKitchenModels;
        this.launcher = launcher;
    }

    @NonNull
    @Override
    public OrderCheckListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_orderitem_checklist,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            // Cập nhật chỉ các phần của item dựa trên thông tin trong payload
            String updatedText = (String) payloads.get(0);
            holder.status.setText(updatedText);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull OrderCheckListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        OrderKitchenModel model = orderKitchenModels.get(position);
        if(model == null) return;

        if(model.getStatus_id() == OrderKitchenModel.ORDER_NOT_CONFIRM){
            holder.cardView.setCardBackgroundColor(context.getColor(R.color.lighter_red));
        }else if(model.getStatus_id() == OrderKitchenModel.ORDER_CONFIRM){
            holder.cardView.setCardBackgroundColor(context.getColor(R.color.light_blue));
        }else if(model.getStatus_id() == OrderKitchenModel.ORDER_SERV){
            holder.cardView.setCardBackgroundColor(context.getColor(R.color.lighter_green));
        }

        holder.status.setText(model.getStatus());
        holder.orderId.setText(String.valueOf(model.getOrder_id()));
        holder.quantity.setText(String.valueOf(model.getQuantity()));
        holder.tableName.setText(model.getTable_name());
        holder.notifyTime.setText(Util.DayTimeFormatting(model.getNotify_time()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, DetailOrderKitchen.class);
                intent.putExtra("OrderKitchenModel",model);
                launcher.launch(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (orderKitchenModels != null) return  orderKitchenModels.size();
        return 0;
    }
    public void updateItemToLast(OrderKitchenModel model){
        int lastIndex = getItemCount() - 1;
        for(int i = 0 ; i < orderKitchenModels.size() ; i++){
            OrderKitchenModel item = orderKitchenModels.get(i);
            if(item.getOrder_id() == model.getOrder_id()
                    && item.getNotify_time().equals(model.getNotify_time())){
                orderKitchenModels.remove(i);
                orderKitchenModels.add(model);
                notifyItemRangeChanged(i,getItemCount() - i);
                return;
            }
        }
    }
    public void orderDeletedRemoving(OrderKitchenModel model){
        int index = -1;
        for (OrderKitchenModel item : orderKitchenModels){
            if(item.getOrder_id() == model.getOrder_id() && item.getTable_id() == model.getTable_id()
            && item.getNotify_time().equals(model.getNotify_time())){
                index = orderKitchenModels.indexOf(item);
                break;
            }
        }
        if(index == -1) return;
        orderKitchenModels.remove(index);
        notifyItemRemoved(index);
    }

    public void removeItem(int order_id){
        Iterator<OrderKitchenModel> iterator = orderKitchenModels.iterator();
        while (iterator.hasNext()) {
            OrderKitchenModel obj = iterator.next();
            if (obj.getOrder_id() == order_id) {
                iterator.remove(); // Loại bỏ đối tượng nếu ID trùng khớp
            }
        }
        notifyDataSetChanged();

    }

    public void addItem(OrderKitchenModel model){
        if(orderKitchenModels.isEmpty()){
            orderKitchenModels.add(model);
            notifyDataSetChanged();
            return;
        }
        for (int i = 0 ; i < orderKitchenModels.size(); i ++){
            OrderKitchenModel item = orderKitchenModels.get(i);
            if(item.getStatus_id() == OrderKitchenModel.ORDER_SERV){
                orderKitchenModels.add(i,model);
                notifyDataSetChanged();
                return;
            }
            if(item.getOrder_id() == model.getOrder_id()
                    && item.getNotify_time().equals(model.getNotify_time())){
                orderKitchenModels.set(i,model);
                notifyItemChanged(i);
                return;
            }
            if(i == orderKitchenModels.size() - 1){
                orderKitchenModels.add(model);
                notifyDataSetChanged();
                break;
            }
        }

    }

    public void updateItem(OrderKitchenModel model){
        for(int i = 0; i < orderKitchenModels.size() ; i ++){
            OrderKitchenModel item = orderKitchenModels.get(i);
            if(item.getOrder_id() == model.getOrder_id()
                    && item.getNotify_time().equals(model.getNotify_time())){
                orderKitchenModels.set(i,model);
                notifyItemChanged(i);
            }

        }
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderId,tableName,quantity,status,notifyTime;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.orderIdTv);
            tableName = itemView.findViewById(R.id.tableNameTv);
            quantity = itemView.findViewById(R.id.quantityTv);
            status = itemView.findViewById(R.id.statusTv);
            notifyTime = itemView.findViewById(R.id.notifyTimeTv);
            cardView = itemView.findViewById(R.id.cardview);
        }
    }
}
