package com.example.scentoria;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.Locale;

/** Feature 02 Fragrance Information + Feature 03 Ratings & Reviews + save to Favorites. */
public class DetailActivity extends AppCompatActivity {

    private Perfume p;
    private Button btnFav;
    private TextView dRating;
    private LinearLayout reviewsContainer;
    private RatingBar ratingBar;
    private EditText etReview;

    public static void open(Context c, int id) {
        Intent i = new Intent(c, DetailActivity.class);
        i.putExtra("id", id);
        c.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        p = PerfumeData.byId(getIntent().getIntExtra("id", -1));
        if (p == null) { finish(); return; }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(p.name);
        }

        android.graphics.drawable.GradientDrawable grad = new android.graphics.drawable.GradientDrawable(
                android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{p.color, ContextCompat.getColor(this, R.color.primary_dark)});
        findViewById(R.id.header).setBackground(grad);
        PerfumeImages.loadBottle(this, p, (android.widget.ImageView) findViewById(R.id.ivBottle));
        ((TextView) findViewById(R.id.dName)).setText(p.name);
        ((TextView) findViewById(R.id.dBrand)).setText(p.brand);
        ((TextView) findViewById(R.id.dMeta)).setText(Fmt.meta(p));
        ((TextView) findViewById(R.id.dPrice)).setText(Fmt.price(p) + "  (est. price)");
        ((TextView) findViewById(R.id.dNotesNote)).setText(p.notesApprox
                ? "Notes and accords are typical for this scent family (approximate)."
                : "Notes as commonly listed for this fragrance; accords are approximate.");
        ((TextView) findViewById(R.id.dDesc)).setText(p.description);
        ((TextView) findViewById(R.id.dTop)).setText(String.join(", ", p.top));
        ((TextView) findViewById(R.id.dMiddle)).setText(String.join(", ", p.middle));
        ((TextView) findViewById(R.id.dBase)).setText(String.join(", ", p.base));
        ((TextView) findViewById(R.id.dOccasion)).setText(String.join(", ", p.occasions));
        dRating = findViewById(R.id.dRating);

        LinearLayout stats = findViewById(R.id.statsContainer);
        int primary = ContextCompat.getColor(this, R.color.primary);
        addBar(stats, "Longevity \u00B7 " + p.longevityText, p.longevity * 20, primary);
        addBar(stats, "Projection \u00B7 " + p.projectionText, p.projection * 20, primary);

        LinearLayout accords = findViewById(R.id.accordsContainer);
        for (String acc : p.accords) {
            String[] parts = acc.split(":");
            addBar(accords, parts[0], Integer.parseInt(parts[1]), p.color);
        }

        btnFav = findViewById(R.id.btnFav);
        updateFavButton();
        btnFav.setOnClickListener(v -> { Favorites.toggle(this, p.id); updateFavButton(); });

        reviewsContainer = findViewById(R.id.reviewsContainer);
        ratingBar = findViewById(R.id.ratingBar);
        etReview = findViewById(R.id.etReview);
        findViewById(R.id.btnSubmit).setOnClickListener(v -> submitReview());
        refreshReviews();
    }

    private void updateFavButton() {
        btnFav.setText(Favorites.isFav(this, p.id) ? R.string.remove_fav : R.string.add_fav);
    }

    private void submitReview() {
        float stars = ratingBar.getRating();
        if (stars < 1) {
            Toast.makeText(this, "Please choose a star rating first", Toast.LENGTH_SHORT).show();
            return;
        }
        ReviewStore.add(this, new Review(p.id, stars, etReview.getText().toString().trim(),
                UserPrefs.getName(this), System.currentTimeMillis()));
        etReview.setText("");
        ratingBar.setRating(0);
        Toast.makeText(this, "Review saved", Toast.LENGTH_SHORT).show();
        refreshReviews();
    }

    private void refreshReviews() {
        List<Review> list = ReviewStore.forPerfume(this, p.id);
        dRating.setText(list.isEmpty() ? "\u2606 No ratings yet"
                : String.format(Locale.US, "\u2605 %.1f  (%d review%s)",
                ReviewStore.rating(this, p), list.size(), list.size() == 1 ? "" : "s"));

        reviewsContainer.removeAllViews();
        float d = getResources().getDisplayMetrics().density;
        if (list.isEmpty()) {
            TextView t = new TextView(this);
            t.setText("No reviews yet. Be the first to share your experience!");
            t.setTextColor(ContextCompat.getColor(this, R.color.text_muted));
            reviewsContainer.addView(t);
            return;
        }
        for (Review r : list) {
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setBackgroundResource(R.drawable.bg_review);
            card.setPadding((int) (12 * d), (int) (10 * d), (int) (12 * d), (int) (10 * d));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.bottomMargin = (int) (8 * d);
            card.setLayoutParams(lp);

            TextView head = new TextView(this);
            head.setText(r.author + "   " + Fmt.stars(r.stars));
            head.setTextColor(ContextCompat.getColor(this, R.color.gold));
            head.setTypeface(null, android.graphics.Typeface.BOLD);
            card.addView(head);

            if (!r.text.isEmpty()) {
                TextView body = new TextView(this);
                body.setText(r.text);
                body.setTextColor(ContextCompat.getColor(this, R.color.text_dark));
                card.addView(body);
            }
            reviewsContainer.addView(card);
        }
    }

    /** Horizontal bar: filled part = pct %. */
    private void addBar(LinearLayout container, String label, int pct, int color) {
        float d = getResources().getDisplayMetrics().density;
        TextView bar = new TextView(this);
        bar.setText(label);
        bar.setTextColor(0xFFFFFFFF);
        bar.setTextSize(13);
        bar.setSingleLine(true);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setPadding((int) (12 * d), 0, (int) (8 * d), 0);
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(8 * d);
        bg.setColor(color);
        bar.setBackground(bg);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setWeightSum(100f);
        LinearLayout.LayoutParams rowLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, (int) (34 * d));
        rowLp.bottomMargin = (int) (6 * d);
        row.setLayoutParams(rowLp);
        row.addView(bar, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, pct));
        if (pct < 100) row.addView(new View(this), new LinearLayout.LayoutParams(0, 1, 100 - pct));
        container.addView(row);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
