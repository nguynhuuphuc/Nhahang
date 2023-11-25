package com.example.nhahang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.nhahang.Abstracts.PaginationScrollListener;
import com.example.nhahang.Adapters.NotificationAdapter;
import com.example.nhahang.Interfaces.ApiService;
import com.example.nhahang.Models.NotificationModel;
import com.example.nhahang.Models.Requests.NotificationRequest;
import com.example.nhahang.databinding.ActivityNotificationBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {
    private ActivityNotificationBinding binding;
    private List<NotificationModel> notificationModels;
    private long notifyCount;
    private NotificationAdapter notificationAdapter;
    private boolean isLoading;
    private boolean isLastPage;
    private int totalPage;
    private int currentPage = 1;
    private MyApplication mApp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mApp = (MyApplication) getApplication();

        ApiService.apiService.getNotificationsCount().enqueue(new Callback<NotificationModel>() {
            @Override
            public void onResponse(Call<NotificationModel> call, Response<NotificationModel> response) {
                if(response.isSuccessful()){
                    notifyCount = response.body().getNotify_count();
                    Toast.makeText(mApp, String.valueOf(notifyCount), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<NotificationModel> call, Throwable t) {

            }
        });

        setSupportActionBar(binding.toolbar);

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(NotificationActivity.this, RecyclerView.VERTICAL,false);
        binding.notificationRv.setLayoutManager(linearLayoutManager);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        binding.notificationRv.addItemDecoration(itemDecoration);

        notificationAdapter = new NotificationAdapter(mApp.getEmployees(),this);

        binding.notificationRv.setAdapter(notificationAdapter);

        setFirstData();

        binding.notificationRv.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            public void loadMoreItem() {
                isLoading = true;
                currentPage += 1;
                loadNextPage();
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }
        });

    }

    private void loadNextPage() {
        int positon = notificationModels.size() - 2;
        Date date = notificationModels.get(positon).getNotify_time();
        ApiService.apiService.getNotifications(new NotificationRequest(date)).enqueue(new Callback<ArrayList<NotificationModel>>() {
            @Override
            public void onResponse(Call<ArrayList<NotificationModel>> call, Response<ArrayList<NotificationModel>> response) {
                if(response.isSuccessful()){
                    notificationAdapter.removeFooterLoading();
                    notificationModels.addAll(response.body());
                    notificationAdapter.notifyDataSetChanged();

                    isLoading = false;
                    if(notificationModels.size() < notifyCount){
                        notificationAdapter.addFooterLoading();
                    }else {
                        isLastPage = true;
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<NotificationModel>> call, Throwable t) {

            }
        });
    }

    private void setFirstData(){
        notificationModels = new ArrayList<>();

        ApiService.apiService.getNotifications().enqueue(new Callback<ArrayList<NotificationModel>>() {
           @Override
           public void onResponse(Call<ArrayList<NotificationModel>> call, Response<ArrayList<NotificationModel>> response) {
               if(response.isSuccessful()){
                    notificationModels.addAll(response.body());
                    notificationAdapter.setData(notificationModels);
                    if(notificationModels.size() < notifyCount){
                        notificationAdapter.addFooterLoading();
                    }else {
                        isLastPage = true;
                    }
               }
           }

           @Override
           public void onFailure(Call<ArrayList<NotificationModel>> call, Throwable t) {

           }
       });

   }
}