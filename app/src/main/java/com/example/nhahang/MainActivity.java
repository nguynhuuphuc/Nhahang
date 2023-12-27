package com.example.nhahang;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nhahang.Adapters.ViewPagerAdapter;
import com.example.nhahang.Fragments.HomeFragment;
import com.example.nhahang.Interfaces.ApiService;
import com.example.nhahang.Interfaces.IUpdateTablesListener;
import com.example.nhahang.Interfaces.WebSocketListener;
import com.example.nhahang.Models.Employee;
import com.example.nhahang.Models.IgnoreEvent;
import com.example.nhahang.Models.MenuModel;
import com.example.nhahang.Models.MessageEvent;
import com.example.nhahang.Models.MessageModel;
import com.example.nhahang.Models.NotificationModel;
import com.example.nhahang.Models.PaymentMethodModel;
import com.example.nhahang.Models.Requests.PositionIdRequest;
import com.example.nhahang.Models.Requests.UserUidRequest;
import com.example.nhahang.Models.ReservationModel;
import com.example.nhahang.Models.TableModel;
import com.example.nhahang.Utils.Auth;
import com.example.nhahang.Utils.MySharedPreferences;
import com.example.nhahang.Utils.MyWebSocketClient;
import com.example.nhahang.Utils.Util;
import com.example.nhahang.databinding.ActivityMainBinding;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;

