package com.example.nhahang.Utils;

import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.nhahang.Interfaces.ApiService;
import com.example.nhahang.Models.Requests.PhoneNumberRequest;
import com.example.nhahang.Models.Respones.ServerResponse;
import com.example.nhahang.ViewModels.AttributeWatcherViewModel;
import com.google.type.DateTime;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Util {
    public static final String localhost = "http://192.168.31.225:3000/";
    public static boolean formIsValid;
    public static boolean isVerify = false;

    public static void updateDateLabel(EditText editText, Date date){
        String format = "dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
        editText.setText(dateFormat.format(date));
    }
    public static Date DateParse(String dateString){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            // Parse the date string into a LocalDate
            LocalDate parsedDate = LocalDate.parse(dateString, formatter);
            Instant instant = parsedDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
            return Date.from(instant);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error parsing date string.");
            return null;
        }
    }
    public static void checkAccount(Context context,String fullNumberWithPlus){
        AttributeWatcherViewModel attributeWatcherViewModel = new ViewModelProvider((ViewModelStoreOwner) context)
                .get(AttributeWatcherViewModel.class);
        PhoneNumberRequest request = new PhoneNumberRequest(fullNumberWithPlus);
        ApiService.apiService.checkPhoneNumberRequest(request).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if(response.isSuccessful()){
                    attributeWatcherViewModel.setIsPhoneExistsLiveData(true);
                    checkPasswordExists(context,request);
                    return;
                }
                attributeWatcherViewModel.setIsPhoneExistsLiveData(false);
            }
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(context, "Lỗi server", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private static void checkPasswordExists(Context context, PhoneNumberRequest request) {
        AttributeWatcherViewModel attributeWatcherViewModel = new ViewModelProvider((ViewModelStoreOwner) context)
                .get(AttributeWatcherViewModel.class);
        ApiService.apiService.checkPasswordExists(request).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if(response.isSuccessful()){
                    attributeWatcherViewModel.setIsPasswordExistsLiveData(true);
                    return;
                }
                attributeWatcherViewModel.setIsPasswordExistsLiveData(false);
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(context, "Lỗi server", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
