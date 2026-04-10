package com.example.prj3.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prj3.R;
import com.example.prj3.adapters.ShowtimeAdapter;
import com.example.prj3.models.Movie;
import com.example.prj3.models.Showtime;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE_ID = "movie_id";

    private ImageView ivMoviePoster;
    private TextView tvMovieTitle, tvGenre, tvRating, tvDuration, tvLanguage, tvYear, tvSynopsis;
    private RecyclerView rvShowtimes;
    private ShowtimeAdapter showtimeAdapter;
    private List<Showtime> showtimes = new ArrayList<>();
    private Movie currentMovie;
    private Showtime selectedShowtime;
    private FirebaseAnalytics mAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mAnalytics = FirebaseAnalytics.getInstance(this);

        String movieId = getIntent().getStringExtra(EXTRA_MOVIE_ID);
        if (movieId == null) { finish(); return; }

        setupToolbar();
        bindViews();
        loadMovie(movieId);
        loadShowtimes(movieId);

        findViewById(R.id.btnBookNow).setOnClickListener(v -> {
            if (selectedShowtime == null) {
                Toast.makeText(this, "Vui lòng chọn suất chiếu!", Toast.LENGTH_SHORT).show();
                return;
            }
            openBooking();
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void bindViews() {
        ivMoviePoster = findViewById(R.id.ivMoviePoster);
        tvMovieTitle = findViewById(R.id.tvMovieTitle);
        tvGenre = findViewById(R.id.tvGenre);
        tvRating = findViewById(R.id.tvRating);
        tvDuration = findViewById(R.id.tvDuration);
        tvLanguage = findViewById(R.id.tvLanguage);
        tvYear = findViewById(R.id.tvYear);
        tvSynopsis = findViewById(R.id.tvSynopsis);
        rvShowtimes = findViewById(R.id.rvShowtimes);
    }

    private void loadMovie(String movieId) {
        FirebaseDatabase.getInstance().getReference("movies").child(movieId)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    currentMovie = snapshot.getValue(Movie.class);
                    if (currentMovie != null) {
                        currentMovie.setId(snapshot.getKey());
                        displayMovie(currentMovie);

                        // Log analytics
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, movieId);
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, currentMovie.getTitle());
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "movie");
                        mAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(MovieDetailActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void displayMovie(Movie movie) {
        tvMovieTitle.setText(movie.getTitle());
        tvGenre.setText(movie.getGenre());
        tvRating.setText(String.format("%.1f", movie.getRating()));
        tvDuration.setText(movie.getDuration() + " " + getString(R.string.minutes));
        tvLanguage.setText(movie.getLanguage() != null ? movie.getLanguage() : "Tiếng Anh");
        tvYear.setText(String.valueOf(movie.getYear()));
        tvSynopsis.setText(movie.getDescription());

        Glide.with(this)
            .load(movie.getImageUrl())
            .centerCrop()
            .placeholder(R.color.background_surface)
            .error(R.color.background_card)
            .into(ivMoviePoster);
    }

    private void loadShowtimes(String movieId) {
        showtimeAdapter = new ShowtimeAdapter(showtimes, showtime -> {
            selectedShowtime = showtime;
        });
        rvShowtimes.setLayoutManager(new LinearLayoutManager(this));
        rvShowtimes.setAdapter(showtimeAdapter);

        FirebaseDatabase.getInstance().getReference("showtimes")
            .orderByChild("movieId").equalTo(movieId)
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    showtimes.clear();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Showtime s = child.getValue(Showtime.class);
                        if (s != null) {
                            s.setId(child.getKey());
                            showtimes.add(s);
                        }
                    }
                    showtimeAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError error) {}
            });
    }

    private void openBooking() {
        Intent intent = new Intent(this, BookingActivity.class);
        intent.putExtra(BookingActivity.EXTRA_MOVIE_ID, currentMovie.getId());
        intent.putExtra(BookingActivity.EXTRA_MOVIE_TITLE, currentMovie.getTitle());
        intent.putExtra(BookingActivity.EXTRA_MOVIE_GENRE, currentMovie.getGenre());
        intent.putExtra(BookingActivity.EXTRA_MOVIE_IMAGE, currentMovie.getImageUrl());
        intent.putExtra(BookingActivity.EXTRA_SHOWTIME_ID, selectedShowtime.getId());
        intent.putExtra(BookingActivity.EXTRA_THEATER, selectedShowtime.getTheater());
        intent.putExtra(BookingActivity.EXTRA_DATE, selectedShowtime.getDate());
        intent.putExtra(BookingActivity.EXTRA_TIME, selectedShowtime.getTime());
        intent.putExtra(BookingActivity.EXTRA_PRICE, selectedShowtime.getPrice());
        startActivity(intent);
    }
}
