package com.example.nhahang;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.nhahang.Adapters.OrderItemAdapter;
import com.example.nhahang.BottomSheetDialogFragments.WarningDeleteItemFragment;
import com.example.nhahang.Interfaces.ApiService;
import com.example.nhahang.Interfaces.IClickViewInWaringDelete;
import com.example.nhahang.Interfaces.IOrderItemDetail;
import com.example.nhahang.Models.MenuModel;
import com.example.nhahang.Models.MessageEvent;
import com.example.nhahang.Models.NotificationModel;
import com.example.nhahang.Models.OrderItemModel;
import com.example.nhahang.Models.OrderModel;
import com.example.nhahang.Models.Requests.OrderRequest;
import com.example.nhahang.Models.Respones.ChangeOrderTableResponse;
import com.example.nhahang.Models.Respones.ServerResponse;
import com.example.nhahang.Models.TableModel;
import com.example.nhahang.Utils.Auth;
import com.example.nhahang.Utils.Util;
import com.example.nhahang.databinding.ActivityOrderDetailBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity implements ActivityResultCallback<ActivityResult>{

    private ActivityOrderDetailBinding binding;
    private TableModel tableModel;
    private OrderItemAdapter orderItemAdapter;
    private List<OrderItemModel> orderItemModels;
    private MyApplication myApp;
    private AlertDialog dialog;
    private boolean isNotifyKitchen;
    private ActivityResultLauncher<Intent> launcher;
    private double orderTotalAmount;
    private int orderTotalQuantity;
    private OrderModel orderModel;
    private boolean notifyChange;
    private List<TableModel> changeTables;
    private boolean isChangeTable;
    private boolean isKitchen;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setToobarNavIcon();

        changeTables = new ArrayList<>();

        isKitchen = getIntent().getBooleanExtra("is_kitchen",false);
        if(isKitchen){
            setViewForKitChen();
        }else{
            setViewForStaff();
        }

        myApp = (MyApplication) getApplication();

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),this);


        binding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(!changeTables.isEmpty()){
//                    EventBus.getDefault().postSticky(new MessageEvent(changeTables,this.getClass().getSimpleName()));
//                }
                Intent resultIntent = new Intent();
                resultIntent.putExtra("table", tableModel);
                setResult(RESULT_OK, resultIntent);
                if(binding.thongBao.isEnabled()){
                    buildDialog();
                    dialog.show();
                }else{
                    if(isChangeTable) finish();
                    finishAfterTransition();
                }

            }
        });

        binding.floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetailActivity.this,SelectProductActitvity.class);
                intent.putExtra("table",tableModel);
                intent.putExtra("orderItems", (Serializable) orderItemModels);
                intent.putExtra("activity","OrderDetail");
                launcher.launch(intent);
            }
        });

        binding.thongBao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(myApp, "Thong bao cho bep", Toast.LENGTH_SHORT).show();
                isNotifyKitchen = true;
                updateOrderItems();
                setEnableThongBao(false);
            }
        });

        tableModel = (TableModel) getIntent().getSerializableExtra("table");


        binding.tableId.setText(tableModel.getTable_name());
        Util.updateMoneyLabel(binding.totalPrice,tableModel.getTotal_amount());

        binding.productsRv.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        orderItemModels = new ArrayList<>();
        orderItemAdapter = new OrderItemAdapter(this,orderItemModels,myApp.getMenuModels(),getSupportFragmentManager());
        binding.productsRv.setAdapter(orderItemAdapter);

        orderItemAdapter.setOnChangeOrderItemListener(new IOrderItemDetail() {
            @Override
            public void onOrderItemChangeListener() {
                calculationTotalOrder();
                setEnableThongBao(true);
            }
        });

        binding.xemTamTinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetailActivity.this,TemporaryPaymentActivity.class);
                intent.putExtra("table",tableModel);
                intent.putExtra("orderItems", (Serializable) orderItemModels);
                startActivity(intent);

            }
        });
        binding.thanhToan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetailActivity.this,PaymentActivity.class);
                intent.putExtra("table",tableModel);
                intent.putExtra("orderItems", (Serializable) orderItemModels);
                intent.putExtra("order",orderModel);
                intent.putExtra("orderTotalAmount",getOrderTotalAmount());
                intent.putExtra("orderTotalQuantity",getOrderTotalQuantity());
                launcher.launch(intent);
            }
        });


        

        OrderRequest request = new OrderRequest();
        request.setOrder_id(tableModel.getOrder_id());
        InProgress(true);
        ApiService.apiService.getOrderItems(request).enqueue(new Callback<ArrayList<OrderItemModel>>() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onResponse(Call<ArrayList<OrderItemModel>> call, Response<ArrayList<OrderItemModel>> response) {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    orderItemModels.addAll(response.body());
                    orderItemAdapter.notifyDataSetChanged();
                    binding.quantity.setText(orderItemAdapter.getQuantity().toString());
                    calculationTotalOrder();
                    InProgress(false);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<OrderItemModel>> call, Throwable t) {

            }
        });

        InProgress(true);
        ApiService.apiService.getOrderById(request).enqueue(new Callback<OrderModel>() {
            @Override
            public void onResponse(Call<OrderModel> call, Response<OrderModel> response) {
                if(response.isSuccessful()){
                    setOrderModel(response.body());
                    InProgress(false);
                }
            }

            @Override
            public void onFailure(Call<OrderModel> call, Throwable t) {

            }
        });

    }

    private void setViewForStaff() {
        binding.staffActionView.setVisibility(View.VISIBLE);
        binding.kitchenActionView.setVisibility(View.GONE);
    }

    private void setViewForKitChen() {
        binding.staffActionView.setVisibility(View.GONE);
        binding.kitchenActionView.setVisibility(View.VISIBLE);
    }

    private void backToHome(String response) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("table", tableModel);
        resultIntent.putExtra("action",response);
        setResult(RESULT_OK,resultIntent);
        finish();
    }
    private void backToHome(List<TableModel> changeTables, String response) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("changeTables", (Serializable) changeTables);
        resultIntent.putExtra("action",response);
        setResult(RESULT_OK,resultIntent);
        finish();
    }

    private void updateOrders() {
        OrderRequest request = new OrderRequest(orderModel);
        ApiService.apiService.updateOrders(request).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if(response.isSuccessful()){
                    ServerResponse response1 = response.body();
                    Toast.makeText(myApp, response1.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    private void setEnableThongBao(boolean isEnable) {
        binding.thongBao.setEnabled(isEnable);
        if(isEnable){
            binding.thongBao.setBackgroundResource(R.drawable.radius_sky_blue_background);
            return;
        }
        binding.thongBao.setBackgroundResource(R.drawable.radius_light_blue_background);
    }

    private void calculationTotalOrder() {
        int totalQuantity = 0;
        double totalPrice = 0;
        for(OrderItemModel item : orderItemModels){
            double price = 0;
            if(item.getDiscount_amount() > 0){
                price = (item.getItem_price() - item.getDiscount_amount())*item.getQuantity();
            }
            if(item.getDiscount_percentage() > 0){
                price = item.getItem_price()*(100 - item.getDiscount_percentage())/100 * item.getQuantity();
            }
            if(price == 0){
                price = item.getItem_price() * item.getQuantity();
            }

            totalPrice+=price;
            totalQuantity += item.getQuantity();
        }
        setOrderTotalAmount(totalPrice);
        setOrderTotalQuantity(totalQuantity);

        Util.updateMoneyLabel(binding.totalPrice,totalPrice);
        binding.quantity.setText(String.valueOf(totalQuantity));
    }
    private void updateOrderItems(){
        if(myApp.getWebSocketClient() != null && myApp.getWebSocketClient().isClosed()){
            myApp.getWebSocketClient().reconnect();
        }
        String totalAmountText = binding.totalPrice.getText().toString().trim();
        double totalAmount = Double.parseDouble(totalAmountText.replaceAll("[,.₫]",""));
        OrderRequest request = new OrderRequest(orderItemModels,totalAmount);
        request.setUser_uid(Auth.User_Uid);
        tableModel.setTotal_amount(totalAmount);
        ApiService.apiService.updateOrderItems(request).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if(response.isSuccessful()){
                    ServerResponse res = response.body();
                    assert res != null;
                    myApp.getWebSocketClient().send("Update order table");
                    notifyForKitchen();
                    InProgress(false);
                }

            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });

    }

    private void notifyForKitchen() {
        if(isNotifyKitchen){
            isNotifyKitchen = false;
            JSONObject object = new JSONObject();
            try {
                object.put("from",Auth.User_Uid);
                object.put("to","kitchen");
                JSONObject message = new JSONObject();
                message.put("order_id",tableModel.getOrder_id());
                message.put("status","đã thông báo bếp cập nhật đơn của " + tableModel.getTable_name());
                message.put("new_table_id",tableModel.getTable_id());
                object.put("message",message);
                myApp.getWebSocketClient().send(object.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(notifyChange){
            notifyChange = false;
            JSONObject object = new JSONObject();
            try {
                object.put("from",Auth.User_Uid);
                JSONObject message = new JSONObject();
                message.put("order_id",tableModel.getOrder_id());
                message.put("status","đã cập nhật đơn của " + tableModel.getTable_name());
                message.put("new_table_id",tableModel.getTable_id());
                object.put("message",message);
                myApp.getWebSocketClient().send(object.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void InProgress(boolean isIn){
        if(isIn){
            binding.ProgressBar.setVisibility(View.VISIBLE);
            binding.contentRl.setVisibility(View.GONE);
            return;
        }
        binding.ProgressBar.setVisibility(View.GONE);
        binding.contentRl.setVisibility(View.VISIBLE);
    }

    private void setToobarNavIcon() {
        Drawable navIcon = binding.toolBar.getNavigationIcon();
        // Tint the navigation icon with the desired color.
        if (navIcon != null) {
            navIcon = DrawableCompat.wrap(navIcon).mutate(); // Ensure the original drawable is not modified.
            DrawableCompat.setTint(navIcon, ContextCompat.getColor(this, R.color.white)); // Replace R.color.your_color with your color resource.
            binding.toolBar.setNavigationIcon(navIcon);
        }
    }
    private void buildDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bạn có muốn thông báo cho bếp không?")
                .setTitle("Thoát");
        builder.setPositiveButton("Thông báo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isNotifyKitchen = true;
                updateOrderItems();
                supportFinishAfterTransition();
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                notifyChange = true;
                updateOrderItems();
                supportFinishAfterTransition();
            }
        });
        dialog = builder.create();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_more_order_detail,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent intent;

        switch (id) {
            case R.id.infoReseration:
                intent = new Intent(this,InforReservationActivity.class);
                intent.putExtra("table",tableModel);
                intent.putExtra("order",getOrderModel());
                startActivity(intent);
                return true;
            case R.id.hisNotifyRes:
                intent = new Intent(this,HisNotifyResActivity.class);
                intent.putExtra("orderModel", orderModel);
                startActivity(intent);
                return true;
            case R.id.joinTable:
                intent = new Intent(this,JoinTableActivity.class);
                intent.putExtra("currentTable",tableModel);
                intent.putExtra("orderitems", (Serializable) orderItemModels);
                intent.putExtra("order",getOrderModel());
                launcher.launch(intent);
                return true;

            case R.id.changeTable:
                intent = new Intent(this,ChangeTableActivity.class);
                intent.putExtra("currentTable",tableModel);
                intent.putExtra("order",getOrderModel());
                launcher.launch(intent);
                return true;

            case R.id.splitTable:
                intent = new Intent(this,SplitOrderTableActivity.class);
                intent.putExtra("currentTable",tableModel);
                intent.putExtra("order",getOrderModel());
                intent.putExtra("orderitems", (Serializable) orderItemModels);
                launcher.launch(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    public double getOrderTotalAmount() {
        return orderTotalAmount;
    }

    public void setOrderTotalAmount(double orderTotalAmount) {
        this.orderTotalAmount = orderTotalAmount;
    }

    public int getOrderTotalQuantity() {
        return orderTotalQuantity;
    }

    public void setOrderTotalQuantity(int orderTotalQuantity) {
        this.orderTotalQuantity = orderTotalQuantity;
    }

    public OrderModel getOrderModel() {
        return orderModel;
    }

    public void setOrderModel(OrderModel orderModel) {
        this.orderModel = orderModel;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(ActivityResult o) {
        if(o.getResultCode() == RESULT_OK){
            Intent data = o.getData();
            if(data != null){
                String response = data.getStringExtra("response");
                InProgress(true);
                switch (response){
                    case "SelectProductActivity":
                        List<OrderItemModel> addOrderItems = (List<OrderItemModel>) data.getSerializableExtra("orderItems");
                        setEnableThongBao(true);
                        Collections.sort(addOrderItems);
                        orderItemModels.addAll(0,addOrderItems);
                        orderItemAdapter.notifyItemRangeChanged(0,addOrderItems.size());
                        calculationTotalOrder();
                        updateOrderItems();
                        break;

                    case "PaymentActivity":
                        OrderModel model = (OrderModel) data.getSerializableExtra("orderModel");
                        orderModel.update(model);
                        updateOrders();
                        InProgress(false);
                        break;
                    case "ChangeOrderTable":
                        List<TableModel> changeTables = (List<TableModel>) data.getSerializableExtra("ChangeTables");
                        backToHome(changeTables,"ChangeOrderTable");
                        break;

                    case "JoinOrderTable":
                        List<TableModel> joinOrderTable = (List<TableModel>) data.getSerializableExtra("JoinOrderTable");
                        backToHome(joinOrderTable,"ChangeOrderTable");
                    case "PaidOrder":
                        backToHome(response);
                        break;


                }
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(List<TableModel> changeTables) {
       for(TableModel model : changeTables){
           if(model.getOrder_id() == 0 && model.getTable_id() == tableModel.getTable_id()){
               tableModel = model;
               finishAfterTransition();
           }
           if(model.getOrder_id() == tableModel.getOrder_id()){
               reloadOrderItems();
               if(tableModel.getTable_id() != model.getTable_id()){
                   isChangeTable = true;
                   binding.tableId.setText(model.getTable_name());
               }
               tableModel = model;
               break;
           }
       }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent event) {
        String activitySimpleName = event.getActivitySimpleName();
        if (activitySimpleName.equals(this.getClass().getSimpleName())){
            return;
        }
        onEvent(event.getChangeTables());
    }


    private void reloadOrderItems(){
        OrderRequest request = new OrderRequest();
        request.setOrder_id(tableModel.getOrder_id());
        InProgress(true);
        ApiService.apiService.getOrderItems(request).enqueue(new Callback<ArrayList<OrderItemModel>>() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onResponse(Call<ArrayList<OrderItemModel>> call, Response<ArrayList<OrderItemModel>> response) {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    orderItemModels.clear();
                    orderItemModels.addAll(response.body());
                    orderItemAdapter.notifyDataSetChanged();
                    binding.quantity.setText(orderItemAdapter.getQuantity().toString());
                    calculationTotalOrder();
                    InProgress(false);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<OrderItemModel>> call, Throwable t) {

            }
        });
    }

}