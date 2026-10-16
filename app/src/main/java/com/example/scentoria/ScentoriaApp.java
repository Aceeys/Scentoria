package com.example.scentoria;

import android.app.Application;

public class ScentoriaApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PerfumeData.init(this); // loads assets/catalog.txt once
    }
}
