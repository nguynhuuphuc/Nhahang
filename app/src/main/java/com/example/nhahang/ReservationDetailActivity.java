package com.example.nhahang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.nhahang.Adapters.OrderItemAdapter;
import com.example.nhahang.Interfaces.ApiService;
import com.example.nhahang.Models.OrderItemModel;
import com.example.nhahang.Models.Requests.OrderRequest;
import com.example.nhahang.Models.Requests.ServerRequest;
import com.example.nhahang.Models.ReservationModel;
import com.example.nhahang.Models.Respones.ServerResponse;
import com.example.nhahang.Utils.Auth;
import com.example.nhahang.Utils.Util;
import com.example.nhahang.databinding.ActivityReservationDetailBinding;
import com.google.protobuf.Api;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationDetailActivity extends AppCompatActivity {
    private ActivityReservationDetailBinding binding;
    private ReservationModel reservation;
    private MyApplication mApp;
    private List<OrderItemModel> orderItemModelList;
    private OrderItemAdapter orderItemAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReservationDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mApp = (MyApplication) getApplication();

        reservation = (ReservationModel) getIntent().getSerializableExtra("reservation");

        if(reservation.getStatus().equals("Chờ xác nhận")){
            binding.acceptTv.setEnabled(true);
            binding.acceptTv.setBackground(getDrawable(R.drawable.radius_sky_blue_background));
        }else{
            binding.acceptTv.setEnabled(false);
            binding.acceptTv.setBackground(getDrawable(R.drawable.radius_light_blue_background));
        }
        if(reservation.getTable_id() == -1){
            binding.tableId.setText("Chưa chọn bàn");
        }else{
            String tableName = "Bàn số "+ reservation.getTable_id();
            binding.tableId.setText(tableName);
        }

        if(reservation.getOrder_id() > 0){
        orderItemModelList = new ArrayList<>();
        orderItemAdapter = new OrderItemAdapter(this,orderItemModelList,mApp.getMenuModels(),null);
        binding.productsRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));
        binding.productsRv.setAdapter(orderItemAdapter);
        getOrderItems();
            Util.updateMoneyLabel(binding.totalPrice,reservation.getTotal_amount());
        }

        setSupportActionBar(binding.toolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        binding.acceptTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.acceptTv.setEnabled(false);
                binding.acceptTv.setBackground(getDrawable(R.drawable.radius_light_blue_background));
                updateStatus();
            }
        });

    }

    private void getOrderItems() {
        inProgress(true);
        ApiService.apiService.getOrderItems(new OrderRequest(reservation.getOrder_id(),false )).enqueue(new Callback<ArrayList<OrderItemModel>>() {
            @Override
            public void onResponse(Call<ArrayList<OrderItemModel>> call, Response<ArrayList<OrderItemModel>> response) {
                if(response.isSuccessful()){
                    orderItemModelList.addAll(response.body());
                    orderItemAdapter.notifyDataSetChanged();
                    binding.quantity.setText(String.valueOf(orderItemAdapter.getQuantity()));
                    inProgress(false);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<OrderItemModel>> call, Throwable t) {
                Toast.makeText(mApp, "Server error", Toast.LENGTH_SHORT).show();
                inProgress(false);

            }
        });
    }

    private void updateStatus() {

        ServerRequest request = new ServerRequest("Đã xác nhận",reservation.getId(),new Date());
        ApiService.apiService.updateReservationStatus(request).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if(response.isSuccessful()){
                    reservation.setStatus("Đã xác nhận");
                    notifyReservation();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(mApp, "Server error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void notifyReservation() {
        JSONObject object = new JSONObject();
        try {
            object.put("from", Auth.User_Uid);
            object.put("isCustomer",true);
            object.put("to",reservation.getCustomer_id());
            JSONObject message = new JSONObject();
            message.put("reservation_id",reservation.getId());
            message.put("status","đã xác nhận đặt bàn ");
            message.put("booking_table_id",reservation.getTable_id());
            object.put("message",message);
            object.put("action","ACCEPT_BOOKING");
            mApp.getWebSocketClient().send(object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void inProgress(boolean isIn){
        if(isIn){
            binding.ProgressBar.setVisibility(View.VISIBLE);
            binding.contentRl.setVisibility(View.GONE);
        }else{
            binding.ProgressBar.setVisibility(View.GONE);
            binding.contentRl.setVisibility(View.VISIBLE);
        }
    }
}