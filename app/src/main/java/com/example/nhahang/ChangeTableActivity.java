package com.example.nhahang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.nhahang.Adapters.CategoryTableAdapter;
import com.example.nhahang.Adapters.TableAdapter;
import com.example.nhahang.Interfaces.ApiService;
import com.example.nhahang.Interfaces.IClickItemCategoryTableListener;
import com.example.nhahang.Interfaces.IClickItemTableListener;
import com.example.nhahang.Interfaces.IUpdateTablesListener;
import com.example.nhahang.Models.LocationModel;
import com.example.nhahang.Models.MessageEvent;
import com.example.nhahang.Models.NotificationModel;
import com.example.nhahang.Models.OrderModel;
import com.example.nhahang.Models.Requests.OrderRequest;
import com.example.nhahang.Models.Requests.UserUidRequest;
import com.example.nhahang.Models.Respones.ServerResponse;
import com.example.nhahang.Models.TableModel;
import com.example.nhahang.Utils.Auth;
import com.example.nhahang.Utils.Util;
import com.example.nhahang.databinding.ActivityChooseTableBinding;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeTableActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private ActivityChooseTableBinding binding;
    private CategoryTableAdapter categoryTableAdapter;
    private List<LocationModel> locationList = new ArrayList<>();
    private int location = 1;
    private TableAdapter tableAdapter;
    private List<TableModel> tableModelList = new ArrayList<>();
    private AlertDialog dialog;
    private OrderModel orderModel;
    private MyApplication mApp;
    private TableModel currentTableModel;
    private  JsonObject jsonObject;
    private List<TableModel> changeTables;
    private boolean isGetCategorySuccess;
    private boolean isGetTablesSuccess;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChooseTableBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mApp = (MyApplication) getApplication();
        jsonObject = new JsonObject();

        changeTables = new ArrayList<>();

        binding.swipeRefreshLayout.setOnRefreshListener(this);

        InProgress(false);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        orderModel = (OrderModel) intent.getSerializableExtra("order");
        currentTableModel = (TableModel) intent.getSerializableExtra("currentTable");
        

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!changeTables.isEmpty()){
                    EventBus.getDefault().postSticky(new MessageEvent(changeTables,this.getClass().getSimpleName()));
                }
                getOnBackPressedDispatcher().onBackPressed();
            }
        });


        GridLayoutManager gridLayoutManager = new GridLayoutManager(ChangeTableActivity.this,2);
        binding.tableRv.setLayoutManager(gridLayoutManager);

        tableAdapter = new TableAdapter(ChangeTableActivity.this,tableModelList, new IClickItemTableListener() {
            @Override
            public void onClickItemTableListener(TableModel tableModel, LinearLayout tablelabelLl, String oldPrice, int index) {
                    buildDialog(tableModel);
                    dialog.show();
            }
        });
        binding.tableRv.setAdapter(tableAdapter);
        binding.typeTableRv.setLayoutManager(new LinearLayoutManager(ChangeTableActivity.this, LinearLayoutManager.HORIZONTAL, false));
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                        changeTable(tableModel);
                    }else{
                        Toast.makeText(ChangeTableActivity.this, "Bàn này đã có đơn!", Toast.LENGTH_SHORT).show();
                        tableAdapter.updateTable(tableModel);

                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("is_occupied",false); // Lấy tất cả các bàn không bận rộn
                        tableAdapter.getFilter().filter(jsonObject.toString());

                    }
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    private void changeTable(TableModel tableModel) {
        OrderRequest request = new OrderRequest();
        request.setOrder_id(orderModel.getOrder_id());
        request.setTable_id(tableModel.getTable_id());
        ApiService.apiService.changeOrderTable(request).enqueue(new Callback<ArrayList<TableModel>>() {
            @Override
            public void onResponse(Call<ArrayList<TableModel>> call, Response<ArrayList<TableModel>> response) {
                if(response.isSuccessful()){
                    changeTables.addAll(response.body());
                    Intent intentResult = new Intent();
                    intentResult.putExtra("ChangeTables", (Serializable) changeTables);
                    intentResult.putExtra("response", "ChangeOrderTable");
                    EventBus.getDefault().postSticky(new MessageEvent(changeTables,this.getClass().getSimpleName()));
                    notifyChangeSuccess(tableModel);
                    setResult(RESULT_OK,intentResult);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<TableModel>> call, Throwable t) {
                InProgress(false);
            }
        });
    }

    private void notifyChangeSuccess(TableModel tableModel) {
        JSONObject object = new JSONObject();
        try {
            object.put("from",Auth.User_Uid);
            JSONObject message = new JSONObject();
            message.put("order_id",currentTableModel.getOrder_id());
            message.put("status","đã chuyển đơn từ " + currentTableModel.getTable_name() + " đến " + tableModel.getTable_name());
            message.put("old_table_id",currentTableModel.getTable_id());
            message.put("new_table_id",tableModel.getTable_id());
            object.put("message",message);
            mApp.getWebSocketClient().send(object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
    public void onMessageEvent(MessageEvent event) {
        if(event.getActivitySimpleName().equals(this.getClass().getSimpleName())){
            return;
        }
        if(event.getChangeTables() == null){
            NotificationModel model = event.getNotificationModel();
            NotificationModel.Message message = model.parseMessage();

            tableAdapter.updateTable(message.getUpdateTables());
            Util.changeTablesAdding(changeTables,message.getUpdateTables());
            tableAdapter.getFilter().filter(jsonObject.toString());
        }else {
            tableAdapter.updateTable(event.getChangeTables());
            tableAdapter.getFilter().filter(jsonObject.toString());
        }

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


}