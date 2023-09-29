package com.example.nhahang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.nhahang.Adapters.MenuAdapter;
import com.example.nhahang.Adapters.MenuCategoryAdapter;
import com.example.nhahang.BottomSheetDialogFragments.NoteProductFragment;
import com.example.nhahang.Interfaces.IClickItemMenuCategoryListener;
import com.example.nhahang.Interfaces.IClickViewInNoteProductListeners;
import com.example.nhahang.Interfaces.IItemMenu;
import com.example.nhahang.Models.MenuCategoryModel;
import com.example.nhahang.Models.MenuModel;
import com.example.nhahang.Models.ProductInReservationModel;
import com.example.nhahang.Models.TableModel;
import com.example.nhahang.Models.VirtualTable;
import com.example.nhahang.Utils.Auth;
import com.example.nhahang.Utils.CircleAnimationUtil;
import com.example.nhahang.Utils.Util;
import com.example.nhahang.databinding.ActivitySelectProductActitvityBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jp.wasabeef.glide.transformations.BlurTransformation;
import render.animations.Render;
import render.animations.Slide;

public class SelectProductActitvity extends AppCompatActivity {

    ActivitySelectProductActitvityBinding binding;
    List<MenuModel> menuModelsList;
    MenuAdapter menuAdapter;
    Intent intent;
    TableModel tableModel;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    MenuCategoryAdapter menuCategoryAdapter;
    List<MenuCategoryModel> menuCategoryModelList;
    Render render = new Render(this);
    Boolean alreadyUp = false;
    String typeId = "00";
    FirebaseDatabase dbRealtime = FirebaseDatabase.getInstance();
    String sActivity = "";
    private SearchView searchView;
    List<VirtualTable> virtualTableList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectProductActitvityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        getData();
        setRv();
        passData();

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        binding.toolbar.getNavigationIcon().setTint(ContextCompat.getColor(this, android.R.color.white));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                supportFinishAfterTransition();
            }
        });

        binding.addToTableCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                putOrderTableToDataBase();
                switch (sActivity){
                    case "Home":
                        dbRealtime.getReference("orders").setValue(true);
                        Intent intent = new Intent(SelectProductActitvity.this,ReservationDetailActivity.class);
                        intent.putExtra("table",tableModel);
                        intent.putExtra("activity",sActivity);
                        startActivity(intent);
                        finish();
                    case "ReservationDetail":
                        dbRealtime.getReference("changeReservationDetail").setValue(true);
                        finish();
                }
            }
        });
        binding.reSelectCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAndPopDown();
                ViewMenuByCategory(typeId);
            }
        });
    }

    private void putOrderTableToDataBase() {
        inProgressItem(true);
        DecimalFormat decimalFormat;
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator(',');
        decimalFormat = new DecimalFormat("#,###",symbols);

        String sCurrentDate, sCurrentTime;
        Calendar c = Calendar.getInstance();
        long totalQuanity = 0;
        double totalPrice = 0;

        SimpleDateFormat format =  new SimpleDateFormat("dd-MM-yyyy");
        sCurrentDate = format.format(c.getTime());
        format = new SimpleDateFormat("hh:mm:ss");
        sCurrentTime = format.format(c.getTime());

        final Map<String,Object> orderMap = new HashMap<>();

        for(VirtualTable vt : virtualTableList){
            totalQuanity += Long.parseLong(vt.getQuantity());
            totalPrice += Double.parseDouble(vt.getTotalPriceProduct().trim().replace(",",""));
            orderMap.put("productId",vt.getDocumentId());
            orderMap.put("productQuantity",vt.getQuantity());
            orderMap.put("productNote",vt.getNote());
            orderMap.put("productDiscountUnint",vt.isDiscountUnit());
            orderMap.put("productDiscountValue",vt.getValueDiscount());
            orderMap.put("productTotalPrice",vt.getTotalPriceProduct());

            db.collection("reservationDetail").document(tableModel.getDocumentId())
                    .collection("products").add(orderMap);
        }

        orderMap.clear();
        orderMap.put("staffId", Auth.User_Uid);
        orderMap.put("currentDate",sCurrentDate);
        orderMap.put("currentTime",sCurrentTime);
        orderMap.put("totalQuantity",String.valueOf(totalQuanity));
        orderMap.put("totalPrice",decimalFormat.format(totalPrice));
        orderMap.put("customerId","");

        db.collection("reservations").document(tableModel.getDocumentId())
                .set(orderMap);

        HashMap<String,Object> update = new HashMap<>();
        update.put("status","not available");
        db.collection("tables").document(tableModel.getDocumentId()).update(update);
        update.clear();
        orderMap.clear();


    }

    private void passData() {
        inProgress(true);
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
                            inProgress(false);
                        }
                    }
                });
        inProgressItem(true);
        db.collection("menu")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document: task.getResult()){
                                MenuModel model = document.toObject(MenuModel.class);
                                model.setDocumentId(document.getId());
                                alreadyAddToTable(model);
                                menuModelsList.add(model);
                            }
                            menuAdapter.notifyDataSetChanged();
                            inProgressItem(false);
                        }
                    }
                });
    }



    private void inProgress(boolean b) {
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

    private void setRv() {
        binding.firstMenuCategoryRv.setLayoutManager(
                new LinearLayoutManager(
                        this,
                        RecyclerView.HORIZONTAL,
                        false
                )
        );
        menuCategoryModelList = new ArrayList<>();
        menuCategoryAdapter = new MenuCategoryAdapter(menuCategoryModelList, new IClickItemMenuCategoryListener() {
            @Override
            public void onClickItemMenuCategoryListener(MenuCategoryModel model) {
                inProgressItem(true);
                typeId = model.getId();
                ViewMenuByCategory(typeId);
                inProgressItem(false);


            }
        });
        binding.firstMenuCategoryRv.setAdapter(menuCategoryAdapter);


        binding.itemInMenuRv.setLayoutManager(
                new LinearLayoutManager(
                        this,
                        RecyclerView.VERTICAL,
                        false));

        menuModelsList = new ArrayList<>();
        menuAdapter = new MenuAdapter(menuModelsList,this, new IItemMenu() {
            @Override
            public void loadImgItem(MenuModel models, ImageView imgV, LinearLayout quantityLl, ImageView checkIv) {
                if(models.isSelected() && !models.isAdded()){
                    new CircleAnimationUtil().attachActivity(SelectProductActitvity.this)
                                    .setTargetView(imgV).setMoveDuration(1000).setDestView(binding.numberDifferentProductTv)
                                    .setAnimationListener(new Animator.AnimatorListener() {
                                        @Override
                                        public void onAnimationStart(@NonNull Animator animation) {
                                            quantityLl.setVisibility(View.VISIBLE);
                                            checkIv.setVisibility(View.VISIBLE);
                                            imgV.setVisibility(View.VISIBLE);
                                            Glide.with(SelectProductActitvity.this)
                                                    .load(models.getImg())
                                                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3)))
                                                    .into(imgV);
                                        }

                                        @Override
                                        public void onAnimationEnd(@NonNull Animator animation) {
                                            binding.numberDifferentProductTv.setText(String.valueOf(virtualTableList.size()));


                                        }

                                        @Override
                                        public void onAnimationCancel(@NonNull Animator animation) {

                                        }

                                        @Override
                                        public void onAnimationRepeat(@NonNull Animator animation) {

                                        }
                                    }).startAnimation();
                    return;
                }
                if(checkIv != null) checkIv.setVisibility(View.GONE);
                if(!models.isAdded()) Glide.with(SelectProductActitvity.this).load(models.getImg()).into(imgV);
            }

            @Override
            public void onClickItemMenuListener(MenuModel models) {
                viewSelectAddToTable(models.isSelected());
                if(models.isSelected() && !models.isAdded()){
                   addToTable(models);
                   return;
               }
                VirtualTable virtualTable = null;
                for(VirtualTable vt : virtualTableList){
                    if(vt.getDocumentId().equals(models.getDocumentId())){
                        virtualTable = vt;
                    }
                }
                NoteProductFragment fragment = new NoteProductFragment(models,virtualTable, new IClickViewInNoteProductListeners() {
                    @Override
                    public void onClickListener(ProductInReservationModel pModel,String command) {
                        switch (command){
                            case "remove":
                                removeTable(models);
                                if (virtualTableList.size() == 0 && alreadyUp){
                                    clearAndPopDown();
                                }
                                ViewMenuByCategory(typeId);
                                break;
                            case "saved":
                                menuAdapter.setVirtualTableList(virtualTableList);
                                menuAdapter.notifyDataSetChanged();
                        }

                    }
                });
                fragment.show(getSupportFragmentManager(),fragment.getTag());



            }

            @Override
            public void onClickPlusListener(MenuModel models,int value) {
                for(VirtualTable vt : virtualTableList){
                    if(vt.getDocumentId().equals(models.getDocumentId())){
                        vt.setQuantity(String.valueOf(value));
                    }
                }
            }

            @Override
            public void onClickMinusListener(MenuModel models, String signal,int value) {
                if(signal.equals("delete")){
                    removeTable(models);
                    binding.numberDifferentProductTv.setText(String.valueOf(virtualTableList.size()));
                }
                else{
                    for(VirtualTable vt : virtualTableList){
                        if(vt.getDocumentId().equals(models.getDocumentId())){
                            vt.setQuantity(String.valueOf(value));
                        }
                    }
                }
                if (virtualTableList.size() == 0 && alreadyUp){
                    clearAndPopDown();
                }

            }

        });
        binding.itemInMenuRv.setAdapter(menuAdapter);
    }

    private void clearAndPopDown() {
        virtualTableList.clear();
        alreadyUp = false;
        render.setAnimation(Slide.OutDown(binding.reSelectCv));
        render.start();
        render.setAnimation(Slide.OutDown(binding.addToTableCv));
        render.start();
    }

    private void removeTable(MenuModel models) {
        models.setSelected(false);
        models.setAdded(false);
        virtualTableList.removeIf(vT -> vT.getDocumentId().equals(models.getDocumentId()));
    }

    private void addToTable(MenuModel models) {
            models.setAdded(true);
            virtualTableList.add(new VirtualTable(models.getDocumentId(),"1",models.getPrice()));
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
                                    alreadyAddToTable(model);
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
                                MenuModel model = document.toObject(MenuModel.class);
                                model.setDocumentId(document.getId());
                                alreadyAddToTable(model);
                                menuModelsList.add(model);
                            }
                            menuAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void alreadyAddToTable(MenuModel model) {
        for(VirtualTable vt : virtualTableList){
            if(vt.getDocumentId().equals(model.getDocumentId())){
                model.setSelected(true);
                model.setAdded(true);
            }
        }
    }


    private void viewSelectAddToTable(boolean b){
        if(b && !alreadyUp){
            alreadyUp = true;
            binding.reSelectCv.setVisibility(View.VISIBLE);
            binding.addToTableCv.setVisibility(View.VISIBLE);
            render.setAnimation(Slide.InUp(binding.reSelectCv));
            render.start();
            render.setAnimation(Slide.InUp(binding.addToTableCv));
            render.start();
            return;
        }
    }




    private void getData() {
        intent = getIntent();
        tableModel = (TableModel) intent.getSerializableExtra("table");
        binding.nameTableTv.setText(tableModel.getId());
        sActivity = intent.getStringExtra("activity");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.searching_menu,menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.toolbar.setNavigationIcon(null);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                binding.toolbar.setNavigationIcon(R.drawable.close_icon);
                inProgressItem(true);
                ViewMenuByCategory(typeId);
                inProgressItem(false);
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                db.collection("menu")
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    menuModelsList.clear();
                                    for (QueryDocumentSnapshot doc :task.getResult()){
                                        MenuModel model = doc.toObject(MenuModel.class);
                                        model.setDocumentId(doc.getId());
                                        alreadyAddToTable(model);
                                        menuModelsList.add(model);
                                    }
                                    menuAdapter.getFilter().filter(query);
                                }
                            }
                        });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                db.collection("menu")
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    menuModelsList.clear();
                                    for (QueryDocumentSnapshot doc :task.getResult()){
                                        MenuModel model = doc.toObject(MenuModel.class);
                                        model.setDocumentId(doc.getId());
                                        alreadyAddToTable(model);
                                        menuModelsList.add(model);
                                    }
                                    menuAdapter.getFilter().filter(newText);
                                }
                            }
                        });
                return false;
            }
        });


        return true;
    }

    @Override
    public void onBackPressed() {
        if(!searchView.isIconified()){
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }
}