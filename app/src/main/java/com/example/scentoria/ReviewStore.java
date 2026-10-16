package com.example.scentoria;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/** Stores the user's ratings & reviews locally as JSON in SharedPreferences. */
public class ReviewStore {
    private static final String KEY = "reviews";

    public static List<Review> all(Context c) {
        List<Review> out = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(Prefs.sp(c).getString(KEY, "[]"));
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                out.add(new Review(o.getInt("pid"), (float) o.getDouble("stars"),
                        o.getString("text"), o.getString("author"), o.getLong("time")));
            }
        } catch (Exception ignored) { }
        return out;
    }

    private static void save(Context c, List<Review> list) {
        try {
            JSONArray arr = new JSONArray();
            for (Review r : list) {
                JSONObject o = new JSONObject();
                o.put("pid", r.perfumeId);
                o.put("stars", r.stars);
                o.put("text", r.text);
                o.put("author", r.author);
                o.put("time", r.time);
                arr.put(o);
            }
            Prefs.sp(c).edit().putString(KEY, arr.toString()).apply();
        } catch (Exception ignored) { }
    }

    public static List<Review> forPerfume(Context c, int id) {
        List<Review> out = new ArrayList<>();
        for (Review r : all(c)) if (r.perfumeId == id) out.add(r);
        return out;
    }

    /** One review per perfume: a new one replaces the old one. */
    public static void add(Context c, Review r) {
        List<Review> list = all(c);
        for (int i = list.size() - 1; i >= 0; i--)
            if (list.get(i).perfumeId == r.perfumeId) list.remove(i);
        list.add(0, r);
        save(c, list);
    }

    public static void delete(Context c, long time) {
        List<Review> list = all(c);
        for (int i = list.size() - 1; i >= 0; i--)
            if (list.get(i).time == time) list.remove(i);
        save(c, list);
    }

    /** Average of the user's own ratings for this perfume, or 0 when nobody has rated it yet. */
    public static double rating(Context c, Perfume p) {
        List<Review> l = forPerfume(c, p.id);
        if (l.isEmpty()) return 0;
        double s = 0;
        for (Review r : l) s += r.stars;
        return s / l.size();
    }

    /** perfumeId -> average stars, for perfumes that have at least one review (one JSON parse). */
    public static Map<Integer, Double> ratingMap(Context c) {
        Map<Integer, double[]> acc = new HashMap<>();
        for (Review r : all(c)) {
            double[] a = acc.get(r.perfumeId);
            if (a == null) { a = new double[2]; acc.put(r.perfumeId, a); }
            a[0] += r.stars; a[1]++;
        }
        Map<Integer, Double> out = new HashMap<>();
        for (Map.Entry<Integer, double[]> e : acc.entrySet()) out.put(e.getKey(), e.getValue()[0] / e.getValue()[1]);
        return out;
    }
}
