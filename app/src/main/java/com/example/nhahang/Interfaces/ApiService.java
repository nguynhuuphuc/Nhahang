package com.example.nhahang.Interfaces;

import com.example.nhahang.Models.AccountModel;
import com.example.nhahang.Models.Requests.CheckEmailRequest;
import com.example.nhahang.Models.Employee;
import com.example.nhahang.Models.Requests.CreatePasswordRequest;
import com.example.nhahang.Models.Requests.LoginRequest;
import com.example.nhahang.Models.Requests.PhoneNumberRequest;
import com.example.nhahang.Models.Requests.PositionIdRequest;
import com.example.nhahang.Models.Respones.LoginResponse;
import com.example.nhahang.Models.Respones.ServerResponse;
import com.example.nhahang.Models.Requests.UserUidRequest;
import com.example.nhahang.Utils.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    ApiService apiService = new Retrofit.Builder()
            .baseUrl(Util.localhost)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);

    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("user/employee")
    Call<Employee> getEmployee(@Body UserUidRequest userUidRequest);

    @POST("user/update_employee")
    Call<ServerResponse> updateEmployee(@Body Employee employee);

    @POST("user/add_new_employee")
    Call<ServerResponse> addNewEmployee(@Body Employee employee);

    @POST("user/email_is_exists")
    Call<ServerResponse> checkEmailExists(@Body CheckEmailRequest checkEmailRequest);

    @POST("user/phone_number_is_exists")
    Call<ServerResponse> checkPhoneNumberRequest(@Body PhoneNumberRequest phoneNumberRequest);

    @POST("user/get_list_employees_byPositionId")
    Call<ArrayList<Employee>> getListEmployeesByPositionId(@Body PositionIdRequest positionIdRequest);

    @POST("account/check_password_exists")
    Call<ServerResponse> checkPasswordExists(@Body PhoneNumberRequest phoneNumberRequest);


    @POST("account/add_new_account")
    Call<ServerResponse> addNewAccount(@Body AccountModel accountModel);

    @POST("account/create_password")
    Call<ServerResponse> createPassword(@Body CreatePasswordRequest passwordRequest);

}
