package com.example.nhahang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.nhahang.Adapters.ItemInTemporaryPaymentAdapter;
import com.example.nhahang.Models.OrderItemModel;
import com.example.nhahang.Models.PaidOrderModel;
import com.example.nhahang.Models.TableModel;
import com.example.nhahang.Utils.Util;
import com.example.nhahang.databinding.ActivityTemporaryPaymentBinding;

import java.util.List;

public class TemporaryPaymentActivity extends AppCompatActivity{

    private ActivityTemporaryPaymentBinding binding;
    private TableModel tableModel;
    private List<OrderItemModel> orderItemModels;
    private ItemInTemporaryPaymentAdapter itemAdapter;
    private MyApplication mApp;
    private boolean ignored;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTemporaryPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        tableModel = (TableModel) intent.getSerializableExtra("table");
        orderItemModels = (List<OrderItemModel>) intent.getSerializableExtra("orderItems");
        String activity = intent.getStringExtra("activity");
        if(activity != null && activity.equals(PaidOrdersManagementActivity.class.getSimpleName())){
            binding.toolbar.setTitle("Chi tiết hóa đơn");
            PaidOrderModel paidOrderModel = (PaidOrderModel) intent.getSerializableExtra("paidOrder");
            if(paidOrderModel.getDiscount_amount() > 0){
                ignored = true;
                double totalPrice = paidOrderModel.getTotal_amount() - paidOrderModel.getDiscount_amount();
                Util.updateMoneyLabel(binding.totalPricePayment,totalPrice);
                Util.updateMoneyLabel(binding.discountAmountTv,paidOrderModel.getDiscount_amount());
            }else
            if (paidOrderModel.getDiscount_percentage() > 0){
                ignored = true;
                binding.discountTv.setText(paidOrderModel.getDiscount_percentage()+"%");
                double totalPrice = Util.tinhTienSauGiamGia(paidOrderModel.getTotal_amount(),paidOrderModel.getDiscount_percentage());
                Util.updateMoneyLabel(binding.totalPricePayment,totalPrice);
                Util.updateMoneyLabel(binding.discountAmountTv,Util.tinhTienGiamGia(paidOrderModel.getTotal_amount(),paidOrderModel.getDiscount_percentage()));
            }
            invisibleView(true);
        }else{
            invisibleView(false);
        }
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
                    binding.quantity.setText(String.valueOf(itemAdapter.getOrderTotalQuantity()));
                    if(!ignored){
                        Util.updateMoneyLabel(binding.totalPricePayment,itemAdapter.getOrderTotalAmount());
                    }
            }
        });


    }

    private void invisibleView(boolean b) {
        if(b){
            binding.thuKhacLL.setVisibility(View.GONE);
            return;
        }

        binding.thuKhacLL.setVisibility(View.VISIBLE);

    }
}