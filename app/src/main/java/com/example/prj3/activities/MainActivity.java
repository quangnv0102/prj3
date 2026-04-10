package com.example.prj3.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.prj3.R;
import com.example.prj3.fragments.BookingsFragment;
import com.example.prj3.fragments.HomeFragment;
import com.example.prj3.fragments.MoviesFragment;
import com.example.prj3.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private FirebaseAnalytics mAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAnalytics = FirebaseAnalytics.getInstance(this);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // Default fragment
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                fragment = new HomeFragment();
                logScreenView("home");
            } else if (id == R.id.nav_movies) {
                fragment = new MoviesFragment();
                logScreenView("movies");
            } else if (id == R.id.nav_bookings) {
                fragment = new BookingsFragment();
                logScreenView("bookings");
            } else if (id == R.id.nav_profile) {
                fragment = new ProfileFragment();
                logScreenView("profile");
            } else {
                return false;
            }

            loadFragment(fragment);
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit();
    }

    private void logScreenView(String screenName) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity");
        mAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
    }
}
