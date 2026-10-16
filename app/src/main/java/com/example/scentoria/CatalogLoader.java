package com.example.scentoria;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Reads the real-brand catalogue from assets.
 * Names, brands, gender and scent family come from the catalogue file.
 * Prices (by brand tier), longevity, projection, occasions and - unless listed in notes.txt - notes/accords
 * are ESTIMATES derived from brand tier, name keywords and scent family. They are flagged as such in the UI.
 */
public class CatalogLoader {

    private static final String[] CODES = {"FL", "WO", "FR", "AM", "GO", "AQ", "CI", "SP"};
    private static final String[] FAMILIES = {"Floral", "Woody", "Fresh", "Amber", "Gourmand", "Aquatic", "Citrus", "Spicy"};

    private static final int[] LO = {0, 2200, 3500, 5500, 8500, 12000, 16000};
    private static final int[] HI = {0, 4500, 6500, 9500, 14000, 19000, 25000};

    private static final String[][] TOP = {
            {"Bergamot", "Pink Pepper", "Peach", "Pear", "Mandarin", "Lychee", "Green Leaves"},
            {"Bergamot", "Cardamom", "Grapefruit", "Juniper", "Pepper", "Lemon"},
            {"Lemon", "Mint", "Green Apple", "Bergamot", "Grapefruit", "Lime"},
            {"Saffron", "Bergamot", "Pink Pepper", "Cardamom", "Mandarin"},
            {"Pear", "Pink Pepper", "Orange", "Caramel", "Black Currant", "Coconut"},
            {"Sea Notes", "Lime", "Bergamot", "Melon", "Lemon", "Mint"},
            {"Lemon", "Orange", "Mandarin", "Grapefruit", "Bergamot", "Yuzu", "Lime"},
            {"Pink Pepper", "Cardamom", "Ginger", "Saffron", "Black Pepper", "Nutmeg"}};
    private static final String[][] MIDDLE = {
            {"Rose", "Jasmine", "Peony", "Lily of the Valley", "Orange Blossom", "Tuberose", "Iris", "Magnolia"},
            {"Cedar", "Vetiver", "Iris", "Lavender", "Geranium", "Cypress"},
            {"Lavender", "Jasmine", "Sage", "Violet Leaf", "Neroli", "Basil"},
            {"Labdanum", "Rose", "Jasmine", "Incense", "Cinnamon", "Orchid"},
            {"Coffee", "Jasmine", "Almond", "Iris", "Cacao", "Heliotrope"},
            {"Marine Accord", "Jasmine", "Sage", "Lotus", "Rosemary", "Geranium"},
            {"Neroli", "Petitgrain", "Ginger", "Jasmine", "Green Tea", "Basil"},
            {"Cinnamon", "Clove", "Rose", "Geranium", "Cumin", "Lavender"}};
    private static final String[][] BASE = {
            {"Musk", "Sandalwood", "Vanilla", "Amber", "Cedar", "Patchouli"},
            {"Sandalwood", "Oakmoss", "Patchouli", "Ambroxan", "Leather", "Musk", "Oud"},
            {"White Musk", "Cedar", "Ambergris", "Vetiver", "Amber"},
            {"Amber", "Benzoin", "Vanilla", "Tonka Bean", "Patchouli", "Oud", "Musk"},
            {"Vanilla", "Praline", "Tonka Bean", "Patchouli", "Caramel", "Musk"},
            {"White Musk", "Driftwood", "Ambergris", "Cedar", "Oakmoss"},
            {"Musk", "Cedar", "Vetiver", "Amber", "Oakmoss"},
            {"Tobacco", "Leather", "Amber", "Vanilla", "Patchouli", "Cedar", "Oud"}};
    private static final String[][] ACCORD = {
            {"Floral", "Powdery", "Fresh", "Sweet", "Fruity"},
            {"Woody", "Earthy", "Aromatic", "Smoky", "Warm Spicy"},
            {"Fresh", "Citrus", "Green", "Aromatic", "Clean"},
            {"Amber", "Warm Spicy", "Sweet", "Balsamic", "Resinous"},
            {"Sweet", "Vanilla", "Gourmand", "Caramel", "Creamy"},
            {"Aquatic", "Marine", "Fresh", "Salty", "Mineral"},
            {"Citrus", "Fresh", "Zesty", "Aromatic", "Woody"},
            {"Warm Spicy", "Fresh Spicy", "Tobacco", "Leather", "Amber"}};
    private static final String[][] OCC = {
            {"Daily", "Date Night", "Office"}, {"Office", "Party", "Daily"}, {"Daily", "Summer", "Office"},
            {"Date Night", "Party", "Daily"}, {"Date Night", "Party", "Daily"}, {"Summer", "Daily", "Office"},
            {"Summer", "Daily", "Office"}, {"Party", "Date Night", "Office"}};
    private static final String[] ALL_OCC = {"Daily", "Office", "Date Night", "Party", "Summer"};
    private static final String[] LONG_TXT = {"", "", "3-5 hrs", "5-7 hrs", "8-10 hrs", "10-12 hrs"};
    private static final String[] PROJ_TXT = {"", "", "Soft", "Moderate", "Strong", "Very strong"};
    private static final String[] UP = {"parfum", "extrait", "elixir", "absolu", "intense", "oud", "extreme", "prive"};
    private static final String[] DOWN = {"cologne", "fraiche", "eau vive", "eau tendre", "sport", "aqua", "light", "l'eau"};

