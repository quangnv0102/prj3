package com.example.prj3.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prj3.R;
import com.example.prj3.models.Movie;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
    }

    private final List<Movie> movies;
    private final OnMovieClickListener listener;
    private final boolean isGrid;

    public MovieAdapter(List<Movie> movies, OnMovieClickListener listener, boolean isGrid) {
        this.movies = movies;
        this.listener = listener;
        this.isGrid = isGrid;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = isGrid ? R.layout.item_movie_grid : R.layout.item_movie_card;
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        holder.bind(movies.get(position));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivPoster;
        private final TextView tvTitle, tvGenre, tvRating;
        private TextView tvDuration;

        MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvGenre = itemView.findViewById(R.id.tvGenre);
            tvRating = itemView.findViewById(R.id.tvRating);
            if (isGrid) tvDuration = itemView.findViewById(R.id.tvDuration);
        }

        void bind(Movie movie) {
            tvTitle.setText(movie.getTitle());
            tvGenre.setText(movie.getGenre());
            tvRating.setText(String.format("%.1f", movie.getRating()));
            if (isGrid && tvDuration != null) {
                tvDuration.setText(movie.getDuration() + "p");
            }

            Glide.with(itemView.getContext())
                .load(movie.getImageUrl())
                .centerCrop()
                .placeholder(R.color.background_surface)
                .error(R.color.background_card)
                .into(ivPoster);

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onMovieClick(movie);
            });
        }
    }
}
