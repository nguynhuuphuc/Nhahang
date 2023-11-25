package com.example.nhahang;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.nhahang.Adapters.HisNotifyResAdapter;
import com.example.nhahang.Interfaces.ApiService;
import com.example.nhahang.Models.OrderItemsHisModel;
import com.example.nhahang.Models.OrderModel;
import com.example.nhahang.Models.Requests.OrderRequest;
import com.example.nhahang.Utils.Util;
import com.example.nhahang.databinding.ActivityHisNotifyResBinding;
import com.google.protobuf.Api;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HisNotifyResActivity extends AppCompatActivity {
    private ActivityHisNotifyResBinding binding;
    private OrderModel orderModel;
    private List<OrderItemsHisModel> orderItemsHisModels;
    private HisNotifyResAdapter hisNotifyResAdapter;
    private MyApplication myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHisNotifyResBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        myApp = (MyApplication) getApplication();
        orderModel = (OrderModel) getIntent().getSerializableExtra("orderModel");
        hisNotifyResAdapter = new HisNotifyResAdapter();


        OrderRequest request = new OrderRequest();
        request.setOrder_id(orderModel.getOrder_id());
        ApiService.apiService.getOrderItemsHisByOrderId(request).enqueue(new Callback<ArrayList<OrderItemsHisModel>>() {
            @Override
            public void onResponse(Call<ArrayList<OrderItemsHisModel>> call, Response<ArrayList<OrderItemsHisModel>> response) {
                if(response.isSuccessful()){
                    orderItemsHisModels = new ArrayList<>();
                    formatData(response.body());
                    hisNotifyResAdapter.setData(orderItemsHisModels,myApp);
                    binding.listOrderItemsHis.setAdapter(hisNotifyResAdapter);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<OrderItemsHisModel>> call, Throwable t) {

            }
        });

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
                finish();
            }
        });
    }

    private void formatData(ArrayList<OrderItemsHisModel> orderItemsHisRawModels) {
        for (OrderItemsHisModel itemRaw : orderItemsHisRawModels){
            itemRaw.setDateHeader(Util.convertToTodayYesterday(itemRaw.getTimestamp()));
            if(orderItemsHisModels.isEmpty()){
                OrderItemsHisModel firstModel = new OrderItemsHisModel(itemRaw.getTimestamp(),itemRaw.getOrder_id(),itemRaw.getChanged_by());
                OrderItemsHisModel.Item firstItem = new OrderItemsHisModel.Item(itemRaw.getQuantity(),itemRaw.getMenu_item_id(),itemRaw.getAction_type());
                firstModel.setItems(new ArrayList<>());
                firstModel.getItems().add(firstItem);
                firstModel.setDateHeader(itemRaw.getDateHeader());
                orderItemsHisModels.add(firstModel);
                continue;
            }
            boolean isAdded = false;
            for(int i = 0; i < orderItemsHisModels.size(); i++){
                String orderItemTime = Util.TimeFormatting(orderItemsHisModels.get(i).getTimestamp());
                String orderItemTimeRaw = Util.TimeFormatting(itemRaw.getTimestamp());
                if(orderItemTime.equals(orderItemTimeRaw)){
                    OrderItemsHisModel.Item item = new OrderItemsHisModel.Item(itemRaw.getQuantity(),itemRaw.getMenu_item_id(),itemRaw.getAction_type());
                    orderItemsHisModels.get(i).getItems().add(item);
                    isAdded = true;
                    break;
                }
            }
            if(isAdded) continue;
            OrderItemsHisModel model = new OrderItemsHisModel(itemRaw.getTimestamp(),itemRaw.getOrder_id(),itemRaw.getChanged_by());
            OrderItemsHisModel.Item item = new OrderItemsHisModel.Item(itemRaw.getQuantity(),itemRaw.getMenu_item_id(),itemRaw.getAction_type());
            model.setItems(new ArrayList<>());
            model.getItems().add(item);
            model.setDateHeader(itemRaw.getDateHeader());
            orderItemsHisModels.add(model);
        }
    }
}