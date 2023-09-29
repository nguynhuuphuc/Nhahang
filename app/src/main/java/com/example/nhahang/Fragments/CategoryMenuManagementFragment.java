package com.example.nhahang.Fragments;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nhahang.Adapters.ItemCategoryManagementAdapter;
import com.example.nhahang.Adapters.MenuCategoryAdapter;
import com.example.nhahang.AddNewProductActivity;
import com.example.nhahang.Interfaces.IClickItemMenuCategoryListener;
import com.example.nhahang.Models.MenuCategoryModel;
import com.example.nhahang.Models.MenuModel;
import com.example.nhahang.R;
import com.example.nhahang.TrashActivity;
import com.example.nhahang.databinding.FragmentCategoryMenuManagementBinding;
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
import java.util.Map;


public class CategoryMenuManagementFragment extends Fragment {
    private FragmentCategoryMenuManagementBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseDatabase dbRealtime = FirebaseDatabase.getInstance();
    private List<MenuCategoryModel> menuCategoryModelList;
    private ItemCategoryManagementAdapter menuCategoryAdapter;
    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCategoryMenuManagementBinding.inflate(getLayoutInflater());
        binding.categoryRV.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false));
        menuCategoryModelList = new ArrayList<>();
        menuCategoryAdapter = new ItemCategoryManagementAdapter(menuCategoryModelList,getContext());
        binding.categoryRV.setAdapter(menuCategoryAdapter);
        viewCategory();
        setHasOptionsMenu(true);

        dbRealtime.getReference("categoryChange").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean isChange = snapshot.getValue(Boolean.class);

                if(Boolean.TRUE.equals(isChange)){
                    dbRealtime.getReference("categoryChange").setValue(false);
                    viewCategory();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return binding.getRoot();


    }

    private void viewCategory() {
        db.collection("menucategory").orderBy("id", Query.Direction.ASCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            menuCategoryModelList.clear();
                            for (QueryDocumentSnapshot doc : task.getResult()){
                                MenuCategoryModel model = doc.toObject(MenuCategoryModel.class);
                                if(!model.getId().equals("00")){
                                    model.setDocumentId(doc.getId());
                                    menuCategoryModelList.add(model);
                                }
                            }
                            menuCategoryAdapter.notifyDataSetChanged();
                            inProgress(false);
                        }
                    }
                });

    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.category_management_menu,menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
                toolbar.setNavigationIcon(null);
                return;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
                toolbar.setNavigationIcon(R.drawable.arrow_back_ios_icon);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                menuCategoryAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                menuCategoryAdapter.getFilter().filter(newText);
                return false;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case R.id.add:
                openAddDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void inProgress(boolean isIn){
        if(isIn){
            binding.categoryRV.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        binding.progressBar.setVisibility(View.GONE);
        binding.categoryRV.setVisibility(View.VISIBLE);
    }
    private void openAddDialog(){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_category);
        TextView title = dialog.findViewById(R.id.titleDialog);
        title.setText("Thêm danh mục mới");

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
                if(nameCate.getText().toString().trim().isEmpty()){
                    nameCate.setError("Tên danh mục không được bỏ trống!");
                    return;
                }
                inProgress(true);
                db.collection("menucategory").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    String id;
                                    if(menuCategoryModelList.size() >= 10) id = String.valueOf(menuCategoryModelList.size());
                                    else id = "0"+menuCategoryModelList.size();
                                    Map<String,Object> map = new HashMap<>();
                                    map.put("id",id);
                                    map.put("name",nameCate.getText().toString().trim());
                                    db.collection("menucategory").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            dbRealtime.getReference("categoryChange").setValue(true);
                                            dialog.dismiss();
                                            inProgress(false);
                                            Toast.makeText(getContext(), "Thêm danh mục thành công!", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), "Thêm danh mục không thành công!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
            }
        });

        dialog.show();
    }

}
