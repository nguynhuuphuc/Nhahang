package com.example.nhahang;

import androidx.activity.OnBackPressedCallback;
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
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import com.example.nhahang.Adapters.OrderItemAdapter;
import com.example.nhahang.Adapters.OrderItemAdapter2;
import com.example.nhahang.BottomSheetDialogFragments.WarningDeleteItemFragment;
import com.example.nhahang.Interfaces.ApiService;
import com.example.nhahang.Interfaces.IClickViewInWaringDelete;
import com.example.nhahang.Interfaces.IOrderItemDetail;
import com.example.nhahang.Models.MenuModel;
import com.example.nhahang.Models.MessageEvent;
import com.example.nhahang.Models.NotificationModel;
import com.example.nhahang.Models.OrderItemModel;
import com.example.nhahang.Models.OrderItemModel2;
import com.example.nhahang.Models.OrderModel;
import com.example.nhahang.Models.Requests.OrderRequest;
import com.example.nhahang.Models.Requests.UserUidRequest;
import com.example.nhahang.Models.Respones.ChangeOrderTableResponse;
import com.example.nhahang.Models.Respones.ServerResponse;
import com.example.nhahang.Models.TableModel;
import com.example.nhahang.Utils.Auth;
import com.example.nhahang.Utils.Util;
import com.example.nhahang.databinding.ActivityOrderDetailBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.java_websocket.client.WebSocketClient;
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
    private OrderItemAdapter2 orderItemAdapter2;
    private List<OrderItemModel2> orderItemModel2s;
    private WebSocketClient webSocketClient;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setToobarNavIcon();
        tableModel = (TableModel) getIntent().getSerializableExtra("table");
        isNotifyKitchen = getIntent().getBooleanExtra("isNotifyKitchen",false);
        setEnableThongBao(isNotifyKitchen);


        changeTables = new ArrayList<>();

        isKitchen = getIntent().getBooleanExtra("is_kitchen",false);
        if(isKitchen){
            setViewForKitChen();
        }else{
            setViewForStaff();
        }

        myApp = (MyApplication) getApplication();
        webSocketClient = myApp.getWebSocketClient();

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),this);
        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
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
        };
        getOnBackPressedDispatcher().addCallback(OrderDetailActivity.this, callback);


        binding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });


        binding.servDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InProgress(true);
                OrderRequest orderRequest = new OrderRequest();
                orderRequest.setOrderItemModels(orderItemModels);
                ApiService.apiService.servOrderItems(orderRequest).enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                        if(response.isSuccessful()){
                            viewServDish(response.body().isIs_servDish());
                            getOrderItems();
                            notifyKitchenServ();
                            InProgress(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<ServerResponse> call, Throwable t) {
                        Toast.makeText(myApp, "Lỗi server", Toast.LENGTH_SHORT).show();
                        InProgress(false);

                    }
                });
            }
        });

        binding.confirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InProgress(true);
                OrderRequest orderRequest = new OrderRequest();
                orderRequest.setOrderItemModels(orderItemModels);
                ApiService.apiService.confirmOrderItems(orderRequest).enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                        if(response.isSuccessful()){
                            viewConfirm(response.body().isIs_confirm());
                            getOrderItems();
                            notifyKitchenConfirm();
                            InProgress(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<ServerResponse> call, Throwable t) {
                        Toast.makeText(myApp, "Lỗi server", Toast.LENGTH_SHORT).show();
                        InProgress(false);

                    }
                });
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
                Toast.makeText(myApp, "Thông báo cho bếp", Toast.LENGTH_SHORT).show();
                isNotifyKitchen = true;
                updateOrderItems();
                setEnableThongBao(false);
            }
        });




        binding.tableId.setText(tableModel.getTable_name());
        Util.updateMoneyLabel(binding.totalPrice,tableModel.getTotal_amount());
        orderItemModels = new ArrayList<>();

        orderItemAdapter = new OrderItemAdapter(this,orderItemModels,myApp.getMenuModels(),getSupportFragmentManager());

        binding.productsRv.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        if(isKitchen){
            orderItemModel2s = new ArrayList<>();
            orderItemAdapter2 = new OrderItemAdapter2(this,orderItemModel2s,myApp.getMenuModels());
            binding.productsRv.setAdapter(orderItemAdapter2);
        }else {
            binding.productsRv.setAdapter(orderItemAdapter);
        }

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


        getOrderItems();


    }
    private void notifyKitchenServ() {
        JSONObject object = new JSONObject();
        try {
            object.put("from", Auth.User_Uid);
            object.put("position","B");
            JSONObject message = new JSONObject();
            message.put("order_id",orderModel.getOrder_id());
            message.put("status","trả món cho " + tableModel.getTable_name());
            object.put("message",message);
            webSocketClient.send(object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    private void notifyKitchenConfirm() {

        JSONObject object = new JSONObject();
        try {
            object.put("from", Auth.User_Uid);
            object.put("position","B");
            JSONObject message = new JSONObject();
            message.put("order_id",orderModel.getOrder_id());
            message.put("status","đã xác nhận món cho " + tableModel.getTable_name());
            object.put("message",message);
            webSocketClient.send(object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void getOrderItems() {
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
                    if(isKitchen){
                        orderItemModel2s.clear();
                        orderItemModel2s = getOrderItemModel2();
                        orderItemAdapter2.setData(orderItemModel2s);
                    }else{
                        orderItemAdapter.notifyDataSetChanged();
                    }
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

    private List<OrderItemModel2> getOrderItemModel2() {
        List<OrderItemModel2> list = new ArrayList<>();
        List<OrderItemModel> unConfirms = new ArrayList<>();
        List<OrderItemModel> confirms = new ArrayList<>();
        List<OrderItemModel> servList = new ArrayList<>();
        for(final OrderItemModel item : orderItemModels){
            try {
                if( item.getQuantity_serv() != 0){
                    OrderItemModel serv = item.clone();
                    serv.setQuantity(item.getQuantity_serv());
                    servList.add(serv);
                }
                if(item.getQuantity_confirm() != 0  && item.getQuantity_serv() < item.getQuantity_confirm()){
                    OrderItemModel confirm = item.clone();
                    confirm.setQuantity(item.getQuantity_confirm() - item.getQuantity_serv());
                    confirms.add(confirm);
                }
                if(item.getQuantity() > item.getQuantity_confirm()){
                    OrderItemModel unConfirm = item.clone();
                    unConfirm.setQuantity(item.getQuantity() - item.getQuantity_confirm());
                    unConfirms.add(unConfirm);
                }
            }catch (CloneNotSupportedException e ){
                e.printStackTrace();
            }

        }
        if(!unConfirms.isEmpty()){
            list.add(new OrderItemModel2("Chưa xác nhận",unConfirms));
        }
        if(!confirms.isEmpty()){
            list.add(new OrderItemModel2("Đã xác nhận", confirms));
        }
        if(!servList.isEmpty()){
            list.add(new OrderItemModel2("Đã trả món cho quầy",servList));
        }
        return list;
    }

    private void setViewForStaff() {
        binding.staffActionView.setVisibility(View.VISIBLE);
        binding.kitchenActionView.setVisibility(View.GONE);
    }


    void viewConfirm(boolean isConfirm){
        if(isConfirm){
            binding.confirmOrder.setEnabled(false);
            binding.confirmOrder.setText("Đã xác nhận");
            binding.confirmOrder.setBackgroundResource(R.drawable.radius_light_blue_background);
        }else{
            binding.confirmOrder.setEnabled(true);
            binding.confirmOrder.setText("Xác nhận");
            binding.confirmOrder.setBackgroundResource(R.drawable.radius_sky_blue_background);
        }

    }

    void  viewServDish(boolean isServ){
        if(isServ){
            binding.servDish.setEnabled(false);
            binding.servDish.setText("Đã trả món");
            binding.servDish.setBackgroundResource(R.drawable.radius_light_blue_background);
        }else{
            binding.servDish.setEnabled(true);
            binding.servDish.setText("Trả món");
            binding.servDish.setBackgroundResource(R.drawable.radius_sky_blue_background);
        }
    }

    private void setViewForKitChen() {
        InProgress(true);
        binding.floatingButton.setVisibility(View.GONE);
        binding.staffActionView.setVisibility(View.GONE);
        binding.kitchenActionView.setVisibility(View.VISIBLE);
        OrderRequest request = new OrderRequest();
        request.setOrder_id(tableModel.getOrder_id());
        ApiService.apiService.isConfirmOrder(request).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if(response.isSuccessful()){
                    viewConfirm(response.body().isIs_confirm());
                    InProgress(false);
                }
                
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(myApp, "Server Error", Toast.LENGTH_SHORT).show();
                InProgress(false);
            }
        });
        InProgress(true);
        ApiService.apiService.isServOrder(request).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if(response.isSuccessful()){

                    viewServDish(response.body().isIs_servDish());
                    InProgress(false);
                }

            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(myApp, "Server Error", Toast.LENGTH_SHORT).show();
                InProgress(false);
            }
        });
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
                    setOrderItemsNotify();
                    InProgress(false);
                }

            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });

    }

    private void setOrderItemsNotify() {
        for(OrderItemModel itemModel : orderItemModels){
            itemModel.setQuantity_notify(itemModel.getQuantity());
        }
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
                object.put("condition","UN_NOTIFY_KITCHEN");
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
        if(isKitchen) return false;
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

    public void onChange(List<TableModel> changeTables) {
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
        onChange(event.getChangeTables());
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