    public static List<Perfume> load(Context c) {
        List<Perfume> out = new ArrayList<>();
        Map<String, String[][]> notes = loadNotes(c);
        try (BufferedReader r = new BufferedReader(
                new InputStreamReader(c.getAssets().open("catalog.txt"), StandardCharsets.UTF_8))) {
            String line, brand = "";
            int tier = 3;
            while ((line = r.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                if (line.startsWith("@")) {
                    String[] b = line.substring(1).split("\\|");
                    brand = b[0].trim();
                    tier = Integer.parseInt(b[1].trim());
                    continue;
                }
                String[] f = line.split("\\|");
                if (f.length < 4) continue;
                out.add(build(out.size() + 1, brand, tier, f[0].trim(), Integer.parseInt(f[1].trim()),
                        f[2].trim(), f[3].trim(), notes));
            }
        } catch (IOException | NumberFormatException ignored) { }
        return out;
    }

    private static Map<String, String[][]> loadNotes(Context c) {
        Map<String, String[][]> m = new HashMap<>();
        try (BufferedReader r = new BufferedReader(
                new InputStreamReader(c.getAssets().open("notes.txt"), StandardCharsets.UTF_8))) {
            String line;
            while ((line = r.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] p = line.split("\\|");
                if (p.length < 5) continue;
                m.put((p[0] + "|" + p[1]).toLowerCase(), new String[][]{p[2].split(","), p[3].split(","), p[4].split(",")});
            }
        } catch (IOException ignored) { }
        return m;
    }

    private static String[] pick(Random r, String[] pool, int n) {
        List<String> l = new ArrayList<>(Arrays.asList(pool));
        Collections.shuffle(l, r);
        return l.subList(0, Math.min(n, l.size())).toArray(new String[0]);
    }

    private static int clamp(int v) { return Math.max(2, Math.min(5, v)); }

    private static Perfume build(int id, String brand, int tier, String name, int year,
                                 String g, String code, Map<String, String[][]> notes) {
        int fi = Arrays.asList(CODES).indexOf(code);
        if (fi < 0) fi = 0;
        String family = FAMILIES[fi];
        String gender = g.equals("W") ? "Women" : g.equals("M") ? "Men" : "Unisex";

        Random rnd = new Random((brand + "|" + name).hashCode() & 0x7fffffff);

        int price = LO[tier] + rnd.nextInt(HI[tier] - LO[tier] + 1);
        price = Math.min(PerfumeData.MAX_PRICE, Math.round(price / 100f) * 100);

        boolean heavy = fi == 1 || fi == 3 || fi == 4 || fi == 7;
        int lon = heavy ? 4 : 3, proj = (fi == 3 || fi == 4 || fi == 7) ? 4 : 3;
        String ln = name.toLowerCase();
        int adj = 0;
        for (String k : UP) if (ln.contains(k)) { adj = 1; break; }
        for (String k : DOWN) if (ln.contains(k)) { adj = -1; break; }
        lon = clamp(lon + adj);
        proj = clamp(proj + adj);

        Set<String> occ = new LinkedHashSet<>(Arrays.asList(pick(rnd, OCC[fi], 2)));
        if (rnd.nextInt(4) == 0) occ.add(ALL_OCC[rnd.nextInt(ALL_OCC.length)]);

        String[] top, mid, base;
        boolean approx;
        String[][] n = notes.get((brand + "|" + name).toLowerCase());
        if (n != null) {
            top = n[0]; mid = n[1]; base = n[2]; approx = false;
        } else {
            top = pick(rnd, TOP[fi], 3); mid = pick(rnd, MIDDLE[fi], 3); base = pick(rnd, BASE[fi], 3); approx = true;
        }

        String[] ac = pick(rnd, ACCORD[fi], 4);
        int[] pct = {100, 80 + rnd.nextInt(11), 62 + rnd.nextInt(11), 42 + rnd.nextInt(13)};
        String[] accords = new String[ac.length];
        for (int i = 0; i < ac.length; i++) accords[i] = ac[i] + ":" + pct[i];

        String desc = brand + "'s " + name + (year > 0 ? " (" + year + ")" : "") + ": a " + family.toLowerCase()
                + " fragrance " + (g.equals("W") ? "designed for women" : g.equals("M") ? "designed for men" : "for everyone") + ".";

        Perfume p = new Perfume(id, name, brand, gender, year, price, 0.0, family, occ.toArray(new String[0]),
                lon, LONG_TXT[lon], proj, PROJ_TXT[proj], PerfumeData.colorOf(family), top, mid, base, accords, desc);
        p.notesApprox = approx;
        return p;
    }
}
