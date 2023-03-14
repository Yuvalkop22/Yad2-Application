package com.example.yad2application;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    public static Context context;
    public static Context getMyContext(){
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}
