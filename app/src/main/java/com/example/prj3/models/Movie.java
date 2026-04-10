package com.example.prj3.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Movie {
    private String id;
    private String title;
    private String description;
    private String genre;
    private String language;
    private String imageUrl;
    private double rating;
    private int duration; // phút
    private int year;
    private boolean nowShowing;
    private boolean comingSoon;
    private long releaseDate;

    public Movie() {}

    public Movie(String id, String title, String description, String genre,
                 String language, String imageUrl, double rating,
                 int duration, int year, boolean nowShowing, boolean comingSoon) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.language = language;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.duration = duration;
        this.year = year;
        this.nowShowing = nowShowing;
        this.comingSoon = comingSoon;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public boolean isNowShowing() { return nowShowing; }
    public void setNowShowing(boolean nowShowing) { this.nowShowing = nowShowing; }

    public boolean isComingSoon() { return comingSoon; }
    public void setComingSoon(boolean comingSoon) { this.comingSoon = comingSoon; }

    public long getReleaseDate() { return releaseDate; }
    public void setReleaseDate(long releaseDate) { this.releaseDate = releaseDate; }
}
