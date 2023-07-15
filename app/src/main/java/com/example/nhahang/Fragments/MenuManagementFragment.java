package com.example.nhahang.Fragments;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nhahang.Adapters.ItemInMenuManagementAdapter;
import com.example.nhahang.Adapters.MenuCategoryAdapter;
import com.example.nhahang.Interfaces.IClickItemMenuCategoryListener;

import com.example.nhahang.Models.MenuCategoryModel;
import com.example.nhahang.Models.MenuModel;
import com.example.nhahang.R;
import com.example.nhahang.databinding.FragmentMenuManagementBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

public class MenuManagementFragment extends Fragment {
    private FragmentMenuManagementBinding binding;
    private String type = "00";
    private List<MenuModel> menuModelsList;
    private ItemInMenuManagementAdapter itemInMenuManagementAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseDatabase dbRealtime = FirebaseDatabase.getInstance();

    private List<MenuCategoryModel> menuCategoryModelList;
    private MenuCategoryAdapter menuCategoryAdapter;
    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMenuManagementBinding.inflate(getLayoutInflater());

        binding.categoryRV.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL,false));
        menuCategoryModelList = new ArrayList<>();
        menuCategoryAdapter = new MenuCategoryAdapter(menuCategoryModelList, new IClickItemMenuCategoryListener() {
            @Override
            public void onClickItemMenuCategoryListener(MenuCategoryModel model) {
                type = model.getId();
                displayItemByType(type);

            }
        });
        binding.categoryRV.setAdapter(menuCategoryAdapter);

        db.collection("menucategory").orderBy("id", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot doc : task.getResult()){
                        MenuCategoryModel  model = doc.toObject(MenuCategoryModel.class);
                        model.setDocumentId(doc.getId());
                        menuCategoryModelList.add(model);
                    }
                    menuCategoryAdapter.notifyDataSetChanged();
                }
            }
        });
        setHasOptionsMenu(true);

//        db.collection("menu").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if(task.isSuccessful()){
//                    for (QueryDocumentSnapshot doc : task.getResult()){
//                        db.collection("menu").document(doc.getId())
//                                .update("isDelete",false);
//                    }
//                }
//            }
//        });

        binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                binding.getRoot().getWindowVisibleDisplayFrame(r);
                int screenHeight = binding.getRoot().getRootView().getHeight();
                int keyboardHeight = screenHeight - r.bottom;
                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);

                if (keyboardHeight > screenHeight * 0.15) {
                    // Bàn phím hiển thị (pop up)
                    // Thực hiện các hành động khi bàn phím hiển thị
                    bottomNavigationView.setVisibility(View.GONE);
                } else {
                    // Bàn phím ẩn (đóng)
                    // Thực hiện các hành động khi bàn phím ẩn

                    Handler handler = new Handler();
                    int delayInMillis = 100;

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Thực hiện thay đổi sự hiển thị của view sau độ trễ
                            // Hoặc View.GONE, View.INVISIBLE tùy theo yêu cầu
                            bottomNavigationView.setVisibility(View.VISIBLE);
                        }
                    }, delayInMillis);
                }
            }
        });


        binding.menuRV.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        menuModelsList = new ArrayList<>();
        itemInMenuManagementAdapter = new ItemInMenuManagementAdapter(menuModelsList,getContext());
        binding.menuRV.setAdapter(itemInMenuManagementAdapter);
        displayItemByType(type);

        dbRealtime.getReference("productManagementChange").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean isChange = snapshot.getValue(Boolean.class);
                if(Boolean.TRUE.equals(isChange)){
                    dbRealtime.getReference("productManagementChange").setValue(false);
                    displayItemByType(type);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return binding.getRoot();
    }

    private void displayItemByType(String type){
        menuModelsList.clear();
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
                                    if (model.getDelete().equals(false)){
                                        menuModelsList.add(model);
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
                                if (model.getDelete().equals(false)){
                                    menuModelsList.add(model);
                                }
                            }
                            itemInMenuManagementAdapter.notifyDataSetChanged();
                            inProgress(false);
                        }
                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.product_management_menu,menu);
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
                displayItemByType(type);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                inProgress(true);
                db.collection("menu")
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    menuModelsList.clear();
                                    for (QueryDocumentSnapshot doc :task.getResult()){
                                        MenuModel model = doc.toObject(MenuModel.class);
                                        model.setDocumentId(doc.getId());
                                        menuModelsList.add(model);
                                    }
                                    itemInMenuManagementAdapter.getFilter().filter(query);
                                    inProgress(false);
                                }
                            }
                        });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                inProgress(true);
                db.collection("menu")
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    menuModelsList.clear();
                                    for (QueryDocumentSnapshot doc :task.getResult()){
                                        MenuModel model = doc.toObject(MenuModel.class);
                                        model.setDocumentId(doc.getId());
                                        menuModelsList.add(model);
                                    }
                                    itemInMenuManagementAdapter.getFilter().filter(newText);
                                    inProgress(false);
                                }
                            }
                        });
                return false;
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
