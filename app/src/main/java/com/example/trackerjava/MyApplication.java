package com.example.trackerjava;

import android.app.Application;
import android.content.Context;


import java.lang.ref.WeakReference;

public class MyApplication extends Application {

    private static WeakReference<Context> appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = new WeakReference<>(getApplicationContext());
    }

    public static Context getAppContext(){

        return appContext.get();
    }
}

