package com.example.nhahang.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import com.example.nhahang.Models.CategoryTableModel;
import com.example.nhahang.Models.LocationModel;
import com.example.nhahang.Models.MessageEvent;
import com.example.nhahang.Models.NotificationModel;
import com.example.nhahang.Models.Requests.UserUidRequest;
import com.example.nhahang.Models.TableModel;
import com.example.nhahang.OrderDetailActivity;
import com.example.nhahang.R;
import com.example.nhahang.Utils.Auth;
import com.example.nhahang.Utils.GridSpacingItemDecoration;
import com.example.nhahang.databinding.FragmentHomeBinding;
import com.google.protobuf.Api;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapKitchenFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private FragmentHomeBinding binding;
    private TableAdapter tableAdapter;
    private CategoryTableAdapter categoryTableAdapter;
    private List<TableModel> tableModelList;
    private List<LocationModel> locationList;
    private boolean loadTableSuccess = false, loadCateSuccess = false;
    private boolean isEventBusReceive;
    private String location = "1";


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

        locationList = new ArrayList<>();
        viewTableCategories();
        binding.typeTableRv.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        categoryTableAdapter = new CategoryTableAdapter(locationList, new IClickItemCategoryTableListener() {
            @Override
            public void onClickItemCategoryTableListener(LocationModel locationModel) {
                location = String.valueOf(locationModel.getLocation_id());
                tableAdapter.getFilter().filter(location);
            }
        });
        binding.typeTableRv.setAdapter(categoryTableAdapter);


        tableModelList = new ArrayList<>();
        getAllTables();
        tableAdapter = new TableAdapter(getContext(), tableModelList, new IClickItemTableListener() {
            @Override
            public void onClickItemTableListener(TableModel tableModel, LinearLayout tablelabelLl, String oldPrice, int index) {
                if(tableModel.is_occupied()){
                Intent intent = new Intent(getContext(), OrderDetailActivity.class);
                intent.putExtra("table",tableModel);
                intent.putExtra("is_kitchen", true);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        requireActivity(),
                        tablelabelLl,
                        Objects.requireNonNull(ViewCompat.getTransitionName(tablelabelLl)));

                startActivity(intent,options.toBundle());
                }else{
                    Toast.makeText(getContext() , "Bàn này chưa có đơn", Toast.LENGTH_SHORT).show();
                }
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2);
        binding.tableRv.setLayoutManager(gridLayoutManager);

        // Đặt ItemDecoration để tạo khoảng trống ở trên và dưới các hàng của lưới
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.grid_spacing); // Đặt giá trị tùy ý
        binding.tableRv.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, true, 0));
        binding.tableRv.setAdapter(tableAdapter);

        binding.SwipeRefreshLayout.setOnRefreshListener(this);

        return binding.getRoot();
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
        if(EventBus.getDefault().removeStickyEvent(event)) {
            event.setChangeTables(new ArrayList<>());
        }
    }
}
