package com.example.nhahang.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import com.example.nhahang.Models.MenuModel;
import com.example.nhahang.Models.MessageEvent;
import com.example.nhahang.Models.NotificationModel;
import com.example.nhahang.databinding.FragmentMenuBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MenuFragment extends Fragment {

    private FragmentMenuBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<MenuModel> menuModelsList = new ArrayList<>();
    List<MenuCategoryModel> menuCategoryModelList = new ArrayList<>();
    private String cateId = "00";
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
                cateId = model.getId();
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
        menuAdapter = new MenuAdapter(menuModelsList,getContext(), new IItemMenu() {
            @Override
            public void loadImgItem(MenuModel models, ImageView imgV, LinearLayout quantityLl, ImageView checkIv) {
                Glide.with(getActivity()).load(models.getImg()).into(imgV);
            }

            @Override
            public void onClickItemMenuListener(MenuModel models,int position) {

            }

            @Override
            public void onClickPlusListener(MenuModel models,int value) {

            }

            @Override
            public void onClickMinusListener(MenuModel models, String signal,int value) {

            }

        });
        binding.itemInMenuRv.setAdapter(menuAdapter);

        ViewMenuByCategory(cateId);

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
                                    MenuModel model = document.toObject(MenuModel.class);
                                    model.setDocumentId(document.getId());
                                    if(document.contains("isDelete")){
                                        model.setDelete(document.getBoolean("isDelete"));
                                    }
                                    if (model.getDelete().equals(false)){
                                        menuModelsList.add(model);
                                    }

                                }
                                menuAdapter.notifyDataSetChanged();
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
                            for (QueryDocumentSnapshot document: task.getResult()){
                                MenuModel model = document.toObject(MenuModel.class);
                                model.setDocumentId(document.getId());
                                if(document.contains("isDelete")){
                                    model.setDelete(document.getBoolean("isDelete"));
                                }
                                if (model.getDelete().equals(false)){
                                    menuModelsList.add(model);
                                }

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

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Boolean isChange) {
        if(isChange){
            ViewMenuByCategory(cateId);
        }
        EventBus.getDefault().removeStickyEvent(isChange);
    }
}
