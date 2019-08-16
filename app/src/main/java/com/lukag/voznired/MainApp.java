package com.lukag.voznired;

import android.app.Application;

import com.facebook.stetho.Stetho;

public class MainApp extends Application {
    private static final String TAG = MainApp.class.getSimpleName();

    public MainApp() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}