package com.example.scentoria;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PerfumeAdapter extends RecyclerView.Adapter<PerfumeAdapter.VH> {

    private final Context context;
    private List<Perfume> items = new ArrayList<>();
    private Map<Integer, String> extras = new HashMap<>();

    public PerfumeAdapter(Context context) { this.context = context; }

    public void setItems(List<Perfume> list) { items = list; notifyDataSetChanged(); }

    /** Optional extra line per perfume id (e.g. "92% match"). */
    public void setExtras(Map<Integer, String> e) { extras = e; }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(context).inflate(R.layout.item_perfume, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Perfume p = items.get(position);
        h.name.setText(p.name);
        h.brand.setText(p.brand);
        h.meta.setText(Fmt.meta(p));
        double rt = ReviewStore.rating(context, p);
        h.rating.setText(rt > 0 ? String.format(Locale.US, "\u2605 %.1f", rt) : "\u2606 new");
        h.price.setText(Fmt.price(p));
        h.fav.setText(Favorites.isFav(context, p.id) ? "\u2665" : "");
        h.initial.setText(p.name.substring(0, 1));

        GradientDrawable circle = new GradientDrawable();
        circle.setShape(GradientDrawable.OVAL);
        circle.setColor(p.color);
        h.initial.setBackground(circle);
        PerfumeImages.loadCircle(context, p, h.photo);

        String extra = extras.get(p.id);
        if (extra == null) h.extra.setVisibility(View.GONE);
        else { h.extra.setVisibility(View.VISIBLE); h.extra.setText(extra); }

        h.itemView.setOnClickListener(v -> DetailActivity.open(context, p.id));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView initial, name, brand, meta, extra, rating, price, fav;
        android.widget.ImageView photo;
        VH(View v) {
            super(v);
            initial = v.findViewById(R.id.tvInitial);
            photo = v.findViewById(R.id.ivPhoto);
            name = v.findViewById(R.id.tvName);
            brand = v.findViewById(R.id.tvBrand);
            meta = v.findViewById(R.id.tvMeta);
            extra = v.findViewById(R.id.tvExtra);
            rating = v.findViewById(R.id.tvRating);
            price = v.findViewById(R.id.tvPrice);
            fav = v.findViewById(R.id.tvFav);
        }
    }
}
