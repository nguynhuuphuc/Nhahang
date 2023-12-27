package com.example.nhahang.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.nhahang.Interfaces.IOnClickDateItemListener;
import com.example.nhahang.R;
import com.example.nhahang.Utils.Util;

import java.time.LocalDate;
import java.util.List;

public class LocalDateAdapter extends RecyclerView.Adapter<LocalDateAdapter.ViewHolder> {
    private Context context;
    private List<LocalDate> localDates;
    private IOnClickDateItemListener iOnClickDateItemListener;
    private int selectedPosition = 0;

    public LocalDateAdapter(Context context, List<LocalDate> localDates, IOnClickDateItemListener iOnClickDateItemListener) {
        this.context = context;
        this.localDates = localDates;
        this.iOnClickDateItemListener = iOnClickDateItemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_day, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        LocalDate date = localDates.get(position);

        if(date.equals(LocalDate.now())){
            holder.nameDay.setText("Hôm nay");
        }else{
            holder.nameDay.setText(Util.getDayOfWeek(date));
        }

        if(selectedPosition == position){
            //Selected
            selectedItem(holder.day);
        }
        else{
            //Not selected
            notSelectedItem(holder.day);
        }
        holder.day.setText(String.valueOf(date.getDayOfMonth()));

        holder.day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedPosition == position)
                {
                    selectedPosition = 0;
                    notifyDataSetChanged();
                    return;
                }
                selectedPosition = position;
                notifyDataSetChanged();
                iOnClickDateItemListener.onClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(localDates != null) return localDates.size();
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameDay,day;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameDay = itemView.findViewById(R.id.name_day_text_view);
            day = itemView.findViewById(R.id.day_text_view);
        }
    }

    private void selectedItem(TextView textView){
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setColor(context.getColor(R.color.deep_sky_blue));
        textView.setBackground(shape);
        textView.setTextColor(context.getColor(R.color.white));
    }
    private void notSelectedItem(TextView textView){
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setColor(context.getColor(R.color.white));
        textView.setBackground(shape);
        textView.setTextColor(context.getColor(R.color.gray));
    }
}
