package com.example.scentoria;

import android.content.Context;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/** Catalogue access. Data comes from assets/catalog.txt and assets/notes.txt (see CatalogLoader). */
public class PerfumeData {

    public static final int MAX_PRICE = 25000;

    private static List<Perfume> cache = new ArrayList<>();
    private static final Map<Integer, Perfume> index = new HashMap<>();

    public static synchronized void init(Context c) {
        if (!cache.isEmpty()) return;
        cache = CatalogLoader.load(c.getApplicationContext());
        for (Perfume p : cache) index.put(p.id, p);
    }

    public static List<Perfume> getAll() { return cache; }

    public static Perfume byId(int id) { return index.get(id); }

    public static List<String> families() {
        TreeSet<String> s = new TreeSet<>();
        for (Perfume p : cache) s.add(p.family);
        return new ArrayList<>(s);
    }

    public static List<String> brands() {
        TreeSet<String> s = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (Perfume p : cache) s.add(p.brand);
        return new ArrayList<>(s);
    }

    public static List<String> occasions() {
        TreeSet<String> s = new TreeSet<>();
        for (Perfume p : cache) for (String o : p.occasions) s.add(o);
        return new ArrayList<>(s);
    }

    /** Soft, boutique-style color per scent family. */
    public static int colorOf(String family) {
        switch (family) {
            case "Floral": return Color.parseColor("#C97B93");
            case "Woody": return Color.parseColor("#8A6A4F");
            case "Fresh": return Color.parseColor("#6E9C94");
            case "Amber": return Color.parseColor("#B8823A");
            case "Gourmand": return Color.parseColor("#A8584F");
            case "Aquatic": return Color.parseColor("#5C8FB5");
            case "Citrus": return Color.parseColor("#C2902A");
            case "Spicy": return Color.parseColor("#9C4A3A");
            default: return Color.parseColor("#7A6A72");
        }
    }
}
