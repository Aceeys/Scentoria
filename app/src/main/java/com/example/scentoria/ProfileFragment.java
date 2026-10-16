package com.example.scentoria;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.List;
import java.util.Locale;
import java.util.Set;

/** Feature 08 User Profile: manage name, favorites, ratings and reviews. */
public class ProfileFragment extends Fragment {

    private TextView tvAvatar, statFav, statRev, statAvg, tvPrefs;
    private EditText etName;
    private LinearLayout myReviews;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle b) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        Context ctx = requireContext();
        tvAvatar = v.findViewById(R.id.tvAvatar);
        statFav = v.findViewById(R.id.statFav);
        statRev = v.findViewById(R.id.statRev);
        statAvg = v.findViewById(R.id.statAvg);
        tvPrefs = v.findViewById(R.id.tvPrefs);
        etName = v.findViewById(R.id.etName);
        myReviews = v.findViewById(R.id.myReviews);

        etName.setText(UserPrefs.getName(ctx));
        v.findViewById(R.id.btnSaveName).setOnClickListener(x -> {
            String n = etName.getText().toString().trim();
            if (n.isEmpty()) n = "Guest";
            UserPrefs.setName(ctx, n);
            Toast.makeText(ctx, "Name saved", Toast.LENGTH_SHORT).show();
            refresh();
        });
        ((Button) v.findViewById(R.id.btnClear)).setOnClickListener(x -> {
            Prefs.sp(ctx).edit().clear().apply();
            etName.setText("Guest");
            Toast.makeText(ctx, "All data cleared", Toast.LENGTH_SHORT).show();
            refresh();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        Context ctx = requireContext();
        String name = UserPrefs.getName(ctx);

        GradientDrawable circle = new GradientDrawable();
        circle.setShape(GradientDrawable.OVAL);
        circle.setColor(ContextCompat.getColor(ctx, R.color.primary));
        tvAvatar.setBackground(circle);
        tvAvatar.setText(name.substring(0, 1).toUpperCase(Locale.ROOT));

        List<Review> reviews = ReviewStore.all(ctx);
        statFav.setText(String.valueOf(Favorites.all(ctx).size()));
        statRev.setText(String.valueOf(reviews.size()));
        float sum = 0;
        for (Review r : reviews) sum += r.stars;
        statAvg.setText(reviews.isEmpty() ? "-" : String.format(Locale.US, "%.1f", sum / reviews.size()));

        Set<String> fams = UserPrefs.getSet(ctx, "pref_family");
        Set<String> occs = UserPrefs.getSet(ctx, "pref_occasion");
        tvPrefs.setText(fams.isEmpty() && occs.isEmpty()
                ? "None yet. Set them in the For You tab."
                : "Families: " + (fams.isEmpty() ? "any" : String.join(", ", fams))
                + "\nOccasions: " + (occs.isEmpty() ? "any" : String.join(", ", occs))
                + "\nBudget: up to " + Fmt.peso(UserPrefs.getBudget(ctx)));

        myReviews.removeAllViews();
        if (reviews.isEmpty()) {
            TextView t = new TextView(ctx);
            t.setText("You haven't reviewed any fragrance yet.");
            t.setTextColor(ContextCompat.getColor(ctx, R.color.text_muted));
            myReviews.addView(t);
        }
        float d = getResources().getDisplayMetrics().density;
        for (Review r : reviews) {
            Perfume p = PerfumeData.byId(r.perfumeId);
            if (p == null) continue;

            LinearLayout card = new LinearLayout(ctx);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setBackgroundResource(R.drawable.bg_review);
            card.setPadding((int) (12 * d), (int) (10 * d), (int) (12 * d), (int) (10 * d));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.bottomMargin = (int) (8 * d);
            card.setLayoutParams(lp);

            TextView title = new TextView(ctx);
            title.setText(p.brand + " - " + p.name + "   " + Fmt.stars(r.stars));
            title.setTextColor(ContextCompat.getColor(ctx, R.color.text_dark));
            title.setTypeface(null, android.graphics.Typeface.BOLD);
            card.addView(title);

            if (!r.text.isEmpty()) {
                TextView body = new TextView(ctx);
                body.setText(r.text);
                body.setTextColor(ContextCompat.getColor(ctx, R.color.text_dark));
                card.addView(body);
            }

            TextView del = new TextView(ctx);
            del.setText("Delete");
            del.setTextColor(ContextCompat.getColor(ctx, R.color.primary));
            del.setPadding(0, (int) (6 * d), 0, 0);
            final long t = r.time;
            del.setOnClickListener(x -> { ReviewStore.delete(ctx, t); refresh(); });
            card.addView(del);

            card.setOnClickListener(x -> DetailActivity.open(ctx, p.id));
            myReviews.addView(card);
        }
    }
}
