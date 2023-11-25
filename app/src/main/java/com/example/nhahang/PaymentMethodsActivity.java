package com.example.nhahang;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.nhahang.databinding.ActivityPaymentMethodsBinding;

public class PaymentMethodsActivity extends AppCompatActivity {
    private ActivityPaymentMethodsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentMethodsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });


        binding.cashMethodCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("paymentMethod","CASH");
                setResult(RESULT_OK,resultIntent);
                finish();
            }
        });
        binding.qrMethodCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("paymentMethod","VNPAY");
                setResult(RESULT_OK,resultIntent);
                finish();
            }
        });

        binding.exchangeMethodCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("paymentMethod","EXCHANGE");
                setResult(RESULT_OK,resultIntent);
                finish();
            }
        });
    }
}