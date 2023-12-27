package com.example.nhahang.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nhahang.CustomerInfoActivity;
import com.example.nhahang.Models.CustomerModel;
import com.example.nhahang.Models.Employee;
import com.example.nhahang.R;
import com.example.nhahang.UserInformationManagementActivity;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerManagementAdapter extends RecyclerView.Adapter<CustomerManagementAdapter.ViewHolder> implements Filterable {
    private List<CustomerModel> customerModels,customerModelsOld;
    private Context context;
    private ActivityResultLauncher<Intent> launcher;

    public CustomerManagementAdapter(List<CustomerModel> customerModels, Context context, ActivityResultLauncher<Intent> launcher) {
        this.customerModels = customerModels;
        this.context = context;
        this.launcher = launcher;
        this.customerModelsOld = customerModels;
    }

    public void addEmployee(CustomerModel customerModel){
        customerModels.add(customerModel);
        notifyDataSetChanged();
    }


    public void deleteEmployee(CustomerModel customerModel){
        for (int i = 0 ; i < customerModels.size(); i++){
            if(customerModels.get(i).getUser_uid().equals(customerModel.getUser_uid())){
                customerModels.remove(i);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public void updateEmployee(CustomerModel customerModel){
        for (int i = 0 ; i < customerModels.size(); i++){
            if(customerModels.get(i).getUser_uid().equals(customerModel.getUser_uid())){
                customerModels.set(i,customerModel);
                notifyItemChanged(i);
                break;
            }
        }
    }

    @NonNull
    @Override
    public CustomerManagementAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staff,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerManagementAdapter.ViewHolder holder, int position) {
        CustomerModel user = customerModels.get(position);
        if(user == null) return;
        if (user.isIs_delete()) return;
        if( user.getAvatar() != null && !user.getAvatar().isEmpty()  ){
            Glide.with(context).load(user.getAvatar()).into(holder.avatar);
        }else {
            holder.avatar.setImageResource(R.drawable.user_icon);
        }
        holder.nameUser.setText(user.getFull_name());
        holder.infoUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CustomerInfoActivity.class);
                intent.putExtra("user",user);
                launcher.launch(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return customerModels.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String sSearch = constraint.toString();
                if(sSearch.isEmpty()){
                    customerModels = customerModelsOld;
                }else {
                    List<CustomerModel> list = new ArrayList<>();
                    for (CustomerModel customerModel : customerModelsOld){
                        if(customerModel.getFull_name().toLowerCase().contains(sSearch.toLowerCase())){
                            list.add(customerModel);
                        }
                    }

                    customerModels = list;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = customerModels;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                customerModels = (List<CustomerModel>) results.values;
                notifyDataSetChanged();
            }

        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView avatar;
        TextView nameUser;
        CardView infoUser;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            infoUser = itemView.findViewById(R.id.inforUserCv);
            nameUser = itemView.findViewById(R.id.nameUserTv);

        }
    }
}
