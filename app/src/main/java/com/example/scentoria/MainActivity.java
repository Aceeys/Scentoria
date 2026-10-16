package com.example.scentoria;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setOnItemSelectedListener(item -> { show(item.getItemId()); return true; });
        if (savedInstanceState == null) nav.setSelectedItemId(R.id.nav_search);
    }

    private void show(int id) {
        Fragment f;
        String title;
        if (id == R.id.nav_favorites) { f = new FavoritesFragment(); title = getString(R.string.nav_favorites); }
        else if (id == R.id.nav_compare) { f = new CompareFragment(); title = "Compare Fragrances"; }
        else if (id == R.id.nav_foryou) { f = new ForYouFragment(); title = "Recommended For You"; }
        else if (id == R.id.nav_profile) { f = new ProfileFragment(); title = getString(R.string.nav_profile); }
        else { f = new HomeFragment(); title = getString(R.string.app_name); }

        if (getSupportActionBar() != null) getSupportActionBar().setTitle(title);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, f).commit();
    }
}
