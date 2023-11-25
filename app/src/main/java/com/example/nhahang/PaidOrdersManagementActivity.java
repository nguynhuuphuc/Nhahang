package com.example.nhahang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;

import com.example.nhahang.Adapters.PaidOrdersManagementAdapter;
import com.example.nhahang.Interfaces.ApiService;
import com.example.nhahang.Models.Employee;
import com.example.nhahang.Models.PaidOrderModel;
import com.example.nhahang.Utils.Util;
import com.example.nhahang.databinding.ActivityPaidOrdersManagementBinding;

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