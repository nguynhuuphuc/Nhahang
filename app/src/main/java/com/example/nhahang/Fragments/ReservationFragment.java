package com.example.nhahang.Fragments;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.nhahang.R;
import com.example.nhahang.ReservationDetailActivity;
import com.example.nhahang.SelectProductActitvity;
import com.example.nhahang.databinding.FragmentHomeBinding;
import com.example.nhahang.databinding.FragmentReservationBinding;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ReservationFragment extends Fragment {
    private FragmentHomeBinding binding;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseDatabase dbRealtime = FirebaseDatabase.getInstance();
    private String typeTb = "01";

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
                Intent intent = new Intent(getContext(), ReservationDetailActivity.class);
                intent.putExtra("table",tableModel);
                intent.putExtra("oldPrice",oldPrice);
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
        displayTable(typeTb,tableModelList,tableAdapter);

        CategoryTableAdapter categoryTableAdapter = new CategoryTableAdapter(categoryTableModelList, new IClickItemCategoryTableListener() {
            @Override
            public void onClickItemCategoryTableListener(CategoryTableModel categoryTableModel) {
                typeTb = categoryTableModel.getType();
                displayTable(typeTb,tableModelList,tableAdapter);
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
        dbRealtime.getReference("orders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean isChanged = snapshot.getValue(Boolean.class);
                if(Boolean.TRUE.equals(isChanged)){
                    dbRealtime.getReference("changedResTable").setValue(false);
                    displayTable(typeTb,tableModelList,tableAdapter);
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
                    dbRealtime.getReference("changedResTable").setValue(false);
                    displayTable(typeTb,tableModelList,tableAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        dbRealtime.getReference("updateRes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean isChanged = snapshot.getValue(Boolean.class);
                if(Boolean.TRUE.equals(isChanged)){
                    dbRealtime.getReference("updateRes").setValue(false);
                    displayTable(typeTb,tableModelList,tableAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                                if(model.getLocation().equals(location) && model.getStatus().equals("not available"))
                                {
                                    model.setDocumentId(documet.getId());
                                    tableModelList.add(0, model);
                                }
                            }
                            tableAdapter.notifyDataSetChanged();
                            InProgress(false);
                        }
                    }
                });

    }

}