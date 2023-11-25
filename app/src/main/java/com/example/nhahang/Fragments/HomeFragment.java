package com.example.nhahang.Fragments;
import static android.app.Activity.RESULT_OK;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.nhahang.Adapters.CategoryTableAdapter;
import com.example.nhahang.Adapters.TableAdapter;

import com.example.nhahang.Interfaces.ApiService;
import com.example.nhahang.Interfaces.IClickItemCategoryTableListener;
import com.example.nhahang.Interfaces.IClickItemTableListener;
import com.example.nhahang.Interfaces.IUpdateTablesListener;
import com.example.nhahang.Models.LocationModel;
import com.example.nhahang.Models.MessageEvent;
import com.example.nhahang.Models.NotificationModel;
import com.example.nhahang.Models.Requests.UserUidRequest;
import com.example.nhahang.Models.TableModel;

import com.example.nhahang.MyApplication;
import com.example.nhahang.OrderDetailActivity;
import com.example.nhahang.R;
import com.example.nhahang.SelectProductActitvity;
import com.example.nhahang.Utils.Auth;
import com.example.nhahang.Utils.GridSpacingItemDecoration;
import com.example.nhahang.Utils.MyWebSocketClient;
import com.example.nhahang.ViewModels.WebSocketViewModel;
import com.example.nhahang.databinding.FragmentHomeBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private FragmentHomeBinding binding;

    private List<TableModel> tableModelList = new ArrayList<>();
    private TableAdapter tableAdapter;
    private List<LocationModel> locationList = new ArrayList<>();
    private CategoryTableAdapter categoryTableAdapter;
    private ActivityResultLauncher<Intent> launcher;
    private String location = "1";
    private MyApplication myApp;
    private MyWebSocketClient webSocketClient;
    private WebSocketViewModel viewModel;
    private boolean loadTableSuccess = false, loadCateSuccess = false;
    private boolean isEventBusReceive;


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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        myApp = (MyApplication) requireActivity().getApplication();
        webSocketClient = myApp.getWebSocketClient();

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            TableModel updateTable = (TableModel) data.getSerializableExtra("table");
                            String action = data.getExtras().getString("action","");
                            switch (action){
                                case "StartOrderDetail":
                                    Intent intent = new Intent(requireActivity(),OrderDetailActivity.class);
                                    intent.putExtra("table",updateTable);
                                    sendNotifyNewOrder(updateTable);
                                    launcher.launch(intent);
                                    break;
                                case "PaidOrder":
                                    updatePaidTable(updateTable.getTable_id());
                                    break;

                                case "ChangeOrderTable":
                                    List<TableModel> changeTables = (List<TableModel>) data.getSerializableExtra("changeTables");
                                    tableAdapter.updateTable(changeTables);
                                    break;
                                default:
                                    tableAdapter.updateTable(updateTable);
                                    break;
                            }

                        }
                    }
                });





        binding.typeTableRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        tableAdapter = new TableAdapter(getContext(),tableModelList, new IClickItemTableListener() {
            @Override
            public void onClickItemTableListener(TableModel tableModel, LinearLayout tablelabelLl,String oldPrice,int index) {
                if(!tableModel.is_occupied()){
                Intent intent = new Intent(getContext(), SelectProductActitvity.class);
                intent.putExtra("table",tableModel);
                intent.putExtra("activity","Home");
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        requireActivity(),
                        tablelabelLl,
                        Objects.requireNonNull(ViewCompat.getTransitionName(tablelabelLl))
                );
                launcher.launch(intent,options);
                }else{
                    Intent intent = new Intent(getContext(), OrderDetailActivity.class);
                    intent.putExtra("table",tableModel);
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            requireActivity(),
                            tablelabelLl,
                            Objects.requireNonNull(ViewCompat.getTransitionName(tablelabelLl))
                    );
                    launcher.launch(intent,options);
                }
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2);
        binding.tableRv.setLayoutManager(gridLayoutManager);

        // Đặt ItemDecoration để tạo khoảng trống ở trên và dưới các hàng của lưới
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.grid_spacing); // Đặt giá trị tùy ý
        binding.tableRv.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, true, 0));

        binding.tableRv.setAdapter(tableAdapter);



        categoryTableAdapter = new CategoryTableAdapter(locationList, new IClickItemCategoryTableListener() {
            @Override
            public void onClickItemCategoryTableListener(LocationModel locationModel) {
                location = String.valueOf(locationModel.getLocation_id());
                tableAdapter.getFilter().filter(location);
            }

        });

        binding.typeTableRv.setAdapter(categoryTableAdapter);
        binding.SwipeRefreshLayout.setOnRefreshListener(this);

        viewTableCategories();
        getAllTables();



        return view;
    }

    private void sendNotifyNewOrder(TableModel tableModel) {
        JSONObject object = new JSONObject();
        try {
            object.put("from",Auth.User_Uid);
            JSONObject message = new JSONObject();
            message.put("order_id",tableModel.getOrder_id());
            message.put("status","đã thêm đơn mới cho " + tableModel.getTable_name());
            message.put("new_table_id",tableModel.getTable_id());
            object.put("message",message);
            webSocketClient.send(object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updatePaidTable(int table_id) {
        InProgress(true);
        final TableModel[] tableModel = {new TableModel()};
        tableModel[0].setTable_id(table_id);
        ApiService.apiService.getTableById(tableModel[0]).enqueue(new Callback<TableModel>() {
            @Override
            public void onResponse(Call<TableModel> call, Response<TableModel> response) {
                if(response.isSuccessful()){
                    tableModel[0] = response.body();
                    tableAdapter.updateTable(tableModel[0]);
                    InProgress(false);
                }
            }

            @Override
            public void onFailure(Call<TableModel> call, Throwable t) {
                Toast.makeText(myApp, "Lỗi sever", Toast.LENGTH_SHORT).show();
                InProgress(false);

            }
        });        
    }

    
    private void getAllTables() {
        if(!binding.SwipeRefreshLayout.isRefreshing()){
            InProgress(true);
        }
        if(!tableModelList.isEmpty()){
            tableModelList.clear();
        }
        ApiService.apiService.getAllTables(new UserUidRequest(Auth.User_Uid))
                .enqueue(new Callback<ArrayList<TableModel>>() {
                    @Override
                    public void onResponse(Call<ArrayList<TableModel>> call, Response<ArrayList<TableModel>> response) {
                        if(response.isSuccessful()){
                            assert response.body() !=null  ;
                            tableModelList.addAll(response.body());
                            tableAdapter.getFilter().filter(location);
                            if(!binding.SwipeRefreshLayout.isRefreshing()){
                                InProgress(false);
                            }else{
                                setLoadTableSuccess(true);
                                stopRefreshing();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<TableModel>> call, Throwable t) {

                    }
                });
    }

    private void stopRefreshing() {
        if(isLoadCateSuccess() && isLoadTableSuccess()){
            binding.SwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void viewTableCategories() {
        if(!binding.SwipeRefreshLayout.isRefreshing()){
            InProgress(true);
        }
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
                            if(!binding.SwipeRefreshLayout.isRefreshing()){
                                InProgress(false);
                            }else{
                                setLoadCateSuccess(true);
                                stopRefreshing();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<LocationModel>> call, Throwable t) {

                    }
                });
    }

    void InProgress(boolean isIn){
        if(isIn){
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.tableRv.setVisibility(View.GONE);
        }
        else{
            binding.progressBar.setVisibility(View.GONE);
            binding.tableRv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRefresh() {
        viewTableCategories();
        getAllTables();

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if(event.getChangeTables() != null){
            tableAdapter.updateTable(event.getChangeTables());

        }else {
            NotificationModel model = event.getNotificationModel();
            NotificationModel.Message messageObject = model.parseMessage();
            tableAdapter.updateTable(messageObject.getUpdateTables());
        }
        if(EventBus.getDefault().removeStickyEvent(event)){
            event.setChangeTables(new ArrayList<>());
        }
    }

}
