package com.example.scentoria;

public class Review {
    public final int perfumeId;
    public final float stars;
    public final String text, author;
    public final long time;

    public Review(int perfumeId, float stars, String text, String author, long time) {
        this.perfumeId = perfumeId; this.stars = stars; this.text = text;
        this.author = author; this.time = time;
    }
}
