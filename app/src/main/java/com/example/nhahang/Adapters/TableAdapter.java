package com.example.nhahang.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nhahang.Interfaces.IClickItemCategoryTableListener;
import com.example.nhahang.Interfaces.IClickItemTableListener;
import com.example.nhahang.Models.ReservationModel;
import com.example.nhahang.Models.TableModel;
import com.example.nhahang.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder> {

    private List<TableModel> tableModelList;
    private IClickItemTableListener iClickItemTableListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public TableAdapter(List<TableModel> tableModelList, IClickItemTableListener listener) {
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
        holder.numbeTable.setText(tableModel.getId());
        if(!tableModel.getStatus().trim().isEmpty() ){
            db.collection("reservations").document(tableModel.getDocumentId())
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot doc = task.getResult();
                                ReservationModel model = doc.toObject(ReservationModel.class);
                                holder.infotableLl.setVisibility(View.VISIBLE);
                                assert model != null;
                                holder.totalPrice.setText(model.getTotalPrice());
                                holder.currTime.setText(model.getCurrentTime());
                                holder.curDate.setText(model.getCurrentDate());
                            }
                        }
                    });
            holder.status.setText("");
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPrice = holder.totalPrice.getText().toString().trim();
                iClickItemTableListener.onClickItemTableListener(tableModel,holder.tablelabelLl,oldPrice);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tableModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout tablelabelLl,infotableLl;
        TextView numbeTable,status,curDate,currTime,totalPrice;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            numbeTable = itemView.findViewById(R.id.numberTableTv);
            tablelabelLl = itemView.findViewById(R.id.tablelabelLl);
            status = itemView.findViewById(R.id.statusTv);
            infotableLl = itemView.findViewById(R.id.infoTable);
            curDate = itemView.findViewById(R.id.currentDate);
            totalPrice = itemView.findViewById(R.id.totalPrice);
            currTime = itemView.findViewById(R.id.currentTime);
        }
    }
}
