package com.example.prj3.activities;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.prj3.R;
import com.example.prj3.models.Ticket;
import com.example.prj3.utils.ReminderScheduler;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class BookingActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE_ID = "extra_movie_id";
    public static final String EXTRA_MOVIE_TITLE = "extra_movie_title";
    public static final String EXTRA_MOVIE_GENRE = "extra_movie_genre";
    public static final String EXTRA_MOVIE_IMAGE = "extra_movie_image";
    public static final String EXTRA_SHOWTIME_ID = "extra_showtime_id";
    public static final String EXTRA_THEATER = "extra_theater";
    public static final String EXTRA_DATE = "extra_date";
    public static final String EXTRA_TIME = "extra_time";
    public static final String EXTRA_PRICE = "extra_price";

    private static final int ROWS = 6;
    private static final int COLS = 8;
    private static final String[] ROW_LABELS = {"A", "B", "C", "D", "E", "F"};

    private TextView tvMovieTitle, tvGenre, tvTheater, tvShowtime;
    private TextView tvTicketPrice, tvSeatCountInfo, tvTotalPrice, tvSelectedSeatsInfo;
    private ImageView ivMoviePoster;
    private LinearLayout seatContainer;

    private final Set<String> bookedSeats = new HashSet<>();
    private final Set<String> selectedSeats = new LinkedHashSet<>();
    private final Map<String, TextView> seatViews = new HashMap<>();

    private long pricePerSeat;
    private String movieId, movieTitle, movieGenre, movieImageUrl;
    private String theater, showDate, showTime, showtimeId;
    private FirebaseAnalytics mAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        mAnalytics = FirebaseAnalytics.getInstance(this);
        extractIntentData();
        bindViews();
        setupToolbar();
        displayInfo();
        loadBookedSeats();
        findViewById(R.id.btnConfirmBooking).setOnClickListener(v -> confirmBooking());
    }

    private void extractIntentData() {
        movieId = getIntent().getStringExtra(EXTRA_MOVIE_ID);
        movieTitle = getIntent().getStringExtra(EXTRA_MOVIE_TITLE);
        movieGenre = getIntent().getStringExtra(EXTRA_MOVIE_GENRE);
        movieImageUrl = getIntent().getStringExtra(EXTRA_MOVIE_IMAGE);
        showtimeId = getIntent().getStringExtra(EXTRA_SHOWTIME_ID);
        theater = getIntent().getStringExtra(EXTRA_THEATER);
        showDate = getIntent().getStringExtra(EXTRA_DATE);
        showTime = getIntent().getStringExtra(EXTRA_TIME);
        pricePerSeat = getIntent().getLongExtra(EXTRA_PRICE, 90000);
    }

    private void bindViews() {
        tvMovieTitle = findViewById(R.id.tvMovieTitle);
        tvGenre = findViewById(R.id.tvGenre);
        tvTheater = findViewById(R.id.tvTheater);
        tvShowtime = findViewById(R.id.tvShowtime);
        tvTicketPrice = findViewById(R.id.tvTicketPrice);
        tvSeatCountInfo = findViewById(R.id.tvSeatCountInfo);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvSelectedSeatsInfo = findViewById(R.id.tvSelectedSeatsInfo);
        ivMoviePoster = findViewById(R.id.ivMoviePoster);
        seatContainer = findViewById(R.id.seatContainer);
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

    private void displayInfo() {
        tvMovieTitle.setText(movieTitle);
        tvGenre.setText(movieGenre);
        tvTheater.setText("🏢 " + theater);
        tvShowtime.setText("🕐 " + showTime + " - " + showDate);

        Glide.with(this)
            .load(movieImageUrl)
            .centerCrop()
            .placeholder(R.color.background_surface)
            .into(ivMoviePoster);

        updatePriceDisplay();
    }

    private void loadBookedSeats() {
        if (showtimeId == null) {
            buildSeatGrid();
            return;
        }
        FirebaseDatabase.getInstance()
            .getReference("showtimes").child(showtimeId).child("bookedSeats")
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    bookedSeats.clear();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        bookedSeats.add(child.getKey());
                    }
                    buildSeatGrid();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    buildSeatGrid();
                }
            });
    }

    private void buildSeatGrid() {
        seatContainer.removeAllViews();
        seatViews.clear();

        int seatSizePx = dpToPx(34);
        int seatMarginPx = dpToPx(3);
        int aisleMarginPx = dpToPx(14);
        int rowLabelWidthPx = dpToPx(20);
        int rowMarginBottomPx = dpToPx(5);

        for (int r = 0; r < ROWS; r++) {
            LinearLayout row = new LinearLayout(this);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
            rowParams.bottomMargin = rowMarginBottomPx;
            row.setLayoutParams(rowParams);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);

            // Row label (A, B, C...)
            TextView rowLabel = new TextView(this);
            LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                rowLabelWidthPx, seatSizePx);
            rowLabel.setLayoutParams(labelParams);
            rowLabel.setText(ROW_LABELS[r]);
            rowLabel.setTextColor(getResources().getColor(R.color.text_secondary, null));
            rowLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            rowLabel.setGravity(Gravity.CENTER);
            row.addView(rowLabel);

            for (int c = 1; c <= COLS; c++) {
                String seatId = ROW_LABELS[r] + c;

                TextView seat = new TextView(this);
                LinearLayout.LayoutParams seatParams = new LinearLayout.LayoutParams(
                    0, seatSizePx, 1f);
                // Khoảng cách lối đi giữa cột 4 và 5
                seatParams.setMarginStart((c == 5) ? aisleMarginPx : seatMarginPx);
                seat.setLayoutParams(seatParams);
                seat.setText(String.valueOf(c));
                seat.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
                seat.setGravity(Gravity.CENTER);

                if (bookedSeats.contains(seatId)) {
                    seat.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_seat_booked));
                    seat.setTextColor(getResources().getColor(R.color.white, null));
                    seat.setEnabled(false);
                } else {
                    seat.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_seat_empty));
                    seat.setTextColor(getResources().getColor(R.color.text_secondary, null));
                    final String finalSeatId = seatId;
                    seat.setOnClickListener(v -> toggleSeat(finalSeatId, seat));
                }

                seatViews.put(seatId, seat);
                row.addView(seat);
            }

            seatContainer.addView(row);
        }
    }

    private void toggleSeat(String seatId, TextView view) {
        if (selectedSeats.contains(seatId)) {
            selectedSeats.remove(seatId);
            view.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_seat_empty));
            view.setTextColor(getResources().getColor(R.color.text_secondary, null));
        } else {
            if (selectedSeats.size() >= 10) {
                Toast.makeText(this, "Tối đa 10 ghế mỗi lần đặt", Toast.LENGTH_SHORT).show();
                return;
            }
            selectedSeats.add(seatId);
            view.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_seat_selected));
            view.setTextColor(getResources().getColor(R.color.text_dark, null));
        }
        updatePriceDisplay();
    }

    private void updatePriceDisplay() {
        NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        int count = selectedSeats.size();

        tvTicketPrice.setText(fmt.format(pricePerSeat) + " đ");
        tvSeatCountInfo.setText("x" + count);
        tvTotalPrice.setText(fmt.format(pricePerSeat * count) + " đ");

        if (count == 0) {
            tvSelectedSeatsInfo.setText("Chưa chọn ghế nào");
            tvSelectedSeatsInfo.setTextColor(getResources().getColor(R.color.text_hint, null));
        } else {
            tvSelectedSeatsInfo.setText("Ghế đã chọn: " + android.text.TextUtils.join(", ", selectedSeats));
            tvSelectedSeatsInfo.setTextColor(getResources().getColor(R.color.primary, null));
        }
    }

    private void confirmBooking() {
        if (selectedSeats.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ít nhất 1 ghế", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) return;

        findViewById(R.id.btnConfirmBooking).setEnabled(false);

        List<String> seatList = new ArrayList<>(selectedSeats);
        Ticket ticket = new Ticket(
            userId, movieId, movieTitle, movieGenre, movieImageUrl,
            theater, showDate, showTime, seatList, pricePerSeat
        );

        String ticketId = FirebaseDatabase.getInstance().getReference("tickets")
            .child(userId).push().getKey();
        ticket.setId(ticketId);

        FirebaseDatabase.getInstance().getReference("tickets")
            .child(userId).child(ticketId)
            .setValue(ticket)
            .addOnSuccessListener(unused -> {
                markSeatsAsBooked(userId);

                FirebaseDatabase.getInstance().getReference("users").child(userId)
                    .child("totalBookings")
                    .get().addOnSuccessListener(snap -> {
                        int current = snap.getValue(Integer.class) != null
                            ? snap.getValue(Integer.class) : 0;
                        FirebaseDatabase.getInstance().getReference("users")
                            .child(userId).child("totalBookings").setValue(current + 1);
                    });

                FirebaseMessaging.getInstance().subscribeToTopic("all_users");

                ReminderScheduler.schedule(
                    BookingActivity.this, movieTitle, theater, showDate, showTime);

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, movieId);
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movieTitle);
                bundle.putLong(FirebaseAnalytics.Param.VALUE, pricePerSeat * selectedSeats.size());
                bundle.putString(FirebaseAnalytics.Param.CURRENCY, "VND");
                bundle.putInt(FirebaseAnalytics.Param.QUANTITY, selectedSeats.size());
                mAnalytics.logEvent(FirebaseAnalytics.Event.PURCHASE, bundle);

                Toast.makeText(this, "🎉 " + getString(R.string.booking_confirmed), Toast.LENGTH_LONG).show();
                finish();
            })
            .addOnFailureListener(e -> {
                findViewById(R.id.btnConfirmBooking).setEnabled(true);
                Toast.makeText(this, getString(R.string.booking_failed) + ": " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            });
    }

    private void markSeatsAsBooked(String userId) {
        if (showtimeId == null) return;

        Map<String, Object> updates = new HashMap<>();
        for (String seatId : selectedSeats) {
            updates.put(seatId, userId);
        }
        FirebaseDatabase.getInstance()
            .getReference("showtimes").child(showtimeId).child("bookedSeats")
            .updateChildren(updates);

        FirebaseDatabase.getInstance()
            .getReference("showtimes").child(showtimeId).child("availableSeats")
            .get().addOnSuccessListener(snap -> {
                Integer current = snap.getValue(Integer.class);
                if (current != null) {
                    int newValue = Math.max(0, current - selectedSeats.size());
                    FirebaseDatabase.getInstance()
                        .getReference("showtimes").child(showtimeId)
                        .child("availableSeats").setValue(newValue);
                }
            });
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
