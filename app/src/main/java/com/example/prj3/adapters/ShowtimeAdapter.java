package com.example.prj3.adapters;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prj3.R;
import com.example.prj3.models.Showtime;

import java.util.List;

public class ShowtimeAdapter extends RecyclerView.Adapter<ShowtimeAdapter.ShowtimeViewHolder> {

    public interface OnShowtimeClickListener {
        void onShowtimeClick(Showtime showtime);
    }

    private final List<Showtime> showtimes;
    private final OnShowtimeClickListener listener;
    private int selectedPosition = -1;

    public ShowtimeAdapter(List<Showtime> showtimes, OnShowtimeClickListener listener) {
        this.showtimes = showtimes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ShowtimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_showtime, parent, false);
        return new ShowtimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowtimeViewHolder holder, int position) {
        holder.bind(showtimes.get(position), position == selectedPosition);
    }

    @Override
    public int getItemCount() {
        return showtimes.size();
    }

    class ShowtimeViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTime, tvDate, tvTheater, tvAvailableSeats, tvPrice;
        private final View rootLayout;

        ShowtimeViewHolder(@NonNull View itemView) {
            super(itemView);
            rootLayout = itemView.findViewById(R.id.layoutShowtime);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTheater = itemView.findViewById(R.id.tvTheater);
            tvAvailableSeats = itemView.findViewById(R.id.tvAvailableSeats);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }

        void bind(Showtime showtime, boolean selected) {
            tvTime.setText(showtime.getTime());
            tvDate.setText(showtime.getDate());
            tvTheater.setText(showtime.getTheater());
            tvAvailableSeats.setText(showtime.getAvailableSeats() + " ghế trống");
            tvPrice.setText(showtime.getPrice() / 1000 + "K");

            // Highlight selected
            if (selected) {
                rootLayout.setBackgroundResource(R.drawable.bg_selected_showtime);
                tvTime.setTextColor(rootLayout.getContext().getColor(R.color.primary));
                tvPrice.setTextColor(rootLayout.getContext().getColor(R.color.primary));
            } else {
                rootLayout.setBackgroundResource(R.drawable.bg_card_movie);
                tvTime.setTextColor(rootLayout.getContext().getColor(R.color.text_primary));
                tvPrice.setTextColor(rootLayout.getContext().getColor(R.color.secondary));
            }

            int seatsColor = showtime.getAvailableSeats() > 10
                ? R.color.success : R.color.warning;
            tvAvailableSeats.setTextColor(rootLayout.getContext().getColor(seatsColor));

            itemView.setOnClickListener(v -> {
                int prev = selectedPosition;
                selectedPosition = getAdapterPosition();
                notifyItemChanged(prev);
                notifyItemChanged(selectedPosition);
                if (listener != null) listener.onShowtimeClick(showtime);
            });
        }
    }
}
