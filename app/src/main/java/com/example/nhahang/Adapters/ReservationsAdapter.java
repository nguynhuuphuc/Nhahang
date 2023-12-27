package com.example.nhahang.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nhahang.Models.ReservationModel;
import com.example.nhahang.Models.TableModel;
import com.example.nhahang.R;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;

public class ReservationsAdapter extends RecyclerView.Adapter<ReservationsAdapter.ViewHolder> {
    private Context context;
    private List<ReservationModel> reservations;
    private OnClickItemListener onClickItemListener;

    public ReservationsAdapter(Context context, List<ReservationModel> reservations) {
        this.context = context;
        this.reservations = reservations;
    }

    public void setOnClickItemListener(OnClickItemListener onClickItemListener) {
        this.onClickItemListener = onClickItemListener;
    }

    public interface OnClickItemListener{
        void onClick(int position);
    }
    public void sortItem(){
        reservations.sort(Comparator.comparing((ReservationModel obj) -> {
            if ("Chờ xác nhận".equals(obj.getStatus())) {
                return "";  // Đặt "" để đưa lên đầu
            } else {
                return obj.getStatus();
            }
        }).thenComparing(ReservationModel::getUpdate_time));

        notifyDataSetChanged();
    }

    public void updateItem(ReservationModel model){
        for(ReservationModel item : reservations){
            if (item.getId() == model.getId()){
                int index = reservations.indexOf(item);
                reservations.set(index,model);
                notifyItemChanged(index);
                return;
            }
        }
    }

    @NonNull
    @Override
    public ReservationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reservation,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ReservationModel reservation = reservations.get(position);
        if(reservation == null  ) return;
        if( reservation.getTable_id() == -1){
            holder.nameTable.setText("Chưa có bàn");
        }else{
            String nameTable = "Bàn số "+reservation.getTable_id();
            holder.nameTable.setText(nameTable);
        }
        holder.status.setText(reservation.getStatus());
        if(reservation.getStatus().equals("Chờ xác nhận")){
            //red
            holder.cardView.setCardBackgroundColor(context.getColor(R.color.lighter_red));
        }else{
            //green
            holder.cardView.setCardBackgroundColor(context.getColor(R.color.lighter_green));
        }
        holder.customerId.setText(String.valueOf(reservation.getCustomer_id()));
        Instant instant = reservation.getReservation_time().toInstant();
        LocalDateTime localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        holder.resTime.setText(localDateTime.toLocalTime().toString());
        holder.customerName.setText(reservation.getCustomer_name());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onClickItemListener != null){
                    onClickItemListener.onClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(reservations != null) return reservations.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTable,resTime,customerId,customerName,status;
        private CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTable = itemView.findViewById(R.id.tableNameTv);
            resTime = itemView.findViewById(R.id.timeTv);
            customerId = itemView.findViewById(R.id.customer_id_tv);
            customerName = itemView.findViewById(R.id.customerNameTv);
            status = itemView.findViewById(R.id.statusTv);
            cardView = itemView.findViewById(R.id.cardview);

        }
    }
}
