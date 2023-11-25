package com.example.nhahang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.nhahang.Interfaces.ApiService;
import com.example.nhahang.Models.AccountModel;
import com.example.nhahang.Models.Employee;
import com.example.nhahang.Models.Requests.CreatePasswordRequest;
import com.example.nhahang.Models.Requests.UserUidRequest;
import com.example.nhahang.Models.Respones.ServerResponse;
import com.example.nhahang.Utils.PasswordUtil;
import com.example.nhahang.Utils.Util;
import com.example.nhahang.ViewModels.AttributeWatcherViewModel;
import com.example.nhahang.ViewModels.CheckVerifyViewModel;
import com.example.nhahang.databinding.ActivityVerifyOtpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyOtpActivity extends AppCompatActivity {

    private ActivityVerifyOtpBinding binding;
    String phoneNumber,command;
    Long timeoutSeconds = 60L;
    String verificationCode;
    PhoneAuthProvider.ForceResendingToken resendingToken;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyOtpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });


        phoneNumber = getIntent().getExtras().getString("phone");
        command = getIntent().getExtras().getString("command");
        setView();

        binding.phoneNumberTv.setText(phoneNumber);

        sendOtp(phoneNumber,false);

        binding.passwordEt.setOnTouchListener(Util.ShowOrHidePass(binding.passwordEt));
        binding.rePasswordEt.setOnTouchListener(Util.ShowOrHidePass(binding.rePasswordEt));


        binding.enterPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = binding.passwordEt.getText().toString().trim();
                String rePassword = binding.rePasswordEt.getText().toString().trim();

                if(!rePassword.equals(password)){
                    binding.rePasswordEt.setError("Nhập lại mật khẩu không khớp!");
                    return;
                }

                String hashPassword = PasswordUtil.hashPassword(rePassword);
                CreatePasswordRequest request = new CreatePasswordRequest(mAuth.getUid(),hashPassword);
                ApiService.apiService.createPassword(request).enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ServerResponse> call, @NonNull Response<ServerResponse> response) {
                        if(response.isSuccessful()){
                            ServerResponse res = response.body();
                            assert res != null;
                            if(command.equals("forgetPassword")){
                                Toast.makeText(VerifyOtpActivity.this, "Đã đặt lại mật khẩu", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(VerifyOtpActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            Intent intent = new Intent(VerifyOtpActivity.this,MainActivity.class);
                            intent.putExtra("phoneNumber",phoneNumber);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<ServerResponse> call, Throwable t) {

                    }
                });


            }
        });
        binding.enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.otpEt.getText().toString().isEmpty()){
                    binding.otpEt.setError("Chưa nhập OTP");
                    return;
                }
                String enterOtp = binding.otpEt.getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode,enterOtp);
                signIn(credential);
                setInProgress(true);
            }
        });

        binding.resendOtpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOtp(phoneNumber,true);
            }
        });

    }

    private void setView() {
        if (command.equals("register") || command.equals("deleteAccount")) {
            binding.logoIv.setVisibility(View.GONE);
            binding.verifyPhoneNumberTv.setVisibility(View.VISIBLE);
        }
    }

    void sendOtp(String phoneNumber,boolean isResend){

        startResendTimer();
        setInProgress(true);
        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signIn(phoneAuthCredential);
                                setInProgress(false);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(VerifyOtpActivity.this, "Gửi OTP không thành công", Toast.LENGTH_SHORT).show();
                                setInProgress(false);
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verificationCode = s;
                                resendingToken = forceResendingToken;
                                Toast.makeText(VerifyOtpActivity.this, "Gửi OTP thành công", Toast.LENGTH_SHORT).show();
                                setInProgress(false);
                            }
                        });
        if(isResend){
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        }else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }
    }

    void setInProgress(boolean inProgress){
        if(inProgress){
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.enterBtn.setVisibility(View.GONE);
        }else{
            binding.progressBar.setVisibility(View.GONE);
            binding.enterBtn.setVisibility(View.VISIBLE);
        }
    }

    void signIn(PhoneAuthCredential phoneAuthCredential){
        //login and next
        setInProgress(true);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    switch (command){
                        case "deleteAccount":
                            Employee employeeD = (Employee) getIntent().getSerializableExtra("employee");
                            deleteAccount(employeeD);
                            mAuth.signOut();
                            break;
                        case "forgetPassword":
                            createPasswordView();
                            setInProgress(false);
                            break;
                        case "login":
                            setInProgress(true);
                            Util.checkAccount(VerifyOtpActivity.this,phoneNumber);
                            AttributeWatcherViewModel attributeWatcherViewModel = 
                                    new ViewModelProvider(VerifyOtpActivity.this)
                                            .get(AttributeWatcherViewModel.class);
                            attributeWatcherViewModel.getIsPasswordExistsLiveData().observe(VerifyOtpActivity.this,isExists ->{
                                if(isExists != null){
                                    if(!isExists){
                                        createPasswordView();
                                        setInProgress(false);
                                    }else {
                                        Intent intent = new Intent(VerifyOtpActivity.this,MainActivity.class);
                                        intent.putExtra("phoneNumber",phoneNumber);
                                        startActivity(intent);
                                        setInProgress(false);
                                    }
                                    attributeWatcherViewModel.getIsPasswordExistsLiveData()
                                            .removeObservers(VerifyOtpActivity.this);
                                }
                            });
                            break;
                        case "register":
                            Employee employee = (Employee) getIntent().getExtras().getSerializable("employee");
                            employee.setUser_uid(mAuth.getUid());
                            mAuth.signOut();
                            saveInfoEmployee(employee);
                            break;
                    }
                }else {
                    Toast.makeText(VerifyOtpActivity.this, "OTP không đúng", Toast.LENGTH_SHORT).show();
                    setInProgress(false);
                }
            }
        });
    }

    private void getEmployeeAndFinish(Employee employee) {
        ApiService.apiService.getEmployeeById(new UserUidRequest(employee.getUser_uid())).enqueue(new Callback<Employee>() {
            @Override
            public void onResponse(Call<Employee> call, Response<Employee> response) {
                if(response.isSuccessful()){
                   Employee employee = response.body();
                   Intent intentResult  = new Intent();
                   intentResult.putExtra("action", "NEW ACCOUNT");
                   intentResult.putExtra("new_employee",employee);
                   setResult(RESULT_OK,intentResult);
                   finish();
                }
            }

            @Override
            public void onFailure(Call<Employee> call, Throwable t) {

            }
        });
    }

    private void deleteAccount(Employee employee) {
        ApiService.apiService.deleteEmployee(new UserUidRequest(employee.getUser_uid())).enqueue(new Callback<Employee>() {
            @Override
            public void onResponse(Call<Employee> call, Response<Employee> response) {
                if(response.isSuccessful()){
                    Employee employee = response.body();
                    Intent intentResult = new Intent();
                    intentResult.putExtra("employee_deleted",employee);
                    setResult(RESULT_OK,intentResult);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Employee> call, Throwable t) {
                Toast.makeText(VerifyOtpActivity.this, "Lỗi server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createPasswordView() {
        binding.verifyOtpContentLl.setVisibility(View.GONE);
        binding.createPasswordContent.setVisibility(View.VISIBLE);
    }

    private void saveAccount(Employee employee,boolean is_verify) {
        CheckVerifyViewModel viewModel = new ViewModelProvider(VerifyOtpActivity.this).get(CheckVerifyViewModel.class);
        AccountModel account = new AccountModel(phoneNumber,null,employee.getUser_uid(),employee.getPosition_id(),is_verify);
        ApiService.apiService.addNewAccount(account).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if(response.isSuccessful()){
                    ServerResponse res = response.body();
                    // Đặt giá trị isVerify thành true
                    viewModel.setIsVerify(is_verify);
                    Toast.makeText(VerifyOtpActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                    getEmployeeAndFinish(employee);


                }else {
                    Toast.makeText(VerifyOtpActivity.this, "Thêm tài khoản thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(VerifyOtpActivity.this, "Thêm tài khoản thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveInfoEmployee(Employee employee) {
        ApiService.apiService.addNewEmployee(employee).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if(response.isSuccessful()){
                    saveAccount(employee,true);
                    ServerResponse res = response.body();
                    Toast.makeText(VerifyOtpActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                }else{
                    // Request failed, handle the error
                    int statusCode = response.code();
                    // Check the status code for specific error handling
                    switch (statusCode){
                        case 500:
                            Toast.makeText(VerifyOtpActivity.this, "Email đã tồn tại", Toast.LENGTH_SHORT).show();
                            break;
                        case 401:
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(VerifyOtpActivity.this, "Thêm tài khoản thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void startResendTimer(){
        binding.resendOtpTv.setEnabled(false);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeoutSeconds--;
                binding.resendOtpTv.setText("Gửi lại OTP trong "+ timeoutSeconds +" giây");
                if (timeoutSeconds<=0){
                    timeoutSeconds = 60L;
                    timer.cancel();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.resendOtpTv.setText(R.string.resend_otp);
                            binding.resendOtpTv.setEnabled(true);
                        }
                    });
                }
            }
        },0,1000);
    }
}