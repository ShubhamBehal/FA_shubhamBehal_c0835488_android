package com.example.fa_shubhambehal_c0835488_android;

import android.content.Context;

import androidx.annotation.NonNull;

public class SharedPrefsUtil {
    private static SharedPrefsUtil instance;
    public static final String PREFS_NAME = "default_preferences";
    public static final String IS_FIRST_LAUNCH = "is_first_launch";

    private SharedPrefsUtil() {
    }

    public synchronized static SharedPrefsUtil getInstance() {
        if (instance == null) {
            instance = new SharedPrefsUtil();
        }
        return instance;
    }

    public boolean isAlreadyLaunched(@NonNull Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(IS_FIRST_LAUNCH, false);
    }

    public void setAlreadyLaunched(@NonNull Context context, boolean value) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit().putBoolean(IS_FIRST_LAUNCH, value).apply();
    }
}