package com.example.nhahang.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.nhahang.Models.Employee;
import com.example.nhahang.Models.MessageModel;
import com.example.nhahang.R;
import com.example.nhahang.Utils.Auth;
import com.example.nhahang.Utils.Util;

import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int TYPE_SEND_MESSAGE = 0;
    private static final int TYPE_RECEIVED_MESSAGE = 1;
    private static final int TYPE_TIMESTAMP = 2;
    private static final int TYPE_RECEIVED_MESSAGE_STAFF = 3;
    private Employee employeeSend;

    private List<MessageModel> messageModelList;
    private List<Employee> employees;

    public MessageAdapter() {
    }

    public MessageAdapter(List<Employee> employees) {
        this.employees = employees;
    }


    public void setData(List<MessageModel> messageModelList){
        this.messageModelList = messageModelList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(TYPE_SEND_MESSAGE == viewType){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_send_message,parent,false);
            return new SendMessageViewHolder(view);
        }else if(TYPE_RECEIVED_MESSAGE == viewType || TYPE_RECEIVED_MESSAGE_STAFF == viewType){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_received_message,parent,false);
            return new ReceivedMessageViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel message = messageModelList.get(position);
        if (message == null )return;

        if(TYPE_SEND_MESSAGE == holder.getItemViewType()){
            SendMessageViewHolder sendMessageViewHolder = (SendMessageViewHolder) holder;
            sendMessageViewHolder.sendTv.setText(message.getContent());
            String sTime = Util.dateTimeInMessageFormatting(message.getTimestamp());
            sendMessageViewHolder.timeTv.setText(sTime);
        }else if(TYPE_RECEIVED_MESSAGE == holder.getItemViewType()){
            ReceivedMessageViewHolder receivedMessageViewHolder = (ReceivedMessageViewHolder) holder;
            String sName = message.getFull_name() +"(KH)";
            receivedMessageViewHolder.nameTv.setText(sName);
            receivedMessageViewHolder.receivedTv.setText(message.getContent());
            String sTime = Util.dateTimeInMessageFormatting(message.getTimestamp());
            receivedMessageViewHolder.timeTv.setText(sTime);
        }else if(TYPE_RECEIVED_MESSAGE_STAFF == holder.getItemViewType()){
            ReceivedMessageViewHolder receivedMessageViewHolder = (ReceivedMessageViewHolder) holder;
            String sName = employeeSend.getFull_name() +"("+employeeSend.getPosition_id()+")";
            receivedMessageViewHolder.nameTv.setText(sName);
            receivedMessageViewHolder.receivedTv.setText(message.getContent());
            String sTime = Util.dateTimeInMessageFormatting(message.getTimestamp());
            receivedMessageViewHolder.timeTv.setText(sTime);
        }

    }

    @Override
    public int getItemCount() {
        if(messageModelList != null) return messageModelList.size();
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        MessageModel message = messageModelList.get(position);

        for(Employee employee : employees){
            if(employee.getEmployee_id() == message.getSender_id()){
                if(employee.getUser_uid().equals(Auth.User_Uid)){
                    return TYPE_SEND_MESSAGE;
                }else{
                    employeeSend = employee;
                    return TYPE_RECEIVED_MESSAGE_STAFF;
                }
            }
            if(employee.getEmployee_id() == message.getReceiver_id()){
                return TYPE_RECEIVED_MESSAGE;
            }
        }
        return TYPE_RECEIVED_MESSAGE;
    }

    public static class SendMessageViewHolder extends RecyclerView.ViewHolder{
        private TextView sendTv,timeTv;
        public SendMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            sendTv = itemView.findViewById(R.id.sendTv);
            timeTv = itemView.findViewById(R.id.timeTv);
        }
    }

    public static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder{
        private TextView receivedTv,nameTv,timeTv;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            receivedTv = itemView.findViewById(R.id.receivedTv);
            nameTv = itemView.findViewById(R.id.nameTv);
            timeTv = itemView.findViewById(R.id.timeTv);
        }
    }
}
