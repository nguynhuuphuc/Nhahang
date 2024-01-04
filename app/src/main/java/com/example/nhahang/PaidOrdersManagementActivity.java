package com.example.nhahang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.nhahang.Adapters.PaidOrdersManagementAdapter;
import com.example.nhahang.Interfaces.ApiService;
import com.example.nhahang.Models.Employee;
import com.example.nhahang.Models.OrderItemModel;
import com.example.nhahang.Models.PaidOrderModel;
import com.example.nhahang.Models.Requests.OrderRequest;
import com.example.nhahang.Utils.Util;
import com.example.nhahang.databinding.ActivityPaidOrdersManagementBinding;
import com.google.protobuf.Api;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaidOrdersManagementActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private ActivityPaidOrdersManagementBinding binding;
    private List<PaidOrderModel> paidOrderModels;
    private List<Employee> employees;
    private MyApplication mApp;
    private PaidOrdersManagementAdapter paidOrdersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaidOrdersManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        binding.swipeRefreshLayout.setEnabled(false);

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        mApp = (MyApplication) getApplication();
        employees = mApp.getEmployees();

        paidOrdersAdapter = new PaidOrdersManagementAdapter();
        binding.swipeRefreshLayout.setOnRefreshListener(this);

        getViewPaidOrders();
        paidOrdersAdapter.setOnItemClickListener(new PaidOrdersManagementAdapter.OnItemClickListener() {
            @Override
            public void onClicked(PaidOrderModel paidOrderModel) {
                getPaidOrderItem(paidOrderModel);
            }
        });



    }

    private void getPaidOrderItem(PaidOrderModel paidOrderModel) {
        ApiService.apiService.getPaidOrderItems(new OrderRequest(paidOrderModel.getOrder_id(),false)).enqueue(new Callback<ArrayList<OrderItemModel>>() {
            @Override
            public void onResponse(Call<ArrayList<OrderItemModel>> call, Response<ArrayList<OrderItemModel>> response) {
                if(response.isSuccessful()){
                    Intent intent = new Intent(PaidOrdersManagementActivity.this,TemporaryPaymentActivity.class);
                    intent.putExtra("activity",PaidOrdersManagementActivity.class.getSimpleName());
                    intent.putExtra("paidOrder",paidOrderModel);
                    intent.putExtra("orderItems",response.body());
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<OrderItemModel>> call, Throwable t) {

            }
        });
    }

    private void getViewPaidOrders() {
        ApiService.apiService.getAllPaidOrders().enqueue(new Callback<ArrayList<PaidOrderModel>>() {
            @Override
            public void onResponse(Call<ArrayList<PaidOrderModel>> call, Response<ArrayList<PaidOrderModel>> response) {
                if(response.isSuccessful()){
                    paidOrderModels = new ArrayList<>();
                    assert response.body() != null;
                    headerMaker(response.body());
                    paidOrdersAdapter.setData(paidOrderModels,employees);
                    binding.listPaidOrders.setAdapter(paidOrdersAdapter);
                    if(binding.swipeRefreshLayout.isRefreshing()){
                        binding.swipeRefreshLayout.setRefreshing(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<PaidOrderModel>> call, Throwable t) {

            }
        });
    }

    private void headerMaker(ArrayList<PaidOrderModel> paidOrderRawModels) {
        for(PaidOrderModel itemRaw : paidOrderRawModels){
            itemRaw.setDateHeader(Util.convertToTodayYesterday(itemRaw.getPaid_time()));
            paidOrderModels.add(itemRaw);
        }
    }

    @Override
    public void onRefresh() {
        getViewPaidOrders();
    }
}