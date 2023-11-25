package com.example.nhahang;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.nhahang.databinding.ActivityPasswordLoginBinding;
import com.google.android.play.core.integrity.e;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class PasswordLoginActivity extends AppCompatActivity {

    private ActivityPasswordLoginBinding binding;
    private String phoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPasswordLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        phoneNumber = getIntent().getExtras().getString("phone");
        binding.phoneNumberTv.setText(phoneNumber);

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        binding.enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.passwordEt.getText().toString().isEmpty()){
                    binding.passwordEt.setError("Chưa nhập OTP");
                    return;
                }
                String enterOtp = binding.passwordEt.getText().toString();

            }
        });


    }

}