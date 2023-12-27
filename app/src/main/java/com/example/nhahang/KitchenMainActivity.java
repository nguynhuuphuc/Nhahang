package com.example.nhahang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.MenuItem;
import android.widget.Toast;


import com.example.nhahang.Adapters.KitchenViewPagerAdapter;
import com.example.nhahang.Adapters.ViewPagerAdapter;
import com.example.nhahang.Interfaces.ApiService;
import com.example.nhahang.Interfaces.WebSocketListener;
import com.example.nhahang.Models.Employee;
import com.example.nhahang.Models.EventKitchenMess;
import com.example.nhahang.Models.MessageEvent;
import com.example.nhahang.Models.NotificationModel;
import com.example.nhahang.Models.Requests.PositionIdRequest;
import com.example.nhahang.Models.Requests.UserUidRequest;
import com.example.nhahang.Utils.Action;
import com.example.nhahang.Utils.Auth;
import com.example.nhahang.Utils.MySharedPreferences;
import com.example.nhahang.Utils.MyWebSocketClient;
import com.example.nhahang.Utils.Util;
import com.example.nhahang.databinding.ActivityKitchenMainBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KitchenMainActivity extends AppCompatActivity implements WebSocketListener {
    private ActivityKitchenMainBinding binding;
    private MyApplication myApp;
    private MyWebSocketClient webSocketClient;
    private MessageEvent event;
    private List<Employee> employees;
    private boolean keepLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityKitchenMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        event = new MessageEvent(this.getClass().getSimpleName());
        event.setChangeTables(new ArrayList<>());
        Auth.User_Uid = getIntent().getStringExtra("user_uid");
        Auth.PhoneNumber = getIntent().getStringExtra("phoneNumber");
        keepLogin = getIntent().getBooleanExtra("keepLogin",false);

        if(Auth.User_Uid == null){
            Auth.User_Uid = MySharedPreferences.getUserUid(getApplication());
            Auth.PhoneNumber = MySharedPreferences.getPhone(getApplication());
            Auth.PositionID = MySharedPreferences.getPositionId(getApplication());
        }

        if(!keepLogin)
            authSigined();



        myApp = (MyApplication) getApplication();
        webSocketClient = myApp.getWebSocketClient();
        employees = myApp.getEmployees();


        if(employees == null){
            employees = new ArrayList<>();

            ApiService.apiService.getListEmployeesByPositionId(new PositionIdRequest("*"))
                    .enqueue(new Callback<ArrayList<Employee>>() {
                        @Override
                        public void onResponse(Call<ArrayList<Employee>> call, Response<ArrayList<Employee>> response) {
                            switch (response.code()){
                                case 500:
                                    Toast.makeText(KitchenMainActivity.this, "Lỗi server", Toast.LENGTH_SHORT).show();
                                    break;
                                default://response success
                                    employees.addAll(response.body());
                                    myApp.setEmployees(employees);
                                    break;
                            }
                        }

                        @Override
                        public void onFailure(Call<ArrayList<Employee>> call, Throwable t) {

                        }
                    });
        }



        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setUpViewPager();
        binding.viewPager.setUserInputEnabled(false);

        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.order:
                        binding.viewPager.setCurrentItem(0);
                        break;
                    case R.id.user:
                        binding.viewPager.setCurrentItem(1);
                        break;
                }
                return true;
            }
        });

        if (myApp.getWebSocketClient() != null) {
            // WebSocket client is already created and connected
            // Use the same client for this activity
            myApp.getWebSocketClient().setWebSocketListener(this);
        } else {
            // Create a new WebSocket client and establish a connection
            try {
                webSocketClient = new MyWebSocketClient(new URI(Util.WEBSOCKET_URL));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            webSocketClient.connect();
            // Save the WebSocket client to the Application class
            myApp.setWebSocketClient(webSocketClient);
            myApp.getWebSocketClient().setWebSocketListener(this);

        }

    }
    private void authSigined() {
        UserUidRequest userUidRequest = new UserUidRequest(Auth.User_Uid);
        ApiService.apiService.getEmployee(userUidRequest).enqueue(new Callback<Employee>() {
            @Override
            public void onResponse(Call<Employee> call, Response<Employee> response) {
                if(response.isSuccessful()){
                    Employee employee = response.body();
                    assert employee != null;
                    String s = "Xin chào "+ employee.getFull_name();
                    Toast.makeText(KitchenMainActivity.this, s, Toast.LENGTH_SHORT).show();
                    MySharedPreferences.saveUserUid(getApplication(),Auth.User_Uid);
                    MySharedPreferences.savePhone(getApplication(),Auth.PhoneNumber);
                    MySharedPreferences.savePosition(getApplication(),"B");
                    Intent finishIntent = new Intent(LoginActivity.ACTION_FINISH_ACTIVITY);
                    sendBroadcast(finishIntent);
                }
            }
            @Override
            public void onFailure(Call<Employee> call, Throwable t) {
                startActivity(new Intent(KitchenMainActivity.this,LoginActivity.class));
                finish();
            }
        });

    }

    private void setUpViewPager() {
        KitchenViewPagerAdapter viewPagerAdapter = new KitchenViewPagerAdapter(KitchenMainActivity.this);
        binding.viewPager.setAdapter(viewPagerAdapter);

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        binding.toolbarTitle.setText("Đơn món");
                        binding.bottomNavigation.setSelectedItemId(R.id.order);
                        break;
                    case 1:
                        binding.toolbarTitle.setText("Hồ sơ");
                        binding.bottomNavigation.setSelectedItemId(R.id.user);
                        break;
                }
                super.onPageSelected(position);
            }
        });
    }
    private void popUpNotification(CharSequence contentText, NotificationModel notificationModel) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(KitchenMainActivity.this, MyApplication.CHANNEL_ID_2)
                .setSmallIcon(R.drawable.hp_res_logo)
                .setContentTitle("Thông báo")
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager != null){
            notificationManager.notify((int) notificationModel.getId(),builder.build());

        }
    }
    private CharSequence getMessage(NotificationModel notificationModel, NotificationModel.Message messageObject) {
        SpannableString created_by = null;
        for (Employee employee : employees){
            if(employee.getUser_uid().equals(notificationModel.getCreated_by())){
                created_by = new SpannableString(employee.getFull_name());
                created_by.setSpan(new StyleSpan(Typeface.BOLD), 0, employee.getFull_name().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            }
        }
        return (CharSequence) TextUtils.concat(created_by, " ", messageObject.getStatus());
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.toolbarTitle.setText("Connected");
                Toast.makeText(myApp, "Open Socket", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMessage(String message) {
        NotificationModel notificationModel = new Gson().fromJson(message,NotificationModel.class);
        NotificationModel.Message messageObject = notificationModel.parseMessage();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!notificationModel.getAction().equals(Action.UNDEFINED)){
                    EventBus.getDefault().post(new EventKitchenMess((int)messageObject.getOrder_id(),notificationModel.getAction()));
                }
                if(messageObject.getUpdateKitchenCheckList() != null){
                    if(!notificationModel.getCreated_by().equals(Auth.User_Uid)) {
                        CharSequence contentText = getMessage(notificationModel, messageObject);
                        popUpNotification(contentText, notificationModel);
                    }
                    EventBus.getDefault().postSticky(messageObject.getUpdateKitchenCheckList());
                }
                if(messageObject.getUpdateTables() != null){
                    Util.changeTablesAdding(event.getChangeTables(),messageObject.getUpdateTables());
                    EventBus.getDefault().postSticky(event);
                }

            }
        });

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        if(remote){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.toolbarTitle.setText("Disconnect");
                    Toast.makeText(myApp, "Close", Toast.LENGTH_SHORT).show();
                    try {
                        Thread.sleep(2000);
                        webSocketClient.reconnect();
                        myApp.setWebSocketClient(webSocketClient);
                    } catch (InterruptedException ignored) {
                    }


                }
            });
        }


    }

    @Override
    public void onError(Exception ex) {

    }
}