package com.example.scentoria;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Feature 01 Fragrance Search + Feature 05 Search & Filter (family, brand, occasion, price). */
public class HomeFragment extends Fragment {

    private static final FilterState filter = new FilterState();
    private static String lastQuery = "";

    private PerfumeAdapter adapter;
    private EditText etSearch;
    private TextView tvSummary, tvEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle b) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        Context ctx = requireContext();
        etSearch = v.findViewById(R.id.etSearch);
        tvSummary = v.findViewById(R.id.tvSummary);
        tvEmpty = v.findViewById(R.id.tvEmpty);
        ImageButton btnFilter = v.findViewById(R.id.btnFilter);
        RecyclerView rv = v.findViewById(R.id.recycler);

        adapter = new PerfumeAdapter(ctx);
        rv.setLayoutManager(new LinearLayoutManager(ctx));
        rv.setAdapter(adapter);

        etSearch.setText(lastQuery);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b2, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b2, int c) {
                lastQuery = s.toString();
                applyFilter();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
        btnFilter.setOnClickListener(x -> FilterSheet.show(ctx, filter, this::applyFilter));
    }

    @Override
    public void onResume() {
        super.onResume();
        applyFilter(); // refresh hearts / ratings after returning from detail
    }

    private void applyFilter() {
        if (adapter == null) return;
        String q = etSearch.getText().toString().trim().toLowerCase(Locale.ROOT);
        List<Perfume> out = new ArrayList<>();
        for (Perfume p : PerfumeData.getAll()) {
            if ((q.isEmpty() || p.searchText().contains(q)) && filter.matches(p)) out.add(p);
        }
        filter.sort(ReviewStore.ratingMap(requireContext()), out);
        adapter.setItems(out);

        StringBuilder s = new StringBuilder(out.size() + " fragrances");
        if (filter.activeCount() > 0) {
            s.append("  \u2022  ").append(filter.activeCount()).append(" filter(s) active");
            if (filter.priceActive())
                s.append("  \u2022  ").append(Fmt.peso(filter.minPrice)).append("-").append(Fmt.peso(filter.maxPrice));
        }
        s.append("  \u2022  ").append(filter.sort);
        tvSummary.setText(s.toString());
        tvEmpty.setVisibility(out.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
