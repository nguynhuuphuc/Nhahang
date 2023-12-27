package com.example.nhahang;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.AudioAttributes;
import android.net.Uri;
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
    public static final String CHANNEL_ID_2 = "CHANNEL 2";
    public static final String CHANNEL_MESSAGE = "CHANNEL MESSAGE";
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
        createNotificationChannel2();
    }

    public IUpdateTablesListener getiUpdateTablesListener() {
        return iUpdateTablesListener;
    }

    public void setiUpdateTablesListener(IUpdateTablesListener iUpdateTablesListener) {
        this.iUpdateTablesListener = iUpdateTablesListener;
    }

    private void createNotificationChannel2() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        CharSequence name = getString(R.string.channel_name2);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID_2, name, importance);
        channel.setDescription(description);
        channel.enableVibration(true);
        channel.setVibrationPattern(new long[]{100,100,200,340});
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this.
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);


        Uri sound = Uri.parse("android.resource://" + getPackageName() + "/"+ R.raw.sound_notification);
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();
        CharSequence name2 = getString(R.string.channel_message);
        String description2 = getString(R.string.channel_description);
        int importance2 = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel2 = new NotificationChannel(CHANNEL_MESSAGE, name2, importance2);
        channel2.setDescription(description2);
        channel2.enableVibration(true);
        channel2.setVibrationPattern(new long[]{100,100,200,340});
        channel2.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        channel2.setSound(sound,audioAttributes);

        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this.
        notificationManager.createNotificationChannel(channel2);
    }

    private void createNotificationChannel() {
        Uri sound = Uri.parse("android.resource://" + getPackageName() + "/"+ R.raw.sound_notification);
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        channel.enableVibration(true);
        channel.setVibrationPattern(new long[]{100,100,200,340});
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        channel.setSound(sound,audioAttributes);

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
