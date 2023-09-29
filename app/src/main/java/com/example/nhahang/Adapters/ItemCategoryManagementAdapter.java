package com.example.nhahang.Adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nhahang.Models.MenuCategoryModel;
import com.example.nhahang.Models.MenuModel;
import com.example.nhahang.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ItemCategoryManagementAdapter extends RecyclerView.Adapter<ItemCategoryManagementAdapter.ViewHolder> implements Filterable {
    private List<MenuCategoryModel> menuCategoryModelList,menuCategoryModelListOld;
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseDatabase dbRealtime = FirebaseDatabase.getInstance();

    public ItemCategoryManagementAdapter(List<MenuCategoryModel> menuCategoryModelList, Context context) {
        this.menuCategoryModelList = menuCategoryModelList;
        this.context = context;
        this.menuCategoryModelListOld = menuCategoryModelList;
    }

    @NonNull
    @Override
    public ItemCategoryManagementAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_management,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemCategoryManagementAdapter.ViewHolder holder, int position) {
        MenuCategoryModel model = menuCategoryModelList.get(position);
        if(model == null) return;
        holder.nameCate.setText(model.getName());

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditDialog(model);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkForDelete(model);
            }
        });
    }


    @Override
    public int getItemCount() {
        return menuCategoryModelList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String sSearch = constraint.toString();
                if(sSearch.isEmpty()){
                    menuCategoryModelList = menuCategoryModelListOld;
                }else{
                    List<MenuCategoryModel> list = new ArrayList<>();
                    for (MenuCategoryModel menuModel : menuCategoryModelList){
                        if(menuModel.getName().toLowerCase().contains(sSearch.toLowerCase())){
                            list.add(menuModel);
                        }
                    }
                    menuCategoryModelList = list;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = menuCategoryModelList;
                filterResults.count = menuCategoryModelList.size();

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                menuCategoryModelList = (List<MenuCategoryModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameCate;
        CardView edit,delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameCate = itemView.findViewById(R.id.nameCate);
            edit = itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);
        }
    }
    private void warningDialog(MenuCategoryModel model) {
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Bạn có muốn xóa "+ model.getName() +" không?")
                .setTitle("Xóa");
        builder.setPositiveButton("Vẫn Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.collection("menucategory").document(model.getDocumentId())
                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                dbRealtime.getReference("categoryChange").setValue(true);
                                Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dia) {
                // Truy cập và tùy chỉnh nút PositiveButton
                Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setBackgroundColor(Color.RED);
                positiveButton.setTextColor(Color.WHITE);
            }
        });
        dialog.show();
    }

    private void checkForDelete(MenuCategoryModel model) {
        db.collection("menu").whereEqualTo("type",model.getId())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().isEmpty()){
                                warningDialog(model);
                            }else{
                                openNotifyCantDelete();
                            }
                        }
                    }
                });

    }
    private void openNotifyCantDelete(){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.diaglog_not_allow_to_delete);

        Window window = dialog.getWindow();
        if(window == null){
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttribute = window.getAttributes();
        windowAttribute.gravity = Gravity.CENTER;

        window.setAttributes(windowAttribute);

        dialog.setCancelable(true);

        CardView ok = dialog.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    private void openEditDialog(MenuCategoryModel model){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_category);

        Window window = dialog.getWindow();
        if(window == null){
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttribute = window.getAttributes();
        windowAttribute.gravity = Gravity.CENTER;

        window.setAttributes(windowAttribute);

        dialog.setCancelable(true);

        EditText nameCate = dialog.findViewById(R.id.nameCateEt);
        nameCate.setText(model.getName());

        CardView cancel = dialog.findViewById(R.id.cancel);
        CardView save = dialog.findViewById(R.id.save);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("menucategory").document(model.getDocumentId())
                        .update("name",nameCate.getText().toString().trim())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                dbRealtime.getReference("categoryChange").setValue(true);
                                dbRealtime.getReference("productManagementChange").setValue(true);
                                dialog.dismiss();
                                Toast.makeText(context, "Thay đổi thành công", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Thay đổi không thành công", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        dialog.show();
    }
}
