package com.example.nhahang.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nhahang.EditProductActivity;
import com.example.nhahang.Models.MenuModel;
import com.example.nhahang.R;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ItemInMenuManagementAdapter extends RecyclerView.Adapter<ItemInMenuManagementAdapter.ViewHolder> implements Filterable {
    private List<MenuModel> menuModelsList;
    private Context context;
    private AlertDialog dialog;
    private List<MenuModel> menuModelsListOld;


    public ItemInMenuManagementAdapter(List<MenuModel> menuModelsList, Context context) {
        this.menuModelsList = menuModelsList;
        this.context = context;
        this.menuModelsListOld = menuModelsList;
    }

    @NonNull
    @Override
    public ItemInMenuManagementAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_in_menu_management,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemInMenuManagementAdapter.ViewHolder holder, int position) {
        MenuModel model = menuModelsList.get(position);
        if (model == null) return;
        if(!model.getImg().trim().isEmpty()) Glide.with(context).load(model.getImg()).into(holder.imageView);
        holder.nameP.setText(model.getName());
        holder.priceP.setText(model.getPrice());
        try {
            if(model.getDelete().equals(true)){
                holder.trashLayout.setVisibility(View.VISIBLE);
                holder.defaultLayout.setVisibility(View.GONE);
            }
        }catch (Exception ignored){}

        holder.restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseDatabase dbRealtime = FirebaseDatabase.getInstance();
                db.collection("menu").document(model.getDocumentId())
                        .update("isDelete",false);
                dbRealtime.getReference("trashChange").setValue(true);
            }
        });
        holder.forceDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildDialogForceDelete(model);
                dialog.show();
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditProductActivity.class);
                intent.putExtra("product",model);
                context.startActivity(intent);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildDialog(model);
                dialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return menuModelsList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView nameP,priceP;
        private CardView edit,delete,restore,forceDelete;
        private LinearLayout defaultLayout, trashLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageItemInMenuIv);
            nameP = itemView.findViewById(R.id.nameProductTv);
            priceP = itemView.findViewById(R.id.priceProductTv);
            edit = itemView.findViewById(R.id.editProduct);
            delete = itemView.findViewById(R.id.deleteProduct);
            defaultLayout = itemView.findViewById(R.id.defaultLayout);
            trashLayout = itemView.findViewById(R.id.layout4Strash);
            restore = itemView.findViewById(R.id.restoreProduct);
            forceDelete = itemView.findViewById(R.id.forceDeleteProduct);

        }
    }
    private void buildDialog(MenuModel model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Bạn có muốn xóa "+ model.getName() +" không?")
                .setTitle("Xóa");
        builder.setPositiveButton("Vẫn Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseFirestore.getInstance().collection("menu").document(model.getDocumentId())
                        .update("isDelete",true).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "Xóa thành công!", Toast.LENGTH_SHORT).show();
                                FirebaseDatabase.getInstance().getReference("productManagementChange").setValue(true);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Xóa không thành công!", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
    }

    private void buildDialogForceDelete(MenuModel model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Bạn có muốn xóa "+ model.getName() +" không?")
                .setTitle("Xóa");
        builder.setPositiveButton("Vẫn Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseFirestore.getInstance().collection("menu").document(model.getDocumentId())
                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                menuModelsList.remove(model);
                                notifyDataSetChanged();
                                Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Xóa không thành công", Toast.LENGTH_SHORT).show();

                            }
                        });

            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String sSearch = constraint.toString();
                if(sSearch.isEmpty()){
                    menuModelsList = menuModelsListOld;
                }else{
                    List<MenuModel> list = new ArrayList<>();
                    for (MenuModel menuModel : menuModelsListOld){
                        if(menuModel.getName().toLowerCase().contains(sSearch.toLowerCase())){
                            list.add(menuModel);
                        }
                    }
                    menuModelsList = list;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = menuModelsList;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                menuModelsList = (List<MenuModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
