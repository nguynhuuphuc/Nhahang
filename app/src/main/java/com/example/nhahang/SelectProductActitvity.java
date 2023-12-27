package com.example.nhahang;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.nhahang.Adapters.MenuAdapter;
import com.example.nhahang.Adapters.MenuCategoryAdapter;
import com.example.nhahang.BottomSheetDialogFragments.NoteProductFragment;
import com.example.nhahang.Interfaces.ApiService;
import com.example.nhahang.Interfaces.IClickItemMenuCategoryListener;
import com.example.nhahang.Interfaces.IClickViewInNoteProductListeners;
import com.example.nhahang.Interfaces.IItemMenu;

import com.example.nhahang.Models.MenuCategoryModel;
import com.example.nhahang.Models.MenuModel;
import com.example.nhahang.Models.OrderItemModel;
import com.example.nhahang.Models.ProductInReservationModel;
import com.example.nhahang.Models.Requests.OrderRequest;
import com.example.nhahang.Models.Respones.ServerResponse;
import com.example.nhahang.Models.TableModel;
import com.example.nhahang.Models.DishModel;
import com.example.nhahang.Utils.Auth;
import com.example.nhahang.Utils.CircleAnimationUtil;

import com.example.nhahang.databinding.ActivitySelectProductActitvityBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jp.wasabeef.glide.transformations.BlurTransformation;
import render.animations.Render;
import render.animations.Slide;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectProductActitvity extends AppCompatActivity{

    private ActivitySelectProductActitvityBinding binding;
    private List<MenuModel> menuModelsList;
    private  MenuAdapter menuAdapter;
    private Intent intent;
    private TableModel tableModel;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MenuCategoryAdapter menuCategoryAdapter;
    private List<MenuCategoryModel> menuCategoryModelList;
    private Render render = new Render(this);
    private Boolean alreadyUp = false;
    private String typeId = "00";
    private FirebaseDatabase dbRealtime = FirebaseDatabase.getInstance();
    private String sActivity = "";
    private SearchView searchView;
    private List<DishModel> dishes = new ArrayList<>();
    private boolean ignore = false;
    private MyApplication myApp;
    private ActivityResultLauncher<Intent> launcher;
    private OrderRequest orderRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectProductActitvityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);



        getData();
        setRv();
        passData();


        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                if(o.getResultCode() == RESULT_OK){
                    Intent data = o.getData();
                    TableModel tableSelected = (TableModel) data.getSerializableExtra("table_selected");
                    OrderRequest orderRequest = getOrderRequest();
                    orderRequest.setTable_id(tableSelected.getTable_id());
                    newOrder(orderRequest);
                }
            }
        });


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
                OrderDishes orderDishes = getOrderItems();
                OrderRequest orderRequest = new OrderRequest(orderDishes.getOrderItemModels(),
                        Auth.User_Uid,
                        tableModel.getTable_id(),
                        orderDishes.getTotal_amount());
                switch (sActivity){
                    case "Home":
                        tableIsOccupiedChecking(orderRequest);
                        break;
                    case "OrderDetail":
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("response","SelectProductActivity");
                        resultIntent.putExtra("orderItems", (Serializable) orderDishes.orderItemModels);
                        resultIntent.putExtra("total_amount",orderDishes.total_amount);
                        setResult(RESULT_OK,resultIntent);
                        finish();
                        break;



                }
            }
        });
        binding.reSelectCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAndPopDown();
                menuAdapter.getFilter().filter(typeId);
            }
        });
    }

    private void tableIsOccupiedChecking(OrderRequest orderRequest) {
        OrderRequest request = new OrderRequest();
        request.setTable_id(tableModel.getTable_id());
        ApiService.apiService.orderCheckTableOccupied(request).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    if(response.body().isOccupied()){
                        //Chọn bàn khác
                        setOrderRequest(orderRequest);//Saving order request
                        Intent intent = new Intent(SelectProductActitvity.this,SelectTableActivity.class);
                        launcher.launch(intent);

                        return;
                    }
                    newOrder(orderRequest);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    private void updateOrder(OrderRequest orderRequest) {
        ApiService.apiService.updateOrderItems(orderRequest).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {

            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });


    }

    private void newOrder(OrderRequest orderRequest) {
        ApiService.apiService.addNewOrder(orderRequest)
                .enqueue(new Callback<TableModel>() {
                    @Override
                    public void onResponse(Call<TableModel> call, Response<TableModel> response) {
                        if(response.isSuccessful()){
                            TableModel model = response.body();
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("action","StartOrderDetail");
                            resultIntent.putExtra("table", model);
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<TableModel> call, Throwable t) {
                        Toast.makeText(SelectProductActitvity.this, "Lỗi sever", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private OrderDishes getOrderItems() {
        inProgressItem(true);
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator(',');


        long vtQuanity = 0;
        double totalPrice = 0;

        final Map<String,Object> orderMap = new HashMap<>();
        List<OrderItemModel> orderItemModels = new ArrayList<>();

        for(DishModel vt : dishes){
            OrderItemModel model = new OrderItemModel();
            vtQuanity = Long.parseLong(vt.getQuantity());
            double price= Double.parseDouble(vt.getTotalPriceProduct().trim().replace(",",""))* vtQuanity;
            model.setMenu_item_id(vt.getDocumentId());
            model.setQuantity(Integer.parseInt(vt.getQuantity()));
            model.setItem_price(Double.parseDouble(vt.getPrice().replace(",","")));
            model.setNote(vt.getNote());
            model.setOrder_id(tableModel.getOrder_id());
            model.setOrder_time(vt.getOder_time());



            if(!vt.getValueDiscount().isEmpty()){
                if(vt.isDiscountUnit()){
                    model.setDiscount_percentage(Integer.parseInt(vt.getValueDiscount()));
                    price = price -  (price * model.getDiscount_percentage()/100);
                }
                else{
                    model.setDiscount_amount(Double.parseDouble(vt.getValueDiscount().replace(",","")));
                    price = price - (model.getDiscount_amount() * vtQuanity);
                }
            }
            totalPrice += price;

            orderItemModels.add(model);
        }
        return new OrderDishes(orderItemModels,totalPrice);
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
                                if(document.contains("isDelete")){
                                    model.setDelete(document.getBoolean("isDelete"));
                                }
                                if(!model.getDelete()){
                                    alreadyAddToTable(model);
                                    menuModelsList.add(model);
                                }

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
                typeId = model.getId();
                menuAdapter.getFilter().filter(typeId);
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
                                            binding.numberDifferentProductTv.setText(String.valueOf(dishes.size()));


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
            public void onClickItemMenuListener(MenuModel models, int position) {
                viewSelectAddToTable(models.isSelected());
                if(models.isSelected() && !models.isAdded()){
                   addToTable(models);
                   menuAdapter.notifyItemChanged(position);
                   return;
               }
                DishModel dishModel = null;
                for(DishModel vt : dishes){
                    if(vt.getDocumentId().equals(models.getDocumentId())){
                        dishModel = vt;
                    }
                }
                NoteProductFragment fragment = new NoteProductFragment(models, dishModel, new IClickViewInNoteProductListeners() {
                    @Override
                    public void onClickListener(ProductInReservationModel pModel,String command) {
                        switch (command){
                            case "remove":
                                removeTable(models);
                                if (dishes.size() == 0 && alreadyUp){
                                    clearAndPopDown();
                                }
                                menuAdapter.getFilter().filter(typeId);
                                break;
                            case "saved":
                                menuAdapter.setVirtualTableList(dishes);
                                menuAdapter.notifyItemChanged(position);
                        }

                    }
                });
                fragment.show(getSupportFragmentManager(),fragment.getTag());



            }

            @Override
            public void onClickPlusListener(MenuModel models,int value) {
                for(DishModel vt : dishes){
                    if(vt.getDocumentId().equals(models.getDocumentId())){
                        vt.setQuantity(String.valueOf(value));
                    }
                }
            }

            @Override
            public void onClickMinusListener(MenuModel models, String signal,int value) {
                if(signal.equals("delete")){
                    removeTable(models);
                    binding.numberDifferentProductTv.setText(String.valueOf(dishes.size()));
                }
                else{
                    for(DishModel vt : dishes){
                        if(vt.getDocumentId().equals(models.getDocumentId())){
                            vt.setQuantity(String.valueOf(value));
                        }
                    }
                }
                if (dishes.size() == 0 && alreadyUp){
                    clearAndPopDown();
                }

            }

        });


        binding.itemInMenuRv.setAdapter(menuAdapter);
    }

    private void clearAndPopDown() {
        if(dishes.size()!= 0){
            for(DishModel dish : dishes){
                for(MenuModel model : menuModelsList){
                    if(model.getDocumentId().equals(dish.getDocumentId())){
                        model.setSelected(false);
                        model.setAdded(false);
                    }
                }
            }
            dishes.clear();
        }
        binding.numberDifferentProductTv.setText(String.valueOf(dishes.size()));
        alreadyUp = false;
        render.setAnimation(Slide.OutDown(binding.reSelectCv));
        render.start();
        render.setAnimation(Slide.OutDown(binding.addToTableCv));
        render.start();
    }

    private void removeTable(MenuModel models) {
        models.setSelected(false);
        models.setAdded(false);
        dishes.removeIf(vT -> vT.getDocumentId().equals(models.getDocumentId()));
    }

    private void addToTable(MenuModel models) {
            models.setAdded(true);
            DishModel model = new DishModel(models.getDocumentId(),"1",models.getPrice());
            model.setOder_time(new Date());
            model.setPrice(models.getPrice());
            dishes.add(model);
    }

    private void alreadyAddToTable(MenuModel model) {
        for(DishModel vt : dishes){
            if(vt.getDocumentId().equals(model.getDocumentId())){
                model.setSelected(true);
                model.setAdded(true);
            }
        }
    }

    private void viewSelectAddToTable(boolean b){
        if(b && !alreadyUp){
            menuAdapter.setVirtualTableList(dishes);
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
        binding.nameTableTv.setText(tableModel.getTable_name());
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
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                menuAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                menuAdapter.getFilter().filter(newText);
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
    private class OrderDishes{
        private List<OrderItemModel> orderItemModels;
        private double total_amount;

        public OrderDishes(List<OrderItemModel> orderItemModels, double total_amount) {
            this.orderItemModels = orderItemModels;
            this.total_amount = total_amount;
        }

        public List<OrderItemModel> getOrderItemModels() {
            return orderItemModels;
        }

        public void setOrderItemModels(List<OrderItemModel> orderItemModels) {
            this.orderItemModels = orderItemModels;
        }

        public double getTotal_amount() {
            return total_amount;
        }

        public void setTotal_amount(double total_amount) {
            this.total_amount = total_amount;
        }
    }

    public OrderRequest getOrderRequest() {
        return orderRequest;
    }

    public void setOrderRequest(OrderRequest orderRequest) {
        this.orderRequest = orderRequest;
    }
}