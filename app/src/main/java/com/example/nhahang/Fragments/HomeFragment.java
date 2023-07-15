package com.example.nhahang.Fragments;



import android.app.ActivityOptions;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.nhahang.Adapters.CategoryTableAdapter;
import com.example.nhahang.Adapters.TableAdapter;

import com.example.nhahang.Interfaces.IClickItemCategoryTableListener;
import com.example.nhahang.Interfaces.IClickItemTableListener;
import com.example.nhahang.Models.CategoryTableModel;
import com.example.nhahang.Models.TableModel;

import com.example.nhahang.SelectProductActitvity;
import com.example.nhahang.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HomeFragment extends Fragment{

    private FragmentHomeBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseDatabase dbRealtime = FirebaseDatabase.getInstance();
    String location = "01";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();


        binding.typeTableRv.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        List<CategoryTableModel> categoryTableModelList = new ArrayList<>();

        List<TableModel> tableModelList = new ArrayList<>();
        TableAdapter tableAdapter = new TableAdapter(tableModelList, new IClickItemTableListener() {
            @Override
            public void onClickItemTableListener(TableModel tableModel, LinearLayout tablelabelLl,String oldPrice) {
                Intent intent = new Intent(getContext(), SelectProductActitvity.class);
                intent.putExtra("table",tableModel);
                intent.putExtra("activity","Home");
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        requireActivity(),
                        tablelabelLl,
                        Objects.requireNonNull(ViewCompat.getTransitionName(tablelabelLl))
                );
                startActivity(intent,options.toBundle());
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2);
        binding.tableRv.setLayoutManager(gridLayoutManager);
        binding.tableRv.setAdapter(tableAdapter);
        displayTable(location,tableModelList,tableAdapter);

        CategoryTableAdapter categoryTableAdapter = new CategoryTableAdapter(categoryTableModelList, new IClickItemCategoryTableListener() {
            @Override
            public void onClickItemCategoryTableListener(CategoryTableModel categoryTableModel) {
                location = categoryTableModel.getType();
                displayTable(location,tableModelList,tableAdapter);
                InProgress(false);
            }

        });
        dbRealtime.getReference("orders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean isChanged = snapshot.getValue(Boolean.class);
                if(Boolean.TRUE.equals(isChanged)){
                    displayTable(location,tableModelList,tableAdapter);
                    dbRealtime.getReference("orders").setValue(false);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        dbRealtime.getReference("changedResTable").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean isChanged = snapshot.getValue(Boolean.class);
                if(Boolean.TRUE.equals(isChanged)){
                    displayTable(location,tableModelList,tableAdapter);
                    dbRealtime.getReference("changedResTable").setValue(false);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.typeTableRv.setAdapter(categoryTableAdapter);

        InProgress(true);
        db.collection("categorytable")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document: task.getResult()){
                                CategoryTableModel model = document.toObject(CategoryTableModel.class);
                                categoryTableModelList.add(model);
                            }
                            categoryTableAdapter.notifyDataSetChanged();
                            InProgress(false);
                        }
                    }
                });


        return view;
    }

    void InProgress(boolean isIn){
        if(isIn){
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.tableRv.setVisibility(View.GONE);
        }
        else{
            binding.progressBar.setVisibility(View.GONE);
            binding.tableRv.setVisibility(View.VISIBLE);
        }

    }

    private void displayTable(String location, List<TableModel> tableModelList, TableAdapter tableAdapter) {
        InProgress(true);
        tableModelList.clear();
        db.collection("tables")
                .orderBy("id", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            tableModelList.clear();
                            for (QueryDocumentSnapshot documet: task.getResult()){
                                TableModel model = documet.toObject(TableModel.class);
                                if(model.getLocation().equals(location) && model.getStatus().trim().isEmpty()){
                                    model.setDocumentId(documet.getId());
                                    tableModelList.add(0,model);
                                }
                            }
                            tableAdapter.notifyDataSetChanged();
                            InProgress(false);
                        }
                    }
                });

    }
    void InsertTable(){
        HashMap<String,Object> table = new HashMap<>();
        for (int i = 1; i < 11; i++) {
            String id;
            if(i == 10) id = String.valueOf(i);
            else id = "0"+i;
            table.put("id",id);
            table.put("description","");
            table.put("status"," ");
            table.put("location","01");
            db.collection("tables")
                    .add(table)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(getContext(), documentReference.getId(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }


        HashMap<String,Object> table2 = new HashMap<>();
        for (int i = 11; i < 21; i++) {
            table.put("id",String.valueOf(i));
            table.put("description","");
            table.put("status"," ");
            table.put("location","02");
            db.collection("tables")
                    .add(table)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(getContext(), documentReference.getId(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }
}
