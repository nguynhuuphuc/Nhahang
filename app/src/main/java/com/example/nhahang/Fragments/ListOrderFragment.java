package com.example.nhahang.Fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.nhahang.Adapters.OrderCheckListAdapter;
import com.example.nhahang.Interfaces.ApiService;
import com.example.nhahang.Models.EventKitchenMess;
import com.example.nhahang.Models.MessageEvent;
import com.example.nhahang.Models.NotificationModel;
import com.example.nhahang.Models.OrderKitchenModel;
import com.example.nhahang.MyApplication;
import com.example.nhahang.Utils.Action;
import com.example.nhahang.databinding.FragmentListOrderBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListOrderFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private FragmentListOrderBinding binding;
    private OrderCheckListAdapter adapter;
    private List<OrderKitchenModel> orderKitchenModels;
    private ActivityResultLauncher<Intent> launcher;
    private MyApplication mApp;

    


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentListOrderBinding.inflate(getLayoutInflater());
        binding.swipeRefreshLayout.setOnRefreshListener(this);
        mApp = (MyApplication) getActivity().getApplication();

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                if(o.getResultCode() == RESULT_OK){
                    Intent data = o.getData();
                    OrderKitchenModel model = (OrderKitchenModel) data.getSerializableExtra("OrderKitchenModel");
                    int action = data.getIntExtra("action",-1);
                    if(action == OrderKitchenModel.ORDER_KITCHEN_EMPTY){
                        adapter.orderDeletedRemoving(model);
                        return;
                    }
                    if(action == OrderKitchenModel.ORDER_DELETED){
                        adapter.orderDeletedRemoving(model);
                        return;
                    }
                    if(model.getStatus_id() == OrderKitchenModel.ORDER_CONFIRM) {
                        adapter.updateItem(model);
                        return;
                    }
                    if(model.getStatus_id() == OrderKitchenModel.ORDER_SERV){
                        adapter.updateItemToLast(model);
                        return;
                    }


                }
            }
        });
        orderKitchenModels = new ArrayList<>();
        getOrderCheckList();
        binding.checkListOrderRv.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        adapter = new OrderCheckListAdapter(getContext(),orderKitchenModels,launcher);
        binding.checkListOrderRv.setAdapter(adapter);

        return binding.getRoot();
    }



    private void getOrderCheckList() {
        ApiService.apiService.getCheckListOrder().enqueue(new Callback<ArrayList<OrderKitchenModel>>() {
            @Override
            public void onResponse(Call<ArrayList<OrderKitchenModel>> call, Response<ArrayList<OrderKitchenModel>> response) {
                if(response.isSuccessful()){
                    if(!orderKitchenModels.isEmpty()) orderKitchenModels.clear();
                    orderKitchenModels.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    if(binding.swipeRefreshLayout.isRefreshing()){
                        binding.swipeRefreshLayout.setRefreshing(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<OrderKitchenModel>> call, Throwable t) {

            }
        });
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
    public void onMessageEvent(List<OrderKitchenModel> updateKitchenCheckList) {
        for (OrderKitchenModel model : updateKitchenCheckList){
            if(model.getStatus_id() == OrderKitchenModel.ORDER_NOT_CONFIRM){
                adapter.addItem(model);
                continue;
            }
            if(model.getStatus_id() == OrderKitchenModel.ORDER_CONFIRM){
                Toast.makeText(mApp, "confirm", Toast.LENGTH_SHORT).show();
                adapter.updateItem(model);
                continue;
            }
            if(model.getStatus_id() == OrderKitchenModel.ORDER_SERV){
                adapter.updateItemToLast(model);
            }
        }
        EventBus.getDefault().removeStickyEvent(updateKitchenCheckList);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventKitchenMess event){
        Toast.makeText(mApp, event.getAction(), Toast.LENGTH_SHORT).show();
        switch (event.getAction()){
            case Action.PAID:
                adapter.removeItem(event.getOrder_item());
                return;
            default:
                getOrderCheckList();
                return;
        }

    }

    @Override
    public void onRefresh() {
        getOrderCheckList();
    }
}
