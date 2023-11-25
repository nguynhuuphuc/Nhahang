package com.example.nhahang.Utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.IBinder;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.nhahang.MainActivity;
import com.example.nhahang.Models.Employee;
import com.example.nhahang.Models.NotificationModel;
import com.example.nhahang.MyApplication;
import com.example.nhahang.R;
import com.google.gson.Gson;

public class MyService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            String message = intent.getStringExtra("message");
            NotificationModel notificationModel = new Gson().fromJson(message,NotificationModel.class);
            CharSequence contentText = intent.getCharSequenceExtra("contentText");
            popUpNotification(contentText,notificationModel);
        }catch (Exception ignored){}

        startForeground(1,new Notification());
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    private void popUpNotification(CharSequence contentText, NotificationModel notificationModel) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
                .setSmallIcon(R.drawable.hp_res_logo)
                .setContentTitle("Thông báo")
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager != null){
            notificationManager.notify((int) notificationModel.getId(),builder.build());
        }

    }
}
