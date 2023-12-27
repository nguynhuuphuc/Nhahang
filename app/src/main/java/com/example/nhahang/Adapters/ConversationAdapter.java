package com.example.nhahang.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nhahang.Models.ConversationModel;
import com.example.nhahang.Models.Employee;
import com.example.nhahang.Models.MessageModel;
import com.example.nhahang.R;
import com.example.nhahang.Utils.Auth;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> implements Filterable {
    private Context context;
    private List<ConversationModel> conversationModels;
    private List<ConversationModel> conversationModelsOld;
    private List<Employee> employees;
    private OnClickItemListener onClickItemListener;



    public ConversationAdapter(Context context, List<ConversationModel> conversationModels, List<Employee> employees) {
        this.context = context;
        this.conversationModels = conversationModels;
        this.conversationModelsOld = conversationModels;
        this.employees = employees;
    }

    public void setOnClickItemListener(OnClickItemListener onClickItemListener) {
        this.onClickItemListener = onClickItemListener;
    }


    public interface OnClickItemListener{
        void onClick(ConversationModel conversation);
    }

    @NonNull
    @Override
    public ConversationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationAdapter.ViewHolder holder, int position) {
        ConversationModel model = conversationModels.get(position);
        if(model == null) return;

        if(model.getAvatar() == null || model.getAvatar().isEmpty()){
            holder.avatar.setImageResource(R.drawable.user_icon);
        }else {
            Glide.with(context).load(model.getAvatar()).into(holder.avatar);
        }
        holder.conservationName.setText(model.getFull_name());


        if(model.getSender_id() == model.getCustomer_id()) {
            holder.senderTv.setText(model.getFull_name());
        }else{
            for (Employee employee : employees){
                if(model.getSender_id() == employee.getEmployee_id()){
                    if(employee.getUser_uid().equals(Auth.User_Uid)) {
                        holder.senderTv.setText("Bạn");
                    }else{
                        holder.senderTv.setText(employee.getFull_name());
                    }
                    break;
                }
            }
        }
        holder.lastMessageTv.setText(model.getLastMessage());

        if(model.getUn_read()>0){
            holder.quantityNewMessageTv.setVisibility(View.VISIBLE);
            holder.quantityNewMessageTv.setText(String.valueOf(model.getUn_read()));
            holder.lastMessageTv.setTypeface(null, Typeface.BOLD);
        }else {
            holder.quantityNewMessageTv.setVisibility(View.INVISIBLE);
            holder.lastMessageTv.setTypeface(null, Typeface.NORMAL);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickItemListener.onClick(model);
            }
        });

    }
    public void updateItemConversationRead(int conversation_id){
        for(ConversationModel model : conversationModels){
            if(model.getId() == conversation_id){
                model.setUn_read(0);
                notifyItemChanged(conversationModels.indexOf(model));
                return;
            }
        }

    }

    public void updateItemConversation(MessageModel messageModel){
        for(ConversationModel model : conversationModels){
            if(model.getId() == messageModel.getConversation_id()){
                model.setContent(messageModel.getContent());
                model.setUn_read(model.getUn_read()+1);
                notifyItemChanged(conversationModels.indexOf(model));
                return;
            }
        }

    }

    @Override
    public int getItemCount() {
        if(conversationModels != null) return conversationModels.size();
        return 0;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView avatar;
        private TextView conservationName,senderTv,lastMessageTv,quantityNewMessageTv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            conservationName = itemView.findViewById(R.id.conservationName);
            senderTv = itemView.findViewById(R.id.senderTv);
            lastMessageTv =itemView.findViewById(R.id.lastMessageTv);
            quantityNewMessageTv = itemView.findViewById(R.id.quantityNewMessageTv);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String sSearch = constraint.toString();
                if(sSearch.isEmpty()){
                    conversationModels = conversationModelsOld;
                }else{
                    List<ConversationModel> list = new ArrayList<>();
                    for(ConversationModel model : conversationModelsOld){
                        if(model.getFull_name().toLowerCase().contains(sSearch.toLowerCase())){
                            list.add(model);
                        }
                    }
                    conversationModels = list;

                }

                FilterResults results = new FilterResults();
                results.values = conversationModels;

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                conversationModels = (List<ConversationModel>) results.values;
                notifyDataSetChanged();

            }
        };
    }

}
