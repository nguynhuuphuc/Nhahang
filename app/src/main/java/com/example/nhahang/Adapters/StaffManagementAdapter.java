package com.example.nhahang.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nhahang.Models.Employee;
import com.example.nhahang.Models.UserModel;
import com.example.nhahang.R;
import com.example.nhahang.UserInformationManagementActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StaffManagementAdapter extends RecyclerView.Adapter<StaffManagementAdapter.ViewHolder> {
    private List<Employee> employees,employeesOld;
    private Context context;

    public StaffManagementAdapter(List<Employee> employees, Context context) {
        this.employees = employees;
        this.context = context;
        this.employeesOld = employees;
    }

    @NonNull
    @Override
    public StaffManagementAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staff,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull StaffManagementAdapter.ViewHolder holder, int position) {
        Employee user = employees.get(position);
        if(user == null) return;
        if( user.getAvatar() != null && !user.getAvatar().isEmpty()  ){
            Glide.with(context).load(user.getAvatar()).into(holder.avatar);
        }
        holder.nameUser.setText(user.getFull_name());
        holder.infoUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserInformationManagementActivity.class);
                intent.putExtra("user",user);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return employees.size();
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
