package com.example.prj3.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Showtime {
    private String id;
    private String movieId;
    private String theater;
    private String date;
    private String time;
    private long price;
    private int availableSeats;
    private int totalSeats;

    public Showtime() {}

    public Showtime(String id, String movieId, String theater, String date,
                    String time, long price, int availableSeats, int totalSeats) {
        this.id = id;
        this.movieId = movieId;
        this.theater = theater;
        this.date = date;
        this.time = time;
        this.price = price;
        this.availableSeats = availableSeats;
        this.totalSeats = totalSeats;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getMovieId() { return movieId; }
    public void setMovieId(String movieId) { this.movieId = movieId; }

    public String getTheater() { return theater; }
    public void setTheater(String theater) { this.theater = theater; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public long getPrice() { return price; }
    public void setPrice(long price) { this.price = price; }

    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }

    public String getPriceFormatted() {
        return String.format("%,d đ", price).replace(',', '.');
    }
}
