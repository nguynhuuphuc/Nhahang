package com.example.nhahang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.nhahang.Adapters.ItemInTemporaryPaymentAdapter;
import com.example.nhahang.Models.OrderItemModel;
import com.example.nhahang.Models.ProductInReservationModel;
import com.example.nhahang.Models.ReservationModel;
import com.example.nhahang.Models.TableModel;
import com.example.nhahang.Utils.Util;
import com.example.nhahang.databinding.ActivityTemporaryPaymentBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TemporaryPaymentActivity extends AppCompatActivity{

    private ActivityTemporaryPaymentBinding binding;
    private TableModel tableModel;
    private List<OrderItemModel> orderItemModels;
    private ItemInTemporaryPaymentAdapter itemAdapter;
    private MyApplication mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTemporaryPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        tableModel = (TableModel) intent.getSerializableExtra("table");
        orderItemModels = (List<OrderItemModel>) intent.getSerializableExtra("orderItems");
        mApp = (MyApplication) getApplication();

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.contentRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));
        itemAdapter = new ItemInTemporaryPaymentAdapter(orderItemModels,mApp.getMenuModels());
        binding.contentRv.setAdapter(itemAdapter);
        itemAdapter.setIOnChange(new ItemInTemporaryPaymentAdapter.OnDataAdapterChange() {
            @Override
            public void onChange() {
                Util.updateMoneyLabel(binding.totalPrice,itemAdapter.getOrderTotalAmount());
                Util.updateMoneyLabel(binding.totalPricePayment,itemAdapter.getOrderTotalAmount());
                binding.quantity.setText(String.valueOf(itemAdapter.getOrderTotalQuantity()));
            }
        });


    }
}