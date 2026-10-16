package com.example.scentoria;

import android.content.Context;
import java.util.HashSet;
import java.util.Set;

public class UserPrefs {
    public static String getName(Context c) { return Prefs.sp(c).getString("name", "Guest"); }
    public static void setName(Context c, String n) { Prefs.sp(c).edit().putString("name", n).apply(); }

    public static Set<String> getSet(Context c, String key) {
        return new HashSet<>(Prefs.sp(c).getStringSet(key, new HashSet<>()));
    }
    public static void putSet(Context c, String key, Set<String> v) {
        Prefs.sp(c).edit().putStringSet(key, v).apply();
    }

    public static int getBudget(Context c) { return Prefs.sp(c).getInt("budget", 10000); }
    public static void setBudget(Context c, int b) { Prefs.sp(c).edit().putInt("budget", b).apply(); }
}