import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


    public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, WebSocketListener {

    private ActivityMainBinding binding;
    private MyApplication myApp;
    private MyWebSocketClient webSocketClient;
    private BadgeDrawable badge,messBadge;
    private List<MenuModel> menuModels;
    private FirebaseFirestore firestore;
    private List<Employee> employees;
    private List<PaymentMethodModel> paymentMethods;
    private MessageEvent event;
    private List<ReservationModel> changeReservations;
    private ActivityResultLauncher<Intent> launcher;
    private  IgnoreEvent ignoreEvent;


        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        registerLauncher();
        EventBus.getDefault().register(this);
        badge = BadgeDrawable.create(this);
        badge.setBackgroundColor(ContextCompat.getColor(this,R.color.red)); // Customize the badge background color
        badge.setBadgeTextColor(ContextCompat.getColor(this,R.color.white));// Customize the text color
        setBadgeNumber(getNotificationCount(getApplicationContext()));
        badge.setHorizontalOffset(50);// Adjust the vertical position of the badge
        badge.setBadgeGravity(BadgeDrawable.TOP_END);// Set the position of the badge relative to the icon

            messBadge = BadgeDrawable.create(this);
            messBadge.setBackgroundColor(ContextCompat.getColor(this,R.color.red)); // Customize the badge background color
            messBadge.setBadgeTextColor(ContextCompat.getColor(this,R.color.white));// Customize the text color
            setBadgeMessNumber(getMessNotiCount(getApplicationContext()));
            messBadge.setHorizontalOffset(50);// Adjust the vertical position of the badge
            messBadge.setBadgeGravity(BadgeDrawable.TOP_END);// Set the position of the badge relative to the icon

        myApp = (MyApplication) getApplication();
        webSocketClient = myApp.getWebSocketClient();
        menuModels = myApp.getMenuModels();
        employees = myApp.getEmployees();
        paymentMethods = myApp.getPaymentMethods();
        changeReservations = new ArrayList<>();
        event = new MessageEvent(this.getClass().getSimpleName());
        event.setChangeTables(new ArrayList<>());
        event.setReservationModels(new ArrayList<>());

        getMenu();
        getEmployees();
        getPaymentMethods();

        if (webSocketClient != null) {
            // WebSocket client is already created and connected
            // Use the same client for this activity
            webSocketClient.setWebSocketListener(this);
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
            webSocketClient.setWebSocketListener(this);
        }


        FirebaseAuth auth = FirebaseAuth.getInstance();
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if(Auth.User_Uid != null){
            setView();
        }else{
            Auth.User_Uid = getIntent().getStringExtra("user_uid");
            Auth.PhoneNumber = getIntent().getStringExtra("phoneNumber");
            if(Auth.User_Uid == null){
                Auth.User_Uid = auth.getUid();
            }
            setView();
            authSigined();

        }

        setUpViewPager();
        binding.viewPager.setUserInputEnabled(false);



        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        binding.viewPager.setCurrentItem(0);
                        break;
                    case R.id.action_menu:
                        binding.viewPager.setCurrentItem(1);
                        break;
                    case R.id.action_reservation:
                        binding.viewPager.setCurrentItem(2);
                        break;
                    case R.id.action_user:
                        binding.viewPager.setCurrentItem(3);
                        break;
                }
                return true;
            }
        });

        binding.navigationView.setNavigationItemSelectedListener(this);



    }

    private void registerLauncher(){
            launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if(o.getResultCode() == RESULT_OK){
                        Intent data = o.getData();
                        assert data != null;
                        String activity = data.getStringExtra("activity");
                        if(activity.equals(NotificationActivity.class.getSimpleName())){
                            //clear notifiy
                            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.cancelAll();
                        }

                        setBadgeNumber(getNotificationCount(getApplicationContext()));
                        setBadgeMessNumber(getMessNotiCount(getApplicationContext()));

                    }
                }
            });
    }

        private void getPaymentMethods() {
            if(paymentMethods == null) {
                paymentMethods = new ArrayList<>();
                ApiService.apiService.getAllPaymentMethod().enqueue(new Callback<ArrayList<PaymentMethodModel>>() {
                    @Override
                    public void onResponse(Call<ArrayList<PaymentMethodModel>> call, Response<ArrayList<PaymentMethodModel>> response) {
                        switch (response.code()){
                            case 500:
                                Toast.makeText(MainActivity.this, "Lỗi server", Toast.LENGTH_SHORT).show();
                                break;
                            default://response success
                                paymentMethods.addAll(response.body());
                                myApp.setPaymentMethods(paymentMethods);
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<PaymentMethodModel>> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Lỗi server", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        private void getEmployees() {
            if(employees == null){
                employees = new ArrayList<>();

                ApiService.apiService.getListEmployeesByPositionId(new PositionIdRequest("*"))
                        .enqueue(new Callback<ArrayList<Employee>>() {
                            @Override
                            public void onResponse(Call<ArrayList<Employee>> call, Response<ArrayList<Employee>> response) {
                                switch (response.code()){
                                    case 500:
                                        Toast.makeText(MainActivity.this, "Lỗi server", Toast.LENGTH_SHORT).show();
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

        }

        private void getMenu() {
            if(menuModels == null){
                menuModels = new ArrayList<>();
                firestore = FirebaseFirestore.getInstance();
                firestore.collection("menu").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for (DocumentSnapshot doc : task.getResult()){
                                        MenuModel model = doc.toObject(MenuModel.class);
                                        assert model != null;
                                        model.setDocumentId(doc.getId());
                                        menuModels.add(model);
                                    }
                                    myApp.setMenuModels(menuModels);
                                }
                            }
                        });
            }
        }

        private void setView() {
            UserUidRequest userUidRequest = new UserUidRequest(Auth.User_Uid);
            ApiService.apiService.getEmployee(userUidRequest).enqueue(new Callback<Employee>() {
                @Override
                public void onResponse(Call<Employee> call, Response<Employee> response) {
                    if(response.isSuccessful()){
                        Employee employee = response.body();
                        assert employee != null;
                        if(employee.getPosition_id().equals("QL")){
                            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                            binding.drawerLayout.addDrawerListener(toggle);
                            toggle.syncState();
                        }
                    }
                }
                @Override
                public void onFailure(Call<Employee> call, Throwable t) {
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    finish();
                }
            });


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
                        Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                        if(employee.getPosition_id().equals("QL")){
                            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                            binding.drawerLayout.addDrawerListener(toggle);
                            toggle.syncState();
                        }
                        MySharedPreferences.saveUserUid(getApplication(),Auth.User_Uid);
                        MySharedPreferences.savePhone(getApplication(),Auth.PhoneNumber);
                        Intent finishIntent = new Intent(LoginActivity.ACTION_FINISH_ACTIVITY);
                        sendBroadcast(finishIntent);
                    }
                }
                @Override
                public void onFailure(Call<Employee> call, Throwable t) {
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    finish();
                }
            });

    }

    private void setUpViewPager() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(MainActivity.this);
        binding.viewPager.setAdapter(viewPagerAdapter);

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        binding.toolbarTitle.setText("Đặt bàn");
                        binding.bottomNavigation.setSelectedItemId(R.id.action_home);
                        break;
                    case 1:
                        binding.toolbarTitle.setText("Thực đơn");
                        binding.bottomNavigation.setSelectedItemId(R.id.action_menu);
                        break;
                    case 2:
                        binding.toolbarTitle.setText("Bàn đã đặt");
                        binding.bottomNavigation.setSelectedItemId(R.id.action_reservation);
                        break;
                    case 3:
                        binding.toolbarTitle.setText("Hồ sơ");
                        binding.bottomNavigation.setSelectedItemId(R.id.action_user);
                        break;
                }
                super.onPageSelected(position);
            }
        });
    }


    @Override
    public void onBackPressed() {

        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

        @SuppressLint("UnsafeOptInUsageError")
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            int icon = menu.findItem(R.id.action_notification).getItemId();
            int mess = menu.findItem(R.id.action_message).getItemId();
            BadgeUtils.attachBadgeDrawable(badge,binding.toolbar,icon);
            BadgeUtils.attachBadgeDrawable(messBadge,binding.toolbar,mess);

            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();

            if (id == R.id.action_more) { // R.id.action_settings is the ID of your three-dot menu item
                View view = findViewById(R.id.action_more); // Provide the anchor view for the popup menu

                PopupMenu popupMenu = new PopupMenu(this, view, 0, 0, R.style.PopupMenuStyle);

                popupMenu.getMenuInflater().inflate(R.menu.menu_dropdown, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_logout:
                                startActivity(new Intent(MainActivity.this,KitchenMainActivity.class));
                                finish();

                                // Handle menu item 1 click
                                return true;
                        }
                        return false;
                    }
                });

                popupMenu.show();
                return true;
            }
            if(id == R.id.action_message){
                launcher.launch(new Intent(this,ConversationsActivity.class));
                return true;
            }


            if (id == R.id.action_notification) {
                launcher.launch(new Intent(this,NotificationActivity.class));

                // Handle the notification icon click here
                // You can open a notification activity or perform any desired action.
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int itemId = item.getItemId();
            switch (itemId) {
                case R.id.nav_quanLyMenu:
                    startActivity(new Intent(MainActivity.this,MenuManagementActivity.class));
                    break;
                case R.id.nav_quanLyNV:
                    startActivity(new Intent(MainActivity.this,StaffManagementActivity.class));
                    break;
                case R.id.nav_paidOrders:
                    startActivity(new Intent(MainActivity.this,PaidOrdersManagementActivity.class));
                    break;
                case R.id.nav_revenueStatistics:
                    startActivity(new Intent(MainActivity.this,RevenueStatisticsActivity.class));
                    break;
                case R.id.nav_customers:
                    startActivity(new Intent(MainActivity.this,CustomerManagementActivity.class));
                    break;

            }
            return true;
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
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
                    CharSequence contentText = null;
                    boolean ignore = false;
                    boolean isMessage = false;
                    if(!notificationModel.isFor_customer()){
                    if(notificationModel.getActivity() != null && notificationModel.getActivity().equals("Reservation")){
                        changeReservations.add(notificationModel.getReservation());

                        Util.changeReservationAdding(event.getReservationModels(),changeReservations);
                        changeReservations.clear();
                        event.setToActivity("Reservation");
                        event.setNotificationModel(notificationModel);
                        contentText = getMessageReservation(notificationModel,messageObject);
                        EventBus.getDefault().postSticky(event);
                    }
                    if(notificationModel.getActivity() != null && notificationModel.getActivity().equals("Message")){
                        isMessage = true;
                        EventBus.getDefault().post(notificationModel.getConversationMessage());
                        if(ignoreEvent != null && ignoreEvent.getConversation_id() == notificationModel.getConversationMessage().getConversation_id()){
                            ignore = true;
                        }
                        contentText = getMessageCustomer(notificationModel.getConversationMessage());
                    }
                    if( contentText == null){
                        contentText = getMessageEmployee(notificationModel,messageObject);
                    }
                    if(!notificationModel.getCreated_by().equals(Auth.User_Uid)){
                        if(!ignore){
                            if(isMessage){

                                popUpNotificationMesage(contentText,notificationModel);
                            }else {

                                popUpNotification(contentText,notificationModel);
                            }
                        }
                    }
                    if(messageObject.getUpdateTables() != null){
                        Util.changeTablesAdding(event.getChangeTables(),messageObject.getUpdateTables());
                        event.setToActivity(null);
                        EventBus.getDefault().postSticky(event);
                    }
                    setBadgeNumber(getNotificationCount(myApp));//set number notify badge
                    setBadgeMessNumber(getMessNotiCount(myApp));//set number mess badge
                }
                }
            });
        }

        private CharSequence getMessageReservation(NotificationModel notificationModel, NotificationModel.Message messageObject) {
            String created_by;
            created_by = notificationModel.getReservation().getCustomer_name().trim();
            return (CharSequence)TextUtils.concat(created_by, " ", messageObject.getStatus());

        }

        private void popUpNotificationMesage(CharSequence contentText, NotificationModel notificationModel) {
            Uri sound = Uri.parse("android.resource://" + getPackageName() + "/"+ R.raw.sound_notification);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, MyApplication.CHANNEL_MESSAGE)
                    .setSmallIcon(R.drawable.hp_res_logo)
                    .setSound(sound)
                    .setContentTitle("Tin nhắn")
                    .setContentText(contentText)
                    .setPriority(NotificationCompat.PRIORITY_MAX);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if(notificationManager != null){
                notificationManager.notify((int) notificationModel.getId(),builder.build());

            }
        }



        private void popUpNotification(CharSequence contentText, NotificationModel notificationModel) {
            Uri sound = Uri.parse("android.resource://" + getPackageName() + "/"+ R.raw.sound_notification);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, MyApplication.CHANNEL_ID)
                    .setSmallIcon(R.drawable.hp_res_logo)
                    .setSound(sound)
                    .setContentTitle("Thông báo")
                    .setContentText(contentText)
                    .setPriority(NotificationCompat.PRIORITY_MAX);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if(notificationManager != null){
                notificationManager.notify((int) notificationModel.getId(),builder.build());

            }
        }
        private CharSequence getMessageCustomer(MessageModel messageModel) {

            return (CharSequence)TextUtils.concat(messageModel.getFull_name(), ": ",messageModel.getContent() );
        }

        private CharSequence getMessageEmployee(NotificationModel notificationModel, NotificationModel.Message messageObject) {
            SpannableString created_by = null;
            for (Employee employee : employees){
                if(employee.getUser_uid().equals(notificationModel.getCreated_by())){
                    created_by = new SpannableString(employee.getFull_name());
                    created_by.setSpan(new StyleSpan(Typeface.BOLD), 0, employee.getFull_name().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                }
            }

            return (CharSequence)TextUtils.concat(created_by, " ", messageObject.getStatus());

        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            if(remote){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(myApp, "close", Toast.LENGTH_SHORT).show();
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
        // Method to update the badge count
        public static int getNotificationCount(Context context) {
            // Lấy NotificationManager
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            // Kiểm tra nếu NotificationManager không null
            if (notificationManager != null) {
                // Lấy danh sách các thông báo đang hoạt động
                int count = 0;
                StatusBarNotification[] activeNotifications = notificationManager.getActiveNotifications();
                for(StatusBarNotification sbn : activeNotifications){
                    if(!sbn.getNotification().getChannelId().equals(MyApplication.CHANNEL_MESSAGE)){
                        count++;
                    }
                }
                // Trả về số lượng thông báo
                return count;
            }

            // Trong trường hợp có lỗi hoặc NotificationManager không khả dụng
            return 0;
        }

        // Method to update the badge count
        public static int getMessNotiCount(Context context) {
            // Lấy NotificationManager
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            // Kiểm tra nếu NotificationManager không null
            if (notificationManager != null) {
                // Lấy danh sách các thông báo đang hoạt động
                int count = 0;
                StatusBarNotification[] activeNotifications = notificationManager.getActiveNotifications();
                for(StatusBarNotification sbn : activeNotifications){
                    if(sbn.getNotification().getChannelId().equals(MyApplication.CHANNEL_MESSAGE)){
                        count++;
                    }
                }
                // Trả về số lượng thông báo
                return count;
            }

            // Trong trường hợp có lỗi hoặc NotificationManager không khả dụng
            return 0;
        }

        private void setBadgeMessNumber(int number){
            if(number == 0){
                messBadge.setVisible(false);
            }else{
                messBadge.setVisible(true);
                messBadge.setNumber(number);
            }

        }

        private void setBadgeNumber(int number){
            if(number == 0){
                badge.setVisible(false);
            }else{
                badge.setVisible(true);
                badge.setNumber(number);
            }

        }


        @Override
        protected void onDestroy() {
            EventBus.getDefault().unregister(this);
            super.onDestroy();
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onMessageEvent(IgnoreEvent ignoreEvent) {
            if(ignoreEvent.getSource().equals(MessageActivity.class.getSimpleName())){
                if(ignoreEvent.isIgnore()){
                    this.ignoreEvent = ignoreEvent;
                }else{
                    this.ignoreEvent = null;
                }

            }

        }
    }