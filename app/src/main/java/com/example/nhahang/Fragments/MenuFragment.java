package com.example.nhahang.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nhahang.Adapters.MenuAdapter;
import com.example.nhahang.Adapters.MenuCategoryAdapter;
import com.example.nhahang.Interfaces.IClickItemMenuCategoryListener;
import com.example.nhahang.Interfaces.IItemMenu;
import com.example.nhahang.Models.MenuCategoryModel;
import com.example.nhahang.Models.MenuModels;
import com.example.nhahang.R;
import com.example.nhahang.databinding.FragmentMenuBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MenuFragment extends Fragment {

    private FragmentMenuBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<MenuModels> menuModelsList = new ArrayList<>();
    List<MenuCategoryModel> menuCategoryModelList = new ArrayList<>();

    MenuAdapter menuAdapter;
    MenuCategoryAdapter menuCategoryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMenuBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        inProgressFragment(true);

        binding.firstMenuCategoryRv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL,false));
        menuCategoryAdapter = new MenuCategoryAdapter(menuCategoryModelList, new IClickItemMenuCategoryListener() {
            @Override
            public void onClickItemMenuCategoryListener(MenuCategoryModel model) {
                inProgressItem(true);
                ViewMenuByCategory(model.getId());
                inProgressItem(false);

            }
        });
        binding.firstMenuCategoryRv.setAdapter(menuCategoryAdapter);

        db.collection("menucategory")
                .orderBy("id", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                MenuCategoryModel model = document.toObject(MenuCategoryModel.class);
                                menuCategoryModelList.add(0,model);
                            }
                            menuCategoryAdapter.notifyDataSetChanged();
                            inProgressFragment(false);
                        }
                    }
                });

        binding.itemInMenuRv.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
        menuAdapter = new MenuAdapter(menuModelsList, new IItemMenu() {
            @Override
            public void loadImgItem(String img, ImageView imgV) {
                Glide.with(getContext()).load(img).into(imgV);
            }

            @Override
            public void onClickItemMenuListener(MenuModels models) {

            }
        });
        binding.itemInMenuRv.setAdapter(menuAdapter);

        db.collection("menu")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document: task.getResult()){
                                MenuModels model = document.toObject(MenuModels.class);
                                menuModelsList.add(model);
                            }
                            menuAdapter.notifyDataSetChanged();
                        }
                    }
                });


        return view;
    }

    private void ViewMenuByCategory(String id) {

        menuModelsList.clear();

        if(id.equals("00")){
            db.collection("menu")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for (QueryDocumentSnapshot document: task.getResult()){
                                    MenuModels model = document.toObject(MenuModels.class);
                                    menuModelsList.add(model);
                                }
                                menuAdapter.notifyDataSetChanged();
                                return;
                            }
                        }
                    });

        }
        db.collection("menu")
                .whereEqualTo("type",id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                MenuModels model = document.toObject(MenuModels.class);
                                menuModelsList.add(model);
                            }
                            menuAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
    private void inProgressFragment(boolean b) {
        if(b){
            binding.allMenuLl.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);
            return;
        }
        binding.allMenuLl.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);

    }
    private void inProgressItem(boolean b) {
        if(b){
            binding.itemInMenuRv.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);
            return;
        }
        binding.itemInMenuRv.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);

    }
}
