package com.example.nhahang.Adapters;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nhahang.ChangeTableActivity;
import com.example.nhahang.Interfaces.ApiService;
import com.example.nhahang.Interfaces.IClickItemCategoryTableListener;
import com.example.nhahang.Interfaces.IClickItemTableListener;
import com.example.nhahang.JoinTableActivity;
import com.example.nhahang.Models.MenuModel;
import com.example.nhahang.Models.OrderModel;
import com.example.nhahang.Models.Requests.OrderRequest;
import com.example.nhahang.Models.ReservationModel;
import com.example.nhahang.Models.TableModel;
import com.example.nhahang.R;
import com.example.nhahang.Utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.protobuf.Api;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder> implements Filterable {

    private List<TableModel> tableModelList;
    private final List<TableModel> tableModelListOld;
    private IClickItemTableListener iClickItemTableListener;
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseDatabase realtimeDB = FirebaseDatabase.getInstance();


    public TableAdapter(Context context, List<TableModel> tableModelList, IClickItemTableListener listener) {
        this.tableModelListOld = tableModelList;
        this.context = context;
        this.tableModelList = tableModelList;
        iClickItemTableListener = listener;

    }


    @NonNull
    @Override
    public TableAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_table, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TableAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        TableModel tableModel = tableModelList.get(position);
        if(tableModel == null){
            return;
        }
        holder.nameTable.setText(tableModel.getTable_name());
        if(tableModel.is_occupied()){
            holder.infotableLl.setVisibility(View.VISIBLE);
            int strokeColor = ContextCompat.getColor(context, R.color.deep_sky_blue);
            holder.cardView.setStrokeColor(strokeColor);
            holder.status.setText("Đang sử dụng");
            try {
                Util.updateDateLabel(holder.curDate,tableModel.getOrder_date());
                Util.updateTimeLabel(holder.currTime,tableModel.getOrder_date());
                Util.updateMoneyLabel(holder.totalPrice,tableModel.getTotal_amount());
            }catch (Exception ignored){}

        }else {
            int strokeColor = ContextCompat.getColor(context, R.color.light_gray);
            holder.cardView.setStrokeColor(strokeColor);
            holder.status.setText("Rảnh");
            holder.infotableLl.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iClickItemTableListener.onClickItemTableListener(tableModel,holder.tablelabelLl,"00",position);
            }
        });

    }

    private void getOrderByTableId(int table_id,TableAdapter.ViewHolder holder) {
        OrderRequest request = new OrderRequest();
        request.setTable_id(table_id);
        ApiService.apiService.getOrderByTableId(request)
                .enqueue(new Callback<OrderModel>() {
                    @Override
                    public void onResponse(Call<OrderModel> call, Response<OrderModel> response) {
                        if(response.isSuccessful()){
                            OrderModel model = response.body();
                            assert model != null;
                            Util.updateDateLabel(holder.curDate,model.getOrder_date());
                            Util.updateTimeLabel(holder.currTime,model.getOrder_date());
                            Util.updateMoneyLabel(holder.totalPrice,model.getTotal_amount());
                        }

                    }

                    @Override
                    public void onFailure(Call<OrderModel> call, Throwable t) {

                    }
                });
    }


    @Override
    public int getItemCount() {
        if(tableModelList != null) return tableModelList.size();
        return 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String sSearch = constraint.toString();
                if(sSearch.isEmpty()){
                    tableModelList = tableModelListOld;
                }
                else {
                    List<TableModel> list = new ArrayList<>();
                    int locationId = -1;
                    try {
                        locationId = Integer.parseInt(sSearch);
                    }catch (Exception ignored){}
                    if(locationId != -1) {
                        for (TableModel tableModel : tableModelListOld) {
                            if (tableModel.getLocation_id() == Integer.parseInt(sSearch)) {
                                list.add(tableModel);
                            }
                        }
                    }else {
                        JsonObject jsonObject = JsonParser.parseString(sSearch).getAsJsonObject();
                        boolean isOccupied = jsonObject.get("is_occupied").getAsBoolean();
                        int location_id = jsonObject.get("locationId").getAsInt();
                        for (TableModel tableModel : tableModelListOld){
                            if(tableModel.is_occupied() == isOccupied && tableModel.getLocation_id() == location_id){
                                list.add(tableModel);
                            }
                        }

                    }
                    tableModelList = list;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = tableModelList;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {
                tableModelList = (List<TableModel>) results.values;
                notifyDataSetChanged();

            }
        };
    }

    public void updateTable(TableModel model){
        for (int i = 0; i < tableModelListOld.size(); i++){
            if(i < tableModelList.size() && tableModelList.get(i).getTable_id() == model.getTable_id()){
                tableModelList.set(i,model);
                notifyItemChanged(i);
            }
            if(tableModelListOld.get(i).getTable_id() == model.getTable_id()){
                tableModelListOld.set(i,model);
               notifyItemChanged(i);
                 break;
            }
        }
    }
    public void updateOldTable(List<TableModel> models){
        for (TableModel model : models){
            for (int i = 0; i < tableModelListOld.size(); i++){
                if(tableModelListOld.get(i).getTable_id() == model.getTable_id()){
                    tableModelListOld.set(i,model);
                    notifyItemChanged(i);
                    break;
                }
            }
        }
    }


    public void updateTable(List<TableModel> models){
        if(context instanceof ChangeTableActivity || context instanceof JoinTableActivity){
                for(TableModel model : models){
                    for (int i = 0; i < tableModelListOld.size(); i++){
                        if(i < tableModelList.size() && tableModelList.get(i).getTable_id() == model.getTable_id()){
                            tableModelList.remove(i);
                            notifyItemRemoved(i);
                        }
                        if(tableModelListOld.get(i).getTable_id() == model.getTable_id()){
                            tableModelListOld.set(i,model);
                            notifyItemChanged(i);
                            break;
                        }
                }
            }
            return;
        }
        for (TableModel model : models){
            for (int i = 0; i < tableModelListOld.size(); i++){
                if(i < tableModelList.size() && tableModelList.get(i).getTable_id() == model.getTable_id()){
                    tableModelList.set(i,model);
                    notifyItemChanged(i);
                }
                if(tableModelListOld.get(i).getTable_id() == model.getTable_id()){
                    tableModelListOld.set(i,model);
                    notifyItemChanged(i);
                    break;
                }
            }
        }


    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout tablelabelLl,infotableLl;
        TextView nameTable,status,curDate,currTime,totalPrice;
        MaterialCardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTable = itemView.findViewById(R.id.numberTableTv);
            tablelabelLl = itemView.findViewById(R.id.tablelabelLl);
            status = itemView.findViewById(R.id.statusTv);
            infotableLl = itemView.findViewById(R.id.infoTable);
            curDate = itemView.findViewById(R.id.currentDate);
            totalPrice = itemView.findViewById(R.id.totalPrice);
            currTime = itemView.findViewById(R.id.currentTime);
            cardView = itemView.findViewById(R.id.tableCv);
        }
    }




}
