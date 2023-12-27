package com.example.nhahang;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.nhahang.Interfaces.ApiService;

import com.example.nhahang.Models.Employee;
import com.example.nhahang.Models.Requests.PhoneNumberRequest;
import com.example.nhahang.Models.Respones.ServerResponse;
import com.example.nhahang.Utils.EmailValidator;
import com.example.nhahang.Utils.Util;
import com.example.nhahang.ViewModels.CheckVerifyViewModel;
import com.example.nhahang.databinding.ActivityRegisterNewStaffBinding;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterNewStaffActivity extends AppCompatActivity {
    private ActivityRegisterNewStaffBinding binding;
    String phoneNumber;
    Calendar mCalendar = Calendar.getInstance();
    private ActivityResultLauncher<Intent> launcher;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterNewStaffBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setTitle("Tạo tài khoản nhân viên");
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                if(o.getResultCode() == RESULT_OK){
                    Intent data = o.getData();
                    setResult(RESULT_OK,data);
                    finish();

                }
            }
        });

        CheckVerifyViewModel viewModel = new ViewModelProvider(this).get(CheckVerifyViewModel.class);


        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mCalendar.set(Calendar.YEAR,year);
                mCalendar.set(Calendar.MONTH,month);
                mCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                updateLabel();
            }
        };
        binding.verifyOtpLl.setVisibility(View.GONE);
        inProgress(false);
        binding.ccp.registerCarrierNumberEditText(binding.phoneEt);
        binding.dateBornEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(RegisterNewStaffActivity.this,date
                        ,mCalendar.get(Calendar.YEAR)
                        ,mCalendar.get(Calendar.MONTH)
                        ,mCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
                dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                dialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            }
        });
        binding.emailEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(EmailValidator.isValidEmail(binding.emailEt.getText().toString().trim())){
                    return;
                }
                binding.emailEt.setError("Email chưa đúng!");

            }
        });

        binding.phoneEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(binding.ccp.isValidFullNumber()){
                PhoneNumberRequest phoneNumberRequest = new PhoneNumberRequest(binding.ccp.getFullNumberWithPlus());
                ApiService.apiService.checkPhoneNumberRequest(phoneNumberRequest)
                        .enqueue(new Callback<ServerResponse>() {
                            @Override
                            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                                if(response.isSuccessful()){
                                    binding.phoneEt.setError("Số điện thoại đã tồn tại");
                                    Util.formIsValid = false;
                                    return;
                                }
                                Util.formIsValid = true;
                            }

                            @Override
                            public void onFailure(Call<ServerResponse> call, Throwable t) {

                            }
                        });
                return;
                }
                Util.formIsValid = true;
            }
        });

        binding.resendOtpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Util.formIsValid){
                    return;
                }
                if(!isValid()) {
                    return;
                }

                phoneNumber = binding.ccp.getFullNumberWithPlus();
                Map<String, Object> user = new HashMap<>();
                user.put("name",binding.nameEt.getText().toString().trim());
                user.put("phone",phoneNumber);
                user.put("email",binding.emailEt.getText().toString().trim());
                user.put("dateofbirth",binding.dateBornEt.getText().toString().trim());
                RadioButton sexChecked = null;
                if (binding.maleRb.isChecked()) {
                    sexChecked = binding.maleRb;
                }
                if (binding.femaleRb.isChecked()) {
                    sexChecked = binding.femaleRb;
                }
                if (binding.ortherRb.isChecked()) {
                    sexChecked = binding.ortherRb;
                }
                if (sexChecked != null)
                    user.put("sex", sexChecked.getText().toString().trim());
                else
                    user.put("sex","");
                user.put("avatar","");
                user.put("position","NV");
                Employee employee = new Employee();
                employee.setFull_name((String) user.get("name"));
                if(user.get("email") != null && !user.get("email").toString().isEmpty()){
                    employee.setEmail((String) user.get("email"));
                }
                employee.setGender((String) user.get("sex"));
                if(user.get("dateofbirth") != null && !user.get("dateofbirth").toString().isEmpty()){
                    employee.setDate_of_birth(Util.DateParse((String) user.get("dateofbirth")));
                }
                employee.setPosition_id((String) user.get("position"));
                verifyOTP(employee);
            }

        });

    }

    private void verifyOTP(Employee employee) {
        Intent intent = new Intent(RegisterNewStaffActivity.this,VerifyOtpActivity.class);
        intent.putExtra("phone",binding.ccp.getFullNumberWithPlus());
        intent.putExtra("command","register");
        intent.putExtra("employee",employee);
        launcher.launch(intent);
    }

    private boolean isValid(){
        Util.formIsValid = true;
        String name;
        name = binding.nameEt.getText().toString().trim();
        if(name.isEmpty()){
            binding.nameEt.setError("Tên không được bỏ trống!");
            Util.formIsValid = false;
        }
        if(binding.phoneEt.getText().toString().isEmpty()){
            binding.phoneEt.setError("Số điện thoại không được bỏ trống!");
            Util.formIsValid = false;
        }else if (!binding.ccp.isValidFullNumber()){
            binding.phoneEt.setError("Số điện thoại không hợp lệ!");
            Util.formIsValid = false;
        }
        return Util.formIsValid;
    }
    private void inProgress(boolean isInProgress) {
        if(isInProgress){
            binding.progressbar.setVisibility(View.VISIBLE);
            binding.content.setVisibility(View.GONE);
            return;
        }
        binding.progressbar.setVisibility(View.GONE);
        binding.content.setVisibility(View.VISIBLE);
    }
    private void updateLabel() {
        String format = "dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
        binding.dateBornEt.setText(dateFormat.format(mCalendar.getTime()));
    }
}