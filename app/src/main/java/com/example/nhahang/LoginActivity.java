package com.example.nhahang;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.nhahang.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.progressBar.setVisibility(View.GONE);

        binding.ccp.registerCarrierNumberEditText(binding.phoneEt);
        binding.sendOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(!binding.ccp.isValidFullNumber()){
                   binding.phoneEt.setError("Số điện thoại không đúng");
                   return;
               }
               Intent intent = new Intent(LoginActivity.this,VerifyOtpActivity.class);
               intent.putExtra("phone",binding.ccp.getFullNumberWithPlus());
               startActivity(intent);
            }
        });

    }
}