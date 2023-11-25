package com.example.nhahang.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {
    private static final String PREFS_NAME = "my_prefs";

    public static void saveUserUid(Context context, String user_uid) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString("user_uid", user_uid);
        editor.apply();
    }

    public static String getUserUid(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getString("user_uid", null);
    }

    public static void savePhone(Context context, String phone) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString("phone", phone);
        editor.apply();
    }

    public static String getPhone(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getString("phone", null);
    }


}
