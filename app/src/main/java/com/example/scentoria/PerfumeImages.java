package com.example.scentoria;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads the bottle picture of a fragrance. Order of priority:
 *  1) a file in app/src/main/assets/perfumes/<id>.jpg (or .png / .webp)  - works offline
 *  2) Perfume.imageUrl (a web link)                                       - needs internet
 *  3) nothing: the colored initial circle is shown instead.
 */
public class PerfumeImages {

    private static Map<Integer, String> assetFiles;

    private static void init(Context c) {
        if (assetFiles != null) return;
        assetFiles = new HashMap<>();
        try {
            String[] files = c.getAssets().list("perfumes");
            if (files == null) return;
            for (String f : files) {
                int dot = f.lastIndexOf('.');
                if (dot <= 0) continue;
                String ext = f.substring(dot + 1).toLowerCase();
                if (!(ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png") || ext.equals("webp"))) continue;
                try {
                    assetFiles.put(Integer.parseInt(f.substring(0, dot)), f);
                } catch (NumberFormatException ignored) { }
            }
        } catch (IOException ignored) { }
    }

    /** Returns an image source for Glide, or null if this fragrance has no picture. */
    public static String source(Context c, Perfume p) {
        init(c);
        String f = assetFiles.get(p.id);
        if (f != null) return "file:///android_asset/perfumes/" + f;
        if (p.imageUrl != null && !p.imageUrl.isEmpty()) return p.imageUrl;
        return null;
    }

    /** List card: bottle photo on a white tile over the colored initial (initial stays if no photo). */
    public static void loadCircle(Context c, Perfume p, ImageView iv) {
        String src = source(c, p);
        if (src == null) {
            Glide.with(c).clear(iv);
            iv.setImageDrawable(null);
            iv.setVisibility(View.INVISIBLE);
        } else {
            iv.setVisibility(View.VISIBLE);
            Glide.with(c).load(src).into(iv);
        }
    }

    /** Detail header: big bottle picture, hidden when there is none. */
    public static void loadBottle(Context c, Perfume p, ImageView iv) {
        String src = source(c, p);
        if (src == null) {
            iv.setVisibility(View.GONE);
        } else {
            iv.setVisibility(View.VISIBLE);
            Glide.with(c).load(src).into(iv);
        }
    }
}
