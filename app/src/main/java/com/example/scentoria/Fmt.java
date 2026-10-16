package com.example.scentoria;

import java.text.NumberFormat;
import java.util.Locale;

public class Fmt {
    public static String peso(int n) {
        return "\u20B1" + NumberFormat.getIntegerInstance(Locale.US).format(n);
    }

    public static String stars(float s) {
        StringBuilder sb = new StringBuilder();
        int r = Math.round(s);
        for (int i = 0; i < 5; i++) sb.append(i < r ? "\u2605" : "\u2606");
        return sb.toString();
    }

    /** Estimated shelf price, shown with a ~ to make clear it is approximate. */
    public static String price(Perfume p) { return "~" + peso(p.price); }

    public static String meta(Perfume p) {
        return p.family + "  \u2022  " + p.gender + (p.year > 0 ? "  \u2022  " + p.year : "");
    }
}
