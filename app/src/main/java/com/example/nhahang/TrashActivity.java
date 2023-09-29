package com.example.nhahang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.hardware.lights.LightState;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.nhahang.Adapters.ItemInMenuManagementAdapter;
import com.example.nhahang.Adapters.MenuAdapter;
import com.example.nhahang.Adapters.MenuCategoryAdapter;
import com.example.nhahang.Interfaces.IClickItemMenuCategoryListener;
import com.example.nhahang.Interfaces.IItemMenu;
import com.example.nhahang.Models.MenuCategoryModel;
import com.example.nhahang.Models.MenuModel;
import com.example.nhahang.databinding.ActivityTrashBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TrashActivity extends AppCompatActivity {
    private ActivityTrashBinding binding;
    private List<MenuCategoryModel> menuCategoryModelList;
    private List<MenuModel> menuModelList;
    private MenuCategoryAdapter menuCategoryAdapter;
    private ItemInMenuManagementAdapter itemInMenuManagementAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseDatabase dbRealtime = FirebaseDatabase.getInstance();
    private String typeCate = "00";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTrashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        menuCategoryModelList = new ArrayList<>();
        menuCategoryAdapter = new MenuCategoryAdapter(menuCategoryModelList, new IClickItemMenuCategoryListener() {
            @Override
            public void onClickItemMenuCategoryListener(MenuCategoryModel model) {
                typeCate = model.getId();
                displayItemByType(typeCate);

            }
        });
        binding.categoryRV.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        binding.categoryRV.setAdapter(menuCategoryAdapter);
        viewCategory();

        menuModelList = new ArrayList<>();
        itemInMenuManagementAdapter = new ItemInMenuManagementAdapter(menuModelList,this);
        binding.menuRV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));
        binding.menuRV.setAdapter(itemInMenuManagementAdapter);
        displayItemByType(typeCate);

        dbRealtime.getReference("trashChange").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean isChange = snapshot.getValue(Boolean.class);
                if(Boolean.TRUE.equals(isChange)){
                    dbRealtime.getReference("trashChange").setValue(false);
                    dbRealtime.getReference("productManagementChange").setValue(true);
                    displayItemByType(typeCate);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    private void displayItemByType(String type){
        menuModelList.clear();
        inProgress(true);
        if(type.equals("00")){
            db.collection("menu")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for (QueryDocumentSnapshot doc : task.getResult()){
                                    MenuModel model = doc.toObject(MenuModel.class);
                                    model.setDocumentId(doc.getId());
                                    if(doc.contains("isDelete")){
                                        model.setDelete(doc.getBoolean("isDelete"));
                                    }else{
                                        db.collection("menu").document(doc.getId())
                                                .update("isDelete",false);
                                        model.setDelete(false);
                                    }
                                    if (model.getDelete().equals(true)){
                                        menuModelList.add(model);
                                    }
                                }
                                itemInMenuManagementAdapter.notifyDataSetChanged();
                                inProgress(false);
                            }
                        }
                    });
            return;
        }
        db.collection("menu").whereEqualTo("type",type)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot doc : task.getResult()){
                                MenuModel model = doc.toObject(MenuModel.class);
                                model.setDocumentId(doc.getId());
                                if(doc.contains("isDelete")){
                                    model.setDelete(doc.getBoolean("isDelete"));
                                }else{
                                    db.collection("menu").document(doc.getId())
                                            .update("isDelete",false);
                                    model.setDelete(false);
                                }
                                if (model.getDelete().equals(true)){
                                    menuModelList.add(model);
                                }
                            }
                            itemInMenuManagementAdapter.notifyDataSetChanged();
                            inProgress(false);
                        }
                    }
                });
    }

    private void viewCategory(){
        db.collection("menucategory").orderBy("id", Query.Direction.ASCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot doc : task.getResult()){
                                MenuCategoryModel model = doc.toObject(MenuCategoryModel.class);
                                model.setDocumentId(doc.getId());
                                menuCategoryModelList.add(model);
                            }
                            menuCategoryAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
    private void inProgress(boolean isIn){
        if(isIn){
            binding.content.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        binding.progressBar.setVisibility(View.GONE);
        binding.content.setVisibility(View.VISIBLE);
    }

}