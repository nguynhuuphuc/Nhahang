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
import com.example.nhahang.Models.Employee;
import com.example.nhahang.Models.MenuModel;
import com.example.nhahang.Models.UserModel;
import com.example.nhahang.R;
import com.example.nhahang.UserInformationManagementActivity;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StaffManagementAdapter extends RecyclerView.Adapter<StaffManagementAdapter.ViewHolder> implements Filterable {
    private List<Employee> employees,employeesOld;
    private Context context;
    private ActivityResultLauncher<Intent> launcher;

    public StaffManagementAdapter(List<Employee> employees, Context context,ActivityResultLauncher<Intent> launcher) {
        this.employees = employees;
        this.context = context;
        this.launcher = launcher;
        this.employeesOld = employees;
    }

    public void addEmployee(Employee employee){
        employees.add(employee);
        notifyDataSetChanged();
    }


    public void deleteEmployee(Employee employee){
        for (int i = 0 ; i < employees.size(); i++){
            if(employees.get(i).getUser_uid().equals(employee.getUser_uid())){
                employees.remove(i);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public void updateEmployee(Employee employee){
        for (int i = 0 ; i < employees.size(); i++){
            if(employees.get(i).getUser_uid().equals(employee.getUser_uid())){
                employees.set(i,employee);
                notifyItemChanged(i);
                break;
            }
        }
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
                Intent intent = new Intent(context, UserInformationManagementActivity.class);
                intent.putExtra("user",user);
                launcher.launch(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String sSearch = constraint.toString();
                if(sSearch.isEmpty()){
                    employees = employeesOld;
                }else {
                    List<Employee> list = new ArrayList<>();
                    for (Employee employee : employeesOld){
                        if(employee.getFull_name().toLowerCase().contains(sSearch.toLowerCase())){
                            list.add(employee);
                        }
                    }

                    employees = list;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = employees;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                employees = (List<Employee>) results.values;
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
