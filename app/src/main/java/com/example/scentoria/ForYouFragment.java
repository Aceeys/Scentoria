package com.example.scentoria;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.Slider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Feature 07 Recommendations: scores every perfume against the user's selected preferences. */
public class ForYouFragment extends Fragment {

    private ChipGroup cgFamily, cgOccasion;
    private Slider slider;
    private TextView tvBudget, tvHint;
    private PerfumeAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle b) {
        return inflater.inflate(R.layout.fragment_foryou, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        Context ctx = requireContext();
        cgFamily = v.findViewById(R.id.cgFamily);
        cgOccasion = v.findViewById(R.id.cgOccasion);
        slider = v.findViewById(R.id.sliderBudget);
        tvBudget = v.findViewById(R.id.tvBudget);
        tvHint = v.findViewById(R.id.tvHint);
        RecyclerView rv = v.findViewById(R.id.recycler);

        adapter = new PerfumeAdapter(ctx);
        rv.setLayoutManager(new LinearLayoutManager(ctx));
        rv.setAdapter(adapter);

        FilterSheet.fillChips(ctx, cgFamily, PerfumeData.families(),
                UserPrefs.getSet(ctx, "pref_family"), R.layout.chip_filter);
        FilterSheet.fillChips(ctx, cgOccasion, PerfumeData.occasions(),
                UserPrefs.getSet(ctx, "pref_occasion"), R.layout.chip_filter);

        slider.setValue(UserPrefs.getBudget(ctx));
        tvBudget.setText("Up to " + Fmt.peso(UserPrefs.getBudget(ctx)));

        cgFamily.setOnCheckedStateChangeListener((g, ids) -> onChanged());
        cgOccasion.setOnCheckedStateChangeListener((g, ids) -> onChanged());
        slider.addOnChangeListener((s, value, fromUser) -> {
            tvBudget.setText("Up to " + Fmt.peso(Math.round(value)));
            if (fromUser) onChanged();
        });
        refresh();
    }

    private void onChanged() {
        Context ctx = requireContext();
        UserPrefs.putSet(ctx, "pref_family", FilterSheet.readChecked(cgFamily));
        UserPrefs.putSet(ctx, "pref_occasion", FilterSheet.readChecked(cgOccasion));
        UserPrefs.setBudget(ctx, Math.round(slider.getValue()));
        refresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) refresh();
    }

    private void refresh() {
        Set<String> fams = FilterSheet.readChecked(cgFamily);
        Set<String> occs = FilterSheet.readChecked(cgOccasion);
        int budget = Math.round(slider.getValue());

        if (fams.isEmpty() && occs.isEmpty()) {
            tvHint.setText("Pick at least one scent family or occasion to get recommendations.");
            adapter.setItems(new ArrayList<>());
            return;
        }

        final Map<Integer, Double> rm = ReviewStore.ratingMap(requireContext());
        final Map<Integer, Integer> score = new HashMap<>();
        List<Perfume> list = new ArrayList<>();
        for (Perfume p : PerfumeData.getAll()) {
            int s = 0;
            if (fams.contains(p.family)) s += 40;
            int om = 0;
            for (String o : p.occasions) if (occs.contains(o)) om++;
            s += Math.min(om, 2) * 15;
            s += p.price <= budget ? 20 : -15;
            Double rv = rm.get(p.id);
            if (rv != null) s += (int) Math.round((rv - 3.5) * 10);
            s = Math.max(0, Math.min(100, s));
            if (s >= 30) { score.put(p.id, s); list.add(p); }
        }
        Collections.sort(list, (x, y) -> Integer.compare(score.get(y.id), score.get(x.id)));

        if (list.size() > 20) list = new ArrayList<>(list.subList(0, 20));

        Map<Integer, String> extras = new HashMap<>();
        for (Perfume p : list) extras.put(p.id, score.get(p.id) + "% match");
        adapter.setExtras(extras);
        adapter.setItems(list);
        tvHint.setText(list.isEmpty()
                ? "No close matches. Try a higher budget or more preferences."
                : "Top picks based on your preferences:");
    }
}
