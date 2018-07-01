package com.droidbyme.wallpaperservice;

import android.app.Application;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DatabaseHandler handler = new DatabaseHandler(getApplicationContext());
        try {
            handler.createDataBase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
