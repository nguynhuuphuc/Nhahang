package com.example.nhahang;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.nhahang.Adapters.CategoryTableAdapter;
import com.example.nhahang.Adapters.TableAdapter;
import com.example.nhahang.Interfaces.ApiService;
import com.example.nhahang.Interfaces.IClickItemCategoryTableListener;
import com.example.nhahang.Interfaces.IClickItemTableListener;
import com.example.nhahang.Models.LocationModel;
import com.example.nhahang.Models.MessageEvent;
import com.example.nhahang.Models.NotificationModel;
import com.example.nhahang.Models.OrderItemModel;
import com.example.nhahang.Models.OrderModel;
import com.example.nhahang.Models.Requests.JoinOrderTablesRequest;
import com.example.nhahang.Models.Requests.OrderRequest;
import com.example.nhahang.Models.Requests.UserUidRequest;
import com.example.nhahang.Models.Respones.ServerResponse;
import com.example.nhahang.Models.TableModel;
import com.example.nhahang.Utils.Auth;
import com.example.nhahang.Utils.Util;
import com.example.nhahang.databinding.ActivityChooseTableBinding;
import com.google.android.material.tabs.TabLayout;
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

public class JoinTableActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ActivityChooseTableBinding binding;
    private CategoryTableAdapter categoryTableAdapter;
    private List<LocationModel> locationList = new ArrayList<>();
    private String location = "1";
    private TableAdapter tableAdapter;
    private List<TableModel> tableModelList = new ArrayList<>();
    private AlertDialog dialog;
    private OrderModel orderModel;
    private boolean loadTableSuccess = false, loadCateSuccess = false;
    private List<OrderItemModel> orderItemModels;
    private MyApplication mApp;
    private TableModel currentTableModel;
    private List<TableModel> changeTables;
    private JsonObject jsonObject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChooseTableBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mApp = (MyApplication) getApplication();

        changeTables = new ArrayList<>();
        jsonObject = new JsonObject();
        jsonObject.addProperty("is_occupied", true);
        jsonObject.addProperty("locationId",Integer.parseInt(location));

        InProgress(false);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.toolbarTitleTv.setText("Gộp bàn");

        Intent intent = getIntent();
        orderModel = (OrderModel) intent.getSerializableExtra("order");
        orderItemModels = (List<OrderItemModel>) getIntent().getSerializableExtra("orderitems");
        currentTableModel = (TableModel) getIntent().getSerializableExtra("currentTable");





        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });


        GridLayoutManager gridLayoutManager = new GridLayoutManager(JoinTableActivity.this,2);
        binding.tableRv.setLayoutManager(gridLayoutManager);

        tableAdapter = new TableAdapter(JoinTableActivity.this,tableModelList, new IClickItemTableListener() {
            @Override
            public void onClickItemTableListener(TableModel tableModel, LinearLayout tablelabelLl, String oldPrice, int index) {
                buildDialog(tableModel);
                dialog.show();
            }
        });
        binding.tableRv.setAdapter(tableAdapter);
        binding.typeTableRv.setLayoutManager(new LinearLayoutManager(JoinTableActivity.this, LinearLayoutManager.HORIZONTAL, false));
        categoryTableAdapter = new CategoryTableAdapter(locationList, new IClickItemCategoryTableListener() {
            @Override
            public void onClickItemCategoryTableListener(LocationModel locationModel) {
                location = String.valueOf(locationModel.getLocation_id());
                jsonObject.addProperty("is_occupied", true);
                jsonObject.addProperty("locationId",Integer.parseInt(location));
                tableAdapter.getFilter().filter(jsonObject.toString());
            }

        });
        binding.typeTableRv.setAdapter(categoryTableAdapter);
        binding.swipeRefreshLayout.setOnRefreshListener(this);

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
                            setLoadCateSuccess(true);
                            stopRefreshing();
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
                            for(TableModel model : response.body()){
                                if( model.getTable_id() != orderModel.getTable_id()){
                                    tableModelList.add(model);
                                }
                            }


                            tableAdapter.getFilter().filter(jsonObject.toString());
                            ViewEmptyTables(tableModelList.isEmpty());
                            setLoadTableSuccess(true);
                            stopRefreshing();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<TableModel>> call, Throwable t) {

                    }
                });
    }
    private void buildDialog(TableModel tableModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String message = "Bạn có chắc chắn muốn gộp sang "+ tableModel.getTable_name() + " không ?";
        builder.setMessage(message)
                .setTitle("Gộp bàn");
        builder.setPositiveButton("Chắc chắn có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                joinOrderTables(tableModel);
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

    private void joinOrderTables(TableModel tableModel) {
        JoinOrderTablesRequest request = new JoinOrderTablesRequest(orderModel.getOrder_id(),tableModel.getOrder_id(),orderItemModels);
        request.setUser_uid(Auth.User_Uid);
        ApiService.apiService.joinOrderTables(request).enqueue(new Callback<ArrayList<TableModel>>() {
            @Override
            public void onResponse(Call<ArrayList<TableModel>> call, Response<ArrayList<TableModel>> response) {
                if(response.isSuccessful()){
                    List<TableModel> joinTables = new ArrayList<>();
                    assert response.body() != null;
                    joinTables.addAll(response.body());
                    Intent intentResult = new Intent();
                    intentResult.putExtra("response","JoinOrderTable");
                    intentResult.putExtra("JoinOrderTable", (Serializable) joinTables);
                    EventBus.getDefault().postSticky(joinTables);
                    notifyJoinTableSuccess(tableModel);
                    setResult(RESULT_OK,intentResult);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<TableModel>> call, Throwable t) {

            }
        });
    }

    private void notifyJoinTableSuccess(TableModel tableModel) {
        JSONObject object = new JSONObject();
        try {
            object.put("from",Auth.User_Uid);
            JSONObject message = new JSONObject();
            message.put("order_id",currentTableModel.getOrder_id());
            message.put("status","đã gộp đơn từ " + currentTableModel.getTable_name() + " vào " + tableModel.getTable_name());
            message.put("old_table_id",currentTableModel.getTable_id());
            message.put("new_table_id",tableModel.getTable_id());
            object.put("message",message);
            object.put("action","JOIN_TABLE");
            mApp.getWebSocketClient().send(object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void ViewEmptyTables(boolean isEmpty){
        if(isEmpty){
            binding.emptyTv.setVisibility(View.VISIBLE);
            return;
        }
        binding.emptyTv.setVisibility(View.INVISIBLE);
    }

    private void InProgress(boolean isIn){
        if(isIn){
            binding.typeTableRv.setEnabled(false);
            binding.tableRv.setEnabled(false);
            binding.progressBarCv.setVisibility(View.VISIBLE);
            return;
        }
        binding.typeTableRv.setEnabled(true);
        binding.tableRv.setEnabled(true);
        binding.progressBarCv.setVisibility(View.GONE);
    }
    private void stopRefreshing() {
        if(isLoadCateSuccess() && isLoadTableSuccess()){
            binding.swipeRefreshLayout.setRefreshing(false);
        }
    }

    public boolean isLoadTableSuccess() {
        return loadTableSuccess;
    }

    public void setLoadTableSuccess(boolean loadTableSuccess) {
        this.loadTableSuccess = loadTableSuccess;
    }

    public boolean isLoadCateSuccess() {
        return loadCateSuccess;
    }

    public void setLoadCateSuccess(boolean loadCateSuccess) {
        this.loadCateSuccess = loadCateSuccess;
    }

    @Override
    public void onRefresh() {
        viewTableCategories();
        getAllTables();
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
}