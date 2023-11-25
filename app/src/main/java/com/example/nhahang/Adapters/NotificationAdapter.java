package com.example.nhahang.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nhahang.Models.Employee;
import com.example.nhahang.Models.NotificationModel;
import com.example.nhahang.R;
import com.example.nhahang.Utils.Util;
import com.google.firebase.firestore.auth.User;

import java.text.DateFormat;
import java.util.Formatter;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_LOADING =2 ;

    private List<NotificationModel> notificationModels;
    private List<Employee> employees;
    private Context context;
    private boolean isLoadingAdd;

    public NotificationAdapter(List<Employee> employees,Context context) {
        this.employees = employees;
        this.context = context;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<NotificationModel> notificationModels){
        this.notificationModels = notificationModels;
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        if(notificationModels != null && position == notificationModels.size() - 1 &&  isLoadingAdd){
            return TYPE_LOADING;
        }
        return TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(TYPE_ITEM == viewType){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification,parent,false);
            return new NotificationViewHolder(view);
        }else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_loading,parent,false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder.getItemViewType() == TYPE_ITEM){
            NotificationModel notifyItem = notificationModels.get(position);
            NotificationViewHolder notifyHolder = (NotificationViewHolder) holder;
            NotificationModel.Message messageObject = notifyItem.parseMessage();
            SpannableString created_by = null;
            for (Employee employee : employees){
                if(employee.getUser_uid().equals(notifyItem.getCreated_by())){
                    created_by = new SpannableString(employee.getFull_name());
                    created_by.setSpan(new StyleSpan(Typeface.BOLD), 0, employee.getFull_name().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                }
            }
            CharSequence combinedText = TextUtils.concat(created_by, " ", messageObject.getStatus());
            notifyHolder.messageTv.setText(combinedText);
            notifyHolder.timeTv.setText(Util.DayTimeFormatting(notifyItem.getNotify_time()));

        }

    }

    @Override
    public int getItemCount() {
        if(notificationModels != null) return notificationModels.size();
        return 0;
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder{

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {
        private TextView messageTv, timeTv;
        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            messageTv = itemView.findViewById(R.id.messageTv);
            timeTv = itemView.findViewById(R.id.createdTimeTv);
        }
    }

    public void addFooterLoading(){
        isLoadingAdd = true;
        notificationModels.add(new NotificationModel());
    }

    public void removeFooterLoading(){
        isLoadingAdd = false;
        int position = notificationModels.size() -1;
        NotificationModel notifyItem = notificationModels.get(position);
        if(notifyItem != null){
            notificationModels.remove(position);
            notifyItemRemoved(position);
        }
    }

}
