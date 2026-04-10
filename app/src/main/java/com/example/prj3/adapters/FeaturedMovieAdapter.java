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

public class FeaturedMovieAdapter extends RecyclerView.Adapter<FeaturedMovieAdapter.FeaturedViewHolder> {

    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
    }

    private final List<Movie> movies;
    private final OnMovieClickListener listener;

    public FeaturedMovieAdapter(List<Movie> movies, OnMovieClickListener listener) {
        this.movies = movies;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FeaturedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_featured_movie, parent, false);
        return new FeaturedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeaturedViewHolder holder, int position) {
        holder.bind(movies.get(position));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class FeaturedViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivFeaturedPoster;
        private final TextView tvFeaturedTitle, tvFeaturedRating, tvFeaturedGenre;

        FeaturedViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFeaturedPoster = itemView.findViewById(R.id.ivFeaturedPoster);
            tvFeaturedTitle = itemView.findViewById(R.id.tvFeaturedTitle);
            tvFeaturedRating = itemView.findViewById(R.id.tvFeaturedRating);
            tvFeaturedGenre = itemView.findViewById(R.id.tvFeaturedGenre);
        }

        void bind(Movie movie) {
            tvFeaturedTitle.setText(movie.getTitle());
            tvFeaturedRating.setText(String.format("%.1f", movie.getRating()));
            tvFeaturedGenre.setText(movie.getGenre());

            Glide.with(itemView.getContext())
                .load(movie.getImageUrl())
                .centerCrop()
                .placeholder(R.color.background_surface)
                .into(ivFeaturedPoster);

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onMovieClick(movie);
            });
        }
    }
}
