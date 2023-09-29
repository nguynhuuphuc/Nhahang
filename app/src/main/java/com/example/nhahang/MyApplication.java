package com.example.nhahang;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Khởi tạo FirebaseApp
        FirebaseApp.initializeApp(this);

    }
}
