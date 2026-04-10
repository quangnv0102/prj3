package com.example.prj3.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.prj3.R;
import com.example.prj3.activities.MovieDetailActivity;
import com.example.prj3.adapters.FeaturedMovieAdapter;
import com.example.prj3.adapters.MovieAdapter;
import com.example.prj3.models.Movie;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private ViewPager2 vpFeatured;
    private LinearLayout dotsLayout;
    private RecyclerView rvNowShowing, rvComingSoon;
    private TextView tvGreeting, tvUsername;

    private FeaturedMovieAdapter featuredAdapter;
    private MovieAdapter nowShowingAdapter, comingSoonAdapter;
    private List<Movie> featuredMovies = new ArrayList<>();
    private List<Movie> nowShowingMovies = new ArrayList<>();
    private List<Movie> comingSoonMovies = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(view);
        setupGreeting();
        setupRecyclerViews();
        loadMovies();
        setupRemoteConfig();
    }

    private void bindViews(View view) {
        vpFeatured = view.findViewById(R.id.vpFeatured);
        dotsLayout = view.findViewById(R.id.dotsLayout);
        rvNowShowing = view.findViewById(R.id.rvNowShowing);
        rvComingSoon = view.findViewById(R.id.rvComingSoon);
        tvGreeting = view.findViewById(R.id.tvGreeting);
        tvUsername = view.findViewById(R.id.tvUsername);

        view.findViewById(R.id.ivNotification).setOnClickListener(v ->
            // Log FCM token for demo
            com.google.firebase.messaging.FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(token ->
                    android.widget.Toast.makeText(requireContext(),
                        "FCM Token đã sẵn sàng!", android.widget.Toast.LENGTH_SHORT).show()
                )
        );
    }

    private void setupGreeting() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int hour = calendar.get(java.util.Calendar.HOUR_OF_DAY);
        String greeting;
        if (hour < 12) greeting = "Chào buổi sáng,";
        else if (hour < 18) greeting = "Chào buổi chiều,";
        else greeting = "Chào buổi tối,";

        tvGreeting.setText(greeting);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            if (name != null && !name.isEmpty()) {
                tvUsername.setText(name);
            } else {
                // Load from database
                FirebaseDatabase.getInstance().getReference("users")
                    .child(user.getUid()).child("name")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String dbName = snapshot.getValue(String.class);
                            if (dbName != null && !dbName.isEmpty() && isAdded()) {
                                tvUsername.setText(dbName);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
            }
        }
    }

    private void setupRecyclerViews() {
        // Featured ViewPager
        featuredAdapter = new FeaturedMovieAdapter(featuredMovies, movie -> openMovieDetail(movie));
        vpFeatured.setAdapter(featuredAdapter);
        vpFeatured.setOffscreenPageLimit(3);
        vpFeatured.setPageTransformer((page, position) -> {
            float scale = 1 - Math.abs(position) * 0.15f;
            page.setScaleY(scale);
        });
        vpFeatured.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateDots(position);
            }
        });

        // Now Showing
        nowShowingAdapter = new MovieAdapter(nowShowingMovies, movie -> openMovieDetail(movie), false);
        rvNowShowing.setLayoutManager(
            new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvNowShowing.setAdapter(nowShowingAdapter);

        // Coming Soon
        comingSoonAdapter = new MovieAdapter(comingSoonMovies, movie -> openMovieDetail(movie), false);
        rvComingSoon.setLayoutManager(
            new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvComingSoon.setAdapter(comingSoonAdapter);
    }

    private void loadMovies() {
        FirebaseDatabase.getInstance().getReference("movies")
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!isAdded()) return;
                    featuredMovies.clear();
                    nowShowingMovies.clear();
                    comingSoonMovies.clear();

                    for (DataSnapshot child : snapshot.getChildren()) {
                        Movie movie = child.getValue(Movie.class);
                        if (movie != null) {
                            movie.setId(child.getKey());
                            if (movie.isNowShowing()) {
                                nowShowingMovies.add(movie);
                                if (featuredMovies.size() < 5) featuredMovies.add(movie);
                            }
                            if (movie.isComingSoon()) comingSoonMovies.add(movie);
                        }
                    }

                    featuredAdapter.notifyDataSetChanged();
                    nowShowingAdapter.notifyDataSetChanged();
                    comingSoonAdapter.notifyDataSetChanged();
                    setupDots(featuredMovies.size());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
    }

    private void setupDots(int count) {
        dotsLayout.removeAllViews();
        for (int i = 0; i < count; i++) {
            View dot = new View(requireContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                i == 0 ? 24 : 8, 8);
            params.setMargins(4, 0, 4, 0);
            dot.setLayoutParams(params);
            dot.setBackgroundResource(i == 0 ?
                R.drawable.dot_active : R.drawable.dot_inactive);
            dotsLayout.addView(dot);
        }
    }

    private void updateDots(int activeIndex) {
        for (int i = 0; i < dotsLayout.getChildCount(); i++) {
            View dot = dotsLayout.getChildAt(i);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) dot.getLayoutParams();
            params.width = i == activeIndex ? 24 : 8;
            dot.setLayoutParams(params);
            dot.setBackgroundResource(i == activeIndex ?
                R.drawable.dot_active : R.drawable.dot_inactive);
        }
    }

    private void openMovieDetail(Movie movie) {
        Intent intent = new Intent(requireContext(), MovieDetailActivity.class);
        intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_ID, movie.getId());
        startActivity(intent);
    }

    private void setupRemoteConfig() {
        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings settings = new FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)
            .build();
        remoteConfig.setConfigSettingsAsync(settings);

        // Default values
        java.util.HashMap<String, Object> defaults = new java.util.HashMap<>();
        defaults.put("show_banner", true);
        defaults.put("discount_percent", 0);
        remoteConfig.setDefaultsAsync(defaults);

        remoteConfig.fetchAndActivate().addOnSuccessListener(updated -> {
            boolean showBanner = remoteConfig.getBoolean("show_banner");
            long discount = remoteConfig.getLong("discount_percent");
            // Apply remote config values (e.g., show/hide promotional banner)
        });
    }
}
