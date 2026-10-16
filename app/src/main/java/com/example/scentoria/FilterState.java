package com.example.scentoria;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Holds every active filter: scent family, brand, occasion, price range and sort order. */
public class FilterState {
    public final Set<String> families = new HashSet<>();
    public final Set<String> brands = new HashSet<>();
    public final Set<String> occasions = new HashSet<>();
    public int minPrice = 0;
    public int maxPrice = PerfumeData.MAX_PRICE;
    public String sort = "Top rated";

    public static final String[] SORTS = {"Top rated", "Price: low to high", "Price: high to low", "Name A-Z"};

    public boolean priceActive() { return minPrice > 0 || maxPrice < PerfumeData.MAX_PRICE; }

    public int activeCount() {
        int n = 0;
        if (!families.isEmpty()) n++;
        if (!brands.isEmpty()) n++;
        if (!occasions.isEmpty()) n++;
        if (priceActive()) n++;
        return n;
    }

    public void reset() {
        families.clear(); brands.clear(); occasions.clear();
        minPrice = 0; maxPrice = PerfumeData.MAX_PRICE; sort = SORTS[0];
    }

    public boolean matches(Perfume p) {
        if (!families.isEmpty() && !families.contains(p.family)) return false;
        if (!brands.isEmpty() && !brands.contains(p.brand)) return false;
        if (!occasions.isEmpty()) {
            boolean ok = false;
            for (String o : p.occasions) if (occasions.contains(o)) ok = true;
            if (!ok) return false;
        }
        return p.price >= minPrice && p.price <= maxPrice;
    }

    public void sort(Map<Integer, Double> ratings, List<Perfume> list) {
        switch (sort) {
            case "Price: low to high":
                Collections.sort(list, (x, y) -> Integer.compare(x.price, y.price)); break;
            case "Price: high to low":
                Collections.sort(list, (x, y) -> Integer.compare(y.price, x.price)); break;
            case "Name A-Z":
                Collections.sort(list, (x, y) -> x.name.compareToIgnoreCase(y.name)); break;
            default:
                Collections.sort(list, (x, y) -> {
                    double rx = ratings.containsKey(x.id) ? ratings.get(x.id) : 0;
                    double ry = ratings.containsKey(y.id) ? ratings.get(y.id) : 0;
                    int cmp = Double.compare(ry, rx);
                    return cmp != 0 ? cmp : x.name.compareToIgnoreCase(y.name);
                });
        }
    }
}
