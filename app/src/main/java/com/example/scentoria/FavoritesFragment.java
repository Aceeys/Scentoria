package com.example.scentoria;

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

import java.util.ArrayList;
import java.util.List;

/** Feature 04 Favorites. */
public class FavoritesFragment extends Fragment {

    private PerfumeAdapter adapter;
    private TextView tvEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle b) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        tvEmpty = v.findViewById(R.id.tvEmpty);
        RecyclerView rv = v.findViewById(R.id.recycler);
        adapter = new PerfumeAdapter(requireContext());
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        List<Perfume> favs = new ArrayList<>();
        for (Perfume p : PerfumeData.getAll())
            if (Favorites.isFav(requireContext(), p.id)) favs.add(p);
        adapter.setItems(favs);
        tvEmpty.setVisibility(favs.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
