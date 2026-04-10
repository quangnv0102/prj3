package com.example.prj3.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

@IgnoreExtraProperties
public class Ticket {
    private String id;
    private String userId;
    private String movieId;
    private String movieTitle;
    private String movieGenre;
    private String movieImageUrl;
    private String theater;
    private String showDate;
    private String showTime;
    private List<String> seatNumbers;
    private int seatCount;
    private long pricePerSeat;
    private long totalPrice;
    private String status; // confirmed, pending, cancelled
    private long bookingTime;

    public Ticket() {}

    public Ticket(String userId, String movieId, String movieTitle, String movieGenre,
                  String movieImageUrl, String theater, String showDate, String showTime,
                  List<String> seatNumbers, long pricePerSeat) {
        this.userId = userId;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.movieGenre = movieGenre;
        this.movieImageUrl = movieImageUrl;
        this.theater = theater;
        this.showDate = showDate;
        this.showTime = showTime;
        this.seatNumbers = seatNumbers;
        this.seatCount = seatNumbers.size();
        this.pricePerSeat = pricePerSeat;
        this.totalPrice = seatCount * pricePerSeat;
        this.status = "confirmed";
        this.bookingTime = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getMovieId() { return movieId; }
    public void setMovieId(String movieId) { this.movieId = movieId; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getMovieGenre() { return movieGenre; }
    public void setMovieGenre(String movieGenre) { this.movieGenre = movieGenre; }

    public String getMovieImageUrl() { return movieImageUrl; }
    public void setMovieImageUrl(String movieImageUrl) { this.movieImageUrl = movieImageUrl; }

    public String getTheater() { return theater; }
    public void setTheater(String theater) { this.theater = theater; }

    public String getShowDate() { return showDate; }
    public void setShowDate(String showDate) { this.showDate = showDate; }

    public String getShowTime() { return showTime; }
    public void setShowTime(String showTime) { this.showTime = showTime; }

    public List<String> getSeatNumbers() { return seatNumbers; }
    public void setSeatNumbers(List<String> seatNumbers) { this.seatNumbers = seatNumbers; }

    public int getSeatCount() { return seatCount; }
    public void setSeatCount(int seatCount) { this.seatCount = seatCount; }

    public long getPricePerSeat() { return pricePerSeat; }
    public void setPricePerSeat(long pricePerSeat) { this.pricePerSeat = pricePerSeat; }

    public long getTotalPrice() { return totalPrice; }
    public void setTotalPrice(long totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getBookingTime() { return bookingTime; }
    public void setBookingTime(long bookingTime) { this.bookingTime = bookingTime; }

    public String getTotalPriceFormatted() {
        return String.format("%,d đ", totalPrice).replace(',', '.');
    }
}
