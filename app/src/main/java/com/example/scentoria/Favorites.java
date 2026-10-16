package com.example.scentoria;

import android.content.Context;
import java.util.HashSet;
import java.util.Set;

public class Favorites {
    private static final String KEY = "favs";

    public static Set<String> all(Context c) {
        return new HashSet<>(Prefs.sp(c).getStringSet(KEY, new HashSet<>()));
    }

    public static boolean isFav(Context c, int id) {
        return all(c).contains(String.valueOf(id));
    }

    public static boolean toggle(Context c, int id) {
        Set<String> s = all(c);
        String k = String.valueOf(id);
        boolean now;
        if (s.contains(k)) { s.remove(k); now = false; } else { s.add(k); now = true; }
        Prefs.sp(c).edit().putStringSet(KEY, s).apply();
        return now;
    }
}
