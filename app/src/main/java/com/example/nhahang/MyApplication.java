package com.example.nhahang;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.nhahang.Interfaces.IUpdateTablesListener;
import com.example.nhahang.Interfaces.WebSocketListener;
import com.example.nhahang.Models.Employee;
import com.example.nhahang.Models.MenuModel;
import com.example.nhahang.Models.OrderModel;
import com.example.nhahang.Models.PaymentMethodModel;
import com.example.nhahang.Utils.MyWebSocketClient;
import com.example.nhahang.ViewModels.WebSocketViewModel;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import org.java_websocket.handshake.ServerHandshake;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application{
    public static final String CHANNEL_ID = "CHANNEL 1";
    private MyWebSocketClient webSocketClient;
    private List<MenuModel> menuModels;
    private List<Employee> employees;
    private List<PaymentMethodModel> paymentMethods;
    private IUpdateTablesListener iUpdateTablesListener;


    @Override
    public void onCreate() {
        super.onCreate();
        // Khởi tạo FirebaseApp
        FirebaseApp.initializeApp(this);
        createNotificationChannel();
    }

    public IUpdateTablesListener getiUpdateTablesListener() {
        return iUpdateTablesListener;
    }

    public void setiUpdateTablesListener(IUpdateTablesListener iUpdateTablesListener) {
        this.iUpdateTablesListener = iUpdateTablesListener;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this.
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public List<PaymentMethodModel> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(List<PaymentMethodModel> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public List<MenuModel> getMenuModels() {
        return menuModels;
    }

    public void setMenuModels(List<MenuModel> menuModels) {
        this.menuModels = menuModels;
    }

    public MyWebSocketClient getWebSocketClient() {
        return webSocketClient;
    }

    public void setWebSocketClient(MyWebSocketClient client) {
        webSocketClient = client;
    }


}
