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
import com.example.prj3.models.Ticket;

import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private final List<Ticket> tickets;

    public TicketAdapter(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        holder.bind(tickets.get(position));
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    static class TicketViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivMoviePoster;
        private final TextView tvMovieTitle, tvGenre, tvStatus;
        private final TextView tvTheater, tvShowtime, tvSeats, tvTotalPrice, tvBookingId;

        TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMoviePoster = itemView.findViewById(R.id.ivMoviePoster);
            tvMovieTitle = itemView.findViewById(R.id.tvMovieTitle);
            tvGenre = itemView.findViewById(R.id.tvGenre);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTheater = itemView.findViewById(R.id.tvTheater);
            tvShowtime = itemView.findViewById(R.id.tvShowtime);
            tvSeats = itemView.findViewById(R.id.tvSeats);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            tvBookingId = itemView.findViewById(R.id.tvBookingId);
        }

        void bind(Ticket ticket) {
            tvMovieTitle.setText(ticket.getMovieTitle());
            tvGenre.setText(ticket.getMovieGenre());
            tvTheater.setText(ticket.getTheater());
            tvShowtime.setText(ticket.getShowTime() + " - " + ticket.getShowDate());
            tvSeats.setText(ticket.getSeatCount() + " ghế");
            tvTotalPrice.setText(ticket.getTotalPriceFormatted());

            if (ticket.getId() != null) {
                String shortId = ticket.getId().length() > 8
                    ? ticket.getId().substring(0, 8).toUpperCase()
                    : ticket.getId().toUpperCase();
                tvBookingId.setText("Mã: #" + shortId);
            }

            // Status
            String status = ticket.getStatus();
            if ("confirmed".equals(status)) {
                tvStatus.setText("✓ Đã xác nhận");
                tvStatus.setTextColor(itemView.getContext().getColor(R.color.success));
            } else if ("cancelled".equals(status)) {
                tvStatus.setText("✗ Đã hủy");
                tvStatus.setTextColor(itemView.getContext().getColor(R.color.error));
            } else {
                tvStatus.setText("⏳ Chờ xác nhận");
                tvStatus.setTextColor(itemView.getContext().getColor(R.color.warning));
            }

            Glide.with(itemView.getContext())
                .load(ticket.getMovieImageUrl())
                .centerCrop()
                .placeholder(R.color.background_surface)
                .error(R.color.background_card)
                .into(ivMoviePoster);

            itemView.setOnClickListener(v -> {
                android.content.Context context = itemView.getContext();
                String seatsJoined = ticket.getSeatNumbers() != null 
                    ? android.text.TextUtils.join(", ", ticket.getSeatNumbers()) 
                    : "Không xác định";
                
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss", java.util.Locale.getDefault());
                String bTime = sdf.format(new java.util.Date(ticket.getBookingTime()));

                String details = "Phim: " + ticket.getMovieTitle() + "\n"
                    + "Rạp: " + ticket.getTheater() + "\n"
                    + "Giờ chiếu: " + ticket.getShowTime() + " - " + ticket.getShowDate() + "\n"
                    + "Vị trí ghế: " + seatsJoined + "\n"
                    + "Đơn giá: " + ticket.getPricePerSeat() + " đ\n"
                    + "Tổng tiền: " + ticket.getTotalPriceFormatted() + "\n"
                    + "Thời gian đặt: " + bTime + "\n"
                    + "Mã tham chiếu: " + ticket.getId();

                new com.google.android.material.dialog.MaterialAlertDialogBuilder(context)
                    .setTitle("Chi Tiết Vé")
                    .setMessage(details)
                    .setPositiveButton("Đóng", null)
                    .show();
            });
        }
    }
}
