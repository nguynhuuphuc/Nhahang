package com.example.nhahang;

import androidx.appcompat.app.AppCompatActivity;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.nhahang.Interfaces.ApiService;
import com.example.nhahang.Models.Requests.PhoneNumberRequest;
import com.example.nhahang.Models.Requests.LoginRequest;
import com.example.nhahang.Models.Respones.LoginResponse;
import com.example.nhahang.Models.Respones.ServerResponse;
import com.example.nhahang.Utils.Util;
import com.example.nhahang.ViewModels.AttributeWatcherViewModel;
import com.example.nhahang.databinding.ActivityLoginBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    public static final String ACTION_FINISH_ACTIVITY = "com.example.ACTION_FINISH_ACTIVITY";
    private BroadcastReceiver finishActivityReceiver;

    private ActivityLoginBinding binding;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        binding.progressBar.setVisibility(View.GONE);

        binding.ccp.registerCarrierNumberEditText(binding.phoneEt);


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.sendOtpBtn.setVisibility(View.VISIBLE);
                binding.loginBtn.setVisibility(View.GONE);
                binding.passwordLogin.setVisibility(View.VISIBLE);
                binding.otpLogin.setVisibility(View.GONE);

                binding.passowrdLl.setVisibility(View.GONE);
            }
        };

        binding.otpLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onClick(binding.otpLogin);
            }
        });

        AttributeWatcherViewModel attributeWatcherViewModel = new ViewModelProvider(this)
                .get(AttributeWatcherViewModel.class);






        binding.passwordLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.sendOtpBtn.setVisibility(View.GONE);
                binding.loginBtn.setVisibility(View.VISIBLE);
                binding.passwordLogin.setVisibility(View.GONE);
                binding.otpLogin.setVisibility(View.VISIBLE);

                binding.passowrdLl.setVisibility(View.VISIBLE);

            }
        });

        binding.phoneEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!binding.ccp.isValidFullNumber()){
                    binding.phoneEt.setError("Số điện thoại không đúng");
                    return;
                }
                Context context = LoginActivity.this;
                String fullNumberWithPlus = binding.ccp.getFullNumberWithPlus();
                Util.checkAccount(context,fullNumberWithPlus);
                attributeWatcherViewModel.getIsPasswordExistsLiveData()
                        .observe(LoginActivity.this,isExists ->{
                            if(isExists != null){
                                if(!isExists){
                                    attributeWatcherViewModel.getIsPasswordExistsLiveData()
                                            .removeObservers(LoginActivity.this);
                                    onClickListener.onClick(binding.otpLogin);
                                }
                            }
                        });

            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });


        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!binding.ccp.isValidFullNumber()){
                    binding.phoneEt.setError("Số điện thoại không đúng");
                    return;
                }

                String phone = binding.ccp.getFullNumberWithPlus();
                String password = binding.passwordEt.getText().toString().trim();
                LoginRequest loginRequest = new LoginRequest(phone,password);
                inProgress(true);
                ApiService.apiService.login(loginRequest).enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if (response.isSuccessful()) {
                            // Request was successful, process the response
                            LoginResponse r = response.body();
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            assert r != null;
                            intent.putExtra("user_uid", r.getUser_uid());
                            intent.putExtra("phoneNumber",binding.ccp.getFullNumberWithPlus());

                            startActivity(intent);
                            finish();
                        } else {
                            // Request failed, handle the error
                            int statusCode = response.code();
                            // Check the status code for specific error handling
                            switch (statusCode){
                                case 500:
                                    Toast.makeText(LoginActivity.this, "An error occurred during login.", Toast.LENGTH_SHORT).show();
                                    break;
                                case 401:
                                    Toast.makeText(LoginActivity.this, "Số điện thoại hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            inProgress(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                        inProgress(false);
                    }
                });
            }
        });

        binding.sendOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(!binding.ccp.isValidFullNumber()){
                   binding.phoneEt.setError("Số điện thoại không đúng");
                   return;
               }
               ApiService.apiService.checkPhoneNumberRequest(new PhoneNumberRequest(binding.ccp.getFullNumberWithPlus()))
                               .enqueue(new Callback<ServerResponse>() {
                                   @Override
                                   public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                                       if(response.isSuccessful()){
                                           Intent intent = new Intent(LoginActivity.this,VerifyOtpActivity.class);
                                           intent.putExtra("phone",binding.ccp.getFullNumberWithPlus());
                                           intent.putExtra("command","login");
                                           startActivity(intent);
                                           return;
                                       }
                                       if (response.code() == 401) {
                                           binding.phoneEt.setError("Số điện thoại không tồn tại");
                                       }
                                   }

                                   @Override
                                   public void onFailure(Call<ServerResponse> call, Throwable t) {

                                   }
                               });
            }
        });
        finishActivityReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(ACTION_FINISH_ACTIVITY)) {
                    finish();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter(ACTION_FINISH_ACTIVITY);
        registerReceiver(finishActivityReceiver, intentFilter);
    }

    private boolean isPhoneNumberExists() {
        final boolean[] isExists = {true};
        PhoneNumberRequest phoneNumberRequest = new PhoneNumberRequest( binding.ccp.getFullNumberWithPlus());
        ApiService.apiService.checkPhoneNumberRequest(phoneNumberRequest)
                .enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if(response.isSuccessful()){
                    isExists[0] = true;
                    return;
                }
                if (response.code() == 401) {
                    binding.phoneEt.setError("Số điện thoại không tồn tại");
                    isExists[0] = false;
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });

        return isExists[0];
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(finishActivityReceiver);
    }
    private void inProgress(boolean isIn){
        if(isIn){
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.loginBtn.setVisibility(View.GONE);
        }
        binding.progressBar.setVisibility(View.GONE);
        binding.loginBtn.setVisibility(View.VISIBLE);
    }
}