package com.example.scentoria;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Bottom sheet with scent family, brand, occasion, PRICE RANGE and sort filters. */
public class FilterSheet {

    public interface OnApply { void apply(); }

    public static void fillChips(Context ctx, ChipGroup group, List<String> items,
                                 Set<String> selected, int chipLayout) {
        LayoutInflater inf = LayoutInflater.from(ctx);
        group.removeAllViews();
        for (String s : items) {
            Chip chip = (Chip) inf.inflate(chipLayout, group, false);
            chip.setText(s);
            chip.setChecked(selected.contains(s));
            group.addView(chip);
        }
    }

    public static Set<String> readChecked(ChipGroup group) {
        Set<String> out = new HashSet<>();
        for (int i = 0; i < group.getChildCount(); i++) {
            Chip c = (Chip) group.getChildAt(i);
            if (c.isChecked()) out.add(c.getText().toString());
        }
        return out;
    }

    public static void show(Context ctx, FilterState st, OnApply cb) {
        BottomSheetDialog dialog = new BottomSheetDialog(ctx);
        View v = LayoutInflater.from(ctx).inflate(R.layout.sheet_filter, null);
        dialog.setContentView(v);

        ChipGroup cgFamily = v.findViewById(R.id.cgFamily);
        ChipGroup cgBrand = v.findViewById(R.id.cgBrand);
        ChipGroup cgOcc = v.findViewById(R.id.cgOccasion);
        ChipGroup cgSort = v.findViewById(R.id.cgSort);
        TextView tvPrice = v.findViewById(R.id.tvPriceRange);
        RangeSlider slider = v.findViewById(R.id.sliderPrice);

        fillChips(ctx, cgFamily, PerfumeData.families(), st.families, R.layout.chip_filter);
        fillChips(ctx, cgBrand, PerfumeData.brands(), st.brands, R.layout.chip_filter);
        fillChips(ctx, cgOcc, PerfumeData.occasions(), st.occasions, R.layout.chip_filter);

        Set<String> sortSel = new HashSet<>();
        sortSel.add(st.sort);
        List<String> sorts = java.util.Arrays.asList(FilterState.SORTS);
        fillChips(ctx, cgSort, sorts, sortSel, R.layout.chip_choice);

        slider.setValues((float) st.minPrice, (float) st.maxPrice);
        tvPrice.setText(Fmt.peso(st.minPrice) + "  -  " + Fmt.peso(st.maxPrice));
        slider.addOnChangeListener((s, value, fromUser) -> {
            List<Float> vals = s.getValues();
            tvPrice.setText(Fmt.peso(Math.round(vals.get(0))) + "  -  " + Fmt.peso(Math.round(vals.get(1))));
        });

        Button btnApply = v.findViewById(R.id.btnApply);
        Button btnReset = v.findViewById(R.id.btnReset);

        btnApply.setOnClickListener(x -> {
            st.families.clear(); st.families.addAll(readChecked(cgFamily));
            st.brands.clear(); st.brands.addAll(readChecked(cgBrand));
            st.occasions.clear(); st.occasions.addAll(readChecked(cgOcc));
            List<Float> vals = slider.getValues();
            st.minPrice = Math.round(vals.get(0));
            st.maxPrice = Math.round(vals.get(1));
            Set<String> sortPicked = readChecked(cgSort);
            if (!sortPicked.isEmpty()) st.sort = sortPicked.iterator().next();
            dialog.dismiss();
            cb.apply();
        });
        btnReset.setOnClickListener(x -> {
            st.reset();
            dialog.dismiss();
            cb.apply();
        });
        dialog.show();
    }
}
