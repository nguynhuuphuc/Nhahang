package com.example.nhahang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.nhahang.Adapters.CategoryTableAdapter;
import com.example.nhahang.Adapters.TableAdapter;
import com.example.nhahang.Interfaces.ApiService;
import com.example.nhahang.Interfaces.IClickItemCategoryTableListener;
import com.example.nhahang.Interfaces.IClickItemTableListener;
import com.example.nhahang.Models.LocationModel;
import com.example.nhahang.Models.Requests.OrderRequest;
import com.example.nhahang.Models.Requests.UserUidRequest;
import com.example.nhahang.Models.Respones.ServerResponse;
import com.example.nhahang.Models.TableModel;
import com.example.nhahang.Utils.Auth;
import com.example.nhahang.databinding.ActivityChooseTableBinding;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SelectTableActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private ActivityChooseTableBinding binding;
    private TableAdapter tableAdapter;
    private List<TableModel> tableModelList;
    private CategoryTableAdapter categoryTableAdapter;
    private AlertDialog dialog;
    private List<LocationModel> locationList = new ArrayList<>();
    private int location = 1;
    private JsonObject jsonObject;
    private boolean isGetCategorySuccess;
    private boolean isGetTablesSuccess;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChooseTableBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        jsonObject = new JsonObject();
        jsonObject.addProperty("is_occupied",false); // Lấy tất cả các bàn không bận rộn
        jsonObject.addProperty("locationId",location);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.toolbarTitleTv.setText("Chọn bàn");

        InProgress(false);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.swipeRefreshLayout.setOnRefreshListener(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(SelectTableActivity.this,2);
        binding.tableRv.setLayoutManager(gridLayoutManager);

        tableModelList = new ArrayList<>();


        tableAdapter = new TableAdapter(SelectTableActivity.this,tableModelList, new IClickItemTableListener() {
            @Override
            public void onClickItemTableListener(TableModel tableModel, LinearLayout tablelabelLl, String oldPrice, int index) {
                buildDialog(tableModel);
                dialog.show();
            }
        });
        binding.tableRv.setAdapter(tableAdapter);
        binding.typeTableRv.setLayoutManager(new LinearLayoutManager(SelectTableActivity.this, LinearLayoutManager.HORIZONTAL, false));
        categoryTableAdapter = new CategoryTableAdapter(locationList, new IClickItemCategoryTableListener() {
            @Override
            public void onClickItemCategoryTableListener(LocationModel locationModel) {
                location = locationModel.getLocation_id();
                jsonObject.addProperty("is_occupied",false); // Lấy tất cả các bàn không bận rộn
                jsonObject.addProperty("locationId",location);
                tableAdapter.getFilter().filter(jsonObject.toString());
            }

        });
        binding.typeTableRv.setAdapter(categoryTableAdapter);
        viewTableCategories();
        getAllTables();

    }
    private void viewTableCategories() {
        if(!locationList.isEmpty()){
            locationList.clear();
        }
        ApiService.apiService.getAllLocations(new UserUidRequest(Auth.User_Uid))
                .enqueue(new Callback<ArrayList<LocationModel>>() {
                    @Override
                    public void onResponse(Call<ArrayList<LocationModel>> call, Response<ArrayList<LocationModel>> response) {
                        if(response.isSuccessful()){
                            assert response.body() != null;
                            locationList.addAll(response.body());
                            categoryTableAdapter.notifyDataSetChanged();
                            if(binding.swipeRefreshLayout.isRefreshing()){
                                setGetCategorySuccess(true);
                                stopRefreshing();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<LocationModel>> call, Throwable t) {

                    }
                });
    }
    private void getAllTables() {
        if(!tableModelList.isEmpty()){
            tableModelList.clear();
        }
        ApiService.apiService.getAllTables(new UserUidRequest(Auth.User_Uid))
                .enqueue(new Callback<ArrayList<TableModel>>() {
                    @Override
                    public void onResponse(Call<ArrayList<TableModel>> call, Response<ArrayList<TableModel>> response) {
                        if(response.isSuccessful()){
                            assert response.body() !=null;
                            tableModelList.addAll(response.body());
                            tableAdapter.notifyDataSetChanged();
                            jsonObject.addProperty("is_occupied",false); // Lấy tất cả các bàn không bận rộn
                            jsonObject.addProperty("locationId",location);
                            tableAdapter.getFilter().filter(jsonObject.toString());
                            if(binding.swipeRefreshLayout.isRefreshing()){
                                setGetTablesSuccess(true);
                                stopRefreshing();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<TableModel>> call, Throwable t) {

                    }
                });
    }
    private void buildDialog(TableModel tableModel) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        String message = "Bạn có chắc chắn muốn chuyển sang "+ tableModel.getTable_name() + " không ?";
        builder.setMessage(message)
                .setTitle("Chuyển bàn");
        builder.setPositiveButton("Chắc chắn có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isTableOccupied(tableModel);

            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
    }
    private void isTableOccupied(TableModel tableModel) {
        InProgress(true);
        OrderRequest request = new OrderRequest();
        request.setTable_id(tableModel.getTable_id());


        ApiService.apiService.orderCheckTableOccupied(request).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if(response.isSuccessful()){
                    ServerResponse res = response.body();
                    assert res != null;
                    tableModel.setIs_occupied(res.isOccupied());
                    if(!res.isOccupied()){ // bàn không bận rộn
                        Intent intentResult = new Intent();
                        intentResult.putExtra("table_selected",tableModel);
                        setResult(RESULT_OK,intentResult);
                        finish();
                    }else{
                        Toast.makeText(SelectTableActivity.this, "Bàn này đã có đơn!", Toast.LENGTH_SHORT).show();
                        tableAdapter.updateTable(tableModel);
                        tableAdapter.notifyDataSetChanged();
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("is_occupied",false);
                        jsonObject.addProperty("locationId",location);// Lấy tất cả các bàn không bận rộn
                        tableAdapter.getFilter().filter(jsonObject.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }
    private void InProgress(boolean isIn){
        if(isIn){
            binding.typeTableRv.setEnabled(false);
            binding.tableRv.setEnabled(false);
            binding.progressBarCv.setVisibility(View.VISIBLE);
        }
        binding.typeTableRv.setEnabled(true);
        binding.tableRv.setEnabled(true);
        binding.progressBarCv.setVisibility(View.GONE);
    }

    @Override
    public void onRefresh() {
        viewTableCategories();
        getAllTables();
    }
    private void stopRefreshing() {
        if(isGetTablesSuccess() && isGetCategorySuccess()){
            binding.swipeRefreshLayout.setRefreshing(false);
        }
    }
    public boolean isGetCategorySuccess() {
        return isGetCategorySuccess;
    }

    public void setGetCategorySuccess(boolean getCategorySuccess) {
        isGetCategorySuccess = getCategorySuccess;
    }

    public boolean isGetTablesSuccess() {
        return isGetTablesSuccess;
    }

    public void setGetTablesSuccess(boolean getTablesSuccess) {
        isGetTablesSuccess = getTablesSuccess;

    }
}