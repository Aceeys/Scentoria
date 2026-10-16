package com.example.scentoria;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.List;
import java.util.Locale;

/** Feature 06 Fragrance Comparison: two perfumes side by side. */
public class CompareFragment extends Fragment {

    private Spinner spA, spB;
    private TableLayout table;
    private final List<Perfume> all = PerfumeData.getAll();
    private int rowIndex;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle b) {
        return inflater.inflate(R.layout.fragment_compare, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        spA = v.findViewById(R.id.spA);
        spB = v.findViewById(R.id.spB);
        table = v.findViewById(R.id.table);

        String[] names = new String[all.size()];
        for (int i = 0; i < all.size(); i++) names[i] = all.get(i).brand + " - " + all.get(i).name;
        ArrayAdapter<String> ad = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, names);
        spA.setAdapter(ad);
        spB.setAdapter(ad);
        spB.setSelection(1);

        AdapterView.OnItemSelectedListener l = new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View view, int pos, long id) { render(); }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        };
        spA.setOnItemSelectedListener(l);
        spB.setOnItemSelectedListener(l);
        render();
    }

    private void render() {
        Context ctx = requireContext();
        Perfume a = all.get(spA.getSelectedItemPosition());
        Perfume b = all.get(spB.getSelectedItemPosition());
        table.removeAllViews();
        rowIndex = 0;

        addRow("", a.name, b.name, 0, true);
        addRow("Brand", a.brand, b.brand, 0, false);
        addRow("Scent family", a.family, b.family, 0, false);
        addRow("For", a.gender, b.gender, 0, false);
        addRow("Year", a.year > 0 ? String.valueOf(a.year) : "-", b.year > 0 ? String.valueOf(b.year) : "-", 0, false);
        addRow("Est. price", Fmt.price(a), Fmt.price(b), Integer.compare(b.price, a.price), false);
        double ra = ReviewStore.rating(ctx, a), rb = ReviewStore.rating(ctx, b);
        addRow("Rating", ra > 0 ? String.format(Locale.US, "\u2605 %.1f", ra) : "-",
                rb > 0 ? String.format(Locale.US, "\u2605 %.1f", rb) : "-",
                (ra > 0 && rb > 0) ? Double.compare(ra, rb) : 0, false);
        addRow("Longevity (est.)", a.longevityText, b.longevityText, Integer.compare(a.longevity, b.longevity), false);
        addRow("Projection (est.)", a.projectionText, b.projectionText, Integer.compare(a.projection, b.projection), false);
        addRow("Occasions", String.join(", ", a.occasions), String.join(", ", b.occasions), 0, false);
        addRow("Top notes", String.join(", ", a.top), String.join(", ", b.top), 0, false);
        addRow("Heart notes", String.join(", ", a.middle), String.join(", ", b.middle), 0, false);
        addRow("Base notes", String.join(", ", a.base), String.join(", ", b.base), 0, false);
    }

    /** better: 1 = A wins, -1 = B wins, 0 = no highlight. */
    private void addRow(String label, String va, String vb, int better, boolean header) {
        Context ctx = requireContext();
        TableRow row = new TableRow(ctx);
        row.setBackgroundColor(header ? ContextCompat.getColor(ctx, R.color.primary)
                : (rowIndex % 2 == 0 ? ContextCompat.getColor(ctx, R.color.card) : Color.TRANSPARENT));
        rowIndex++;
        row.addView(cell(label, false, false, header, 0.8f));
        row.addView(cell(va, header, better > 0, header, 1f));
        row.addView(cell(vb, header, better < 0, header, 1f));
        table.addView(row);
    }

    private TextView cell(String text, boolean bold, boolean highlight, boolean header, float weight) {
        Context ctx = requireContext();
        TextView t = new TextView(ctx);
        t.setText(text);
        t.setTextSize(13);
        int pad = (int) (8 * getResources().getDisplayMetrics().density);
        t.setPadding(pad, pad, pad, pad);
        t.setGravity(Gravity.CENTER_VERTICAL);
        if (header) t.setTextColor(Color.WHITE);
        else if (highlight) t.setTextColor(ContextCompat.getColor(ctx, R.color.primary));
        else t.setTextColor(ContextCompat.getColor(ctx, R.color.text_dark));
        if (bold || highlight) t.setTypeface(Typeface.DEFAULT_BOLD);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, weight);
        t.setLayoutParams(lp);
        return t;
    }
}
