package com.example.prj3.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prj3.R;
import com.example.prj3.activities.MovieDetailActivity;
import com.example.prj3.adapters.MovieAdapter;
import com.example.prj3.models.Movie;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MoviesFragment extends Fragment {

    private RecyclerView rvMovies;
    private ProgressBar progressBar;
    private TextInputEditText etSearch;
    private MovieAdapter movieAdapter;
    private List<Movie> allMovies = new ArrayList<>();
    private List<Movie> filteredMovies = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movies, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvMovies = view.findViewById(R.id.rvMovies);
        progressBar = view.findViewById(R.id.progressBar);
        etSearch = view.findViewById(R.id.etSearch);

        movieAdapter = new MovieAdapter(filteredMovies, movie -> {
            Intent intent = new Intent(requireContext(), MovieDetailActivity.class);
            intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_ID, movie.getId());
            startActivity(intent);
        }, true);

        rvMovies.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvMovies.setAdapter(movieAdapter);

        loadMovies();
        setupSearch();
    }

    private void loadMovies() {
        progressBar.setVisibility(View.VISIBLE);

        FirebaseDatabase.getInstance().getReference("movies")
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!isAdded()) return;
                    allMovies.clear();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Movie movie = child.getValue(Movie.class);
                        if (movie != null) {
                            movie.setId(child.getKey());
                            allMovies.add(movie);
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                    filterMovies(etSearch.getText() != null ?
                        etSearch.getText().toString() : "");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    if (isAdded()) progressBar.setVisibility(View.GONE);
                }
            });
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMovies(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void filterMovies(String query) {
        filteredMovies.clear();
        if (query.isEmpty()) {
            filteredMovies.addAll(allMovies);
        } else {
            String lower = query.toLowerCase();
            for (Movie movie : allMovies) {
                if (movie.getTitle() != null && movie.getTitle().toLowerCase().contains(lower) ||
                    movie.getGenre() != null && movie.getGenre().toLowerCase().contains(lower)) {
                    filteredMovies.add(movie);
                }
            }
        }
        movieAdapter.notifyDataSetChanged();
    }
}
