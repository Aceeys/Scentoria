package com.example.scentoria;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
    public static SharedPreferences sp(Context c) {
        return c.getSharedPreferences("scentoria_prefs", Context.MODE_PRIVATE);
    }
}
