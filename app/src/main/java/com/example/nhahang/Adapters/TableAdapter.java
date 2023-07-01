package com.example.nhahang.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nhahang.Interfaces.IClickItemCategoryTableListener;
import com.example.nhahang.Interfaces.IClickItemTableListener;
import com.example.nhahang.Models.TableModel;
import com.example.nhahang.R;

import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder> {

    private List<TableModel> tableModelList;
    private IClickItemTableListener iClickItemTableListener;

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
        holder.numbeTable.setText(tableModelList.get(position).getId());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClickItemTableListener.onClickItemTableListener(tableModelList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return tableModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView numbeTable;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            numbeTable = itemView.findViewById(R.id.numberTableTv);
        }
    }
}
