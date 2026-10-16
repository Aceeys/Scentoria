package com.example.scentoria;

public class Perfume {
    public final int id, year, price, longevity, projection, color;
    public final String name, brand, gender, family, longevityText, projectionText, description;
    public final double rating;
    public final String[] occasions, top, middle, base, accords; // accords = "Name:percent"

    public Perfume(int id, String name, String brand, String gender, int year, int price,
                   double rating, String family, String[] occasions,
                   int longevity, String longevityText, int projection, String projectionText,
                   int color, String[] top, String[] middle, String[] base,
                   String[] accords, String description) {
        this.id = id; this.name = name; this.brand = brand; this.gender = gender;
        this.year = year; this.price = price; this.rating = rating; this.family = family;
        this.occasions = occasions; this.longevity = longevity; this.longevityText = longevityText;
        this.projection = projection; this.projectionText = projectionText; this.color = color;
        this.top = top; this.middle = middle; this.base = base;
        this.accords = accords; this.description = description;
    }

    /** Optional web image URL. A file in assets/perfumes/<id>.jpg takes priority. */
    public String imageUrl = "";

    /** true when notes/accords are typical for the scent family rather than the product's own notes. */
    public boolean notesApprox = false;

    private String searchCache;

    public String searchText() {
        if (searchCache != null) return searchCache;
        searchCache = (name + " " + brand + " " + family + " " + gender + " "
                + String.join(" ", top) + " " + String.join(" ", middle) + " "
                + String.join(" ", base)).toLowerCase(java.util.Locale.ROOT);
        return searchCache;
    }
}
