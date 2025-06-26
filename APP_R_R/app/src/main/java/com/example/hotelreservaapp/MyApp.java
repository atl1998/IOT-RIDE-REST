package com.example.hotelreservaapp;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MyAppDebug", "MyApp.onCreate() ejecutado");

        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }
    }
}