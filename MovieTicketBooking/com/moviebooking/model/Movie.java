package com.moviebooking.model;

/**
 * Represents a movie in the catalogue.
 */
public class Movie {

    private final String movieId;
    private final String title;
    private final String genre;
    private final String language;
    private final int    durationMinutes;

    public Movie(String movieId, String title, String genre,
                 String language, int durationMinutes) {
        this.movieId         = movieId;
        this.title           = title;
        this.genre           = genre;
        this.language        = language;
        this.durationMinutes = durationMinutes;
    }

    // ── Getters ─────────────────────────────────────────────────────────────
    public String getMovieId()         { return movieId; }
    public String getTitle()           { return title; }
    public String getGenre()           { return genre; }
    public String getLanguage()        { return language; }
    public int    getDurationMinutes() { return durationMinutes; }

    @Override
    public String toString() {
        return String.format("Movie{id='%s', title='%s', genre='%s', lang='%s', duration=%dmin}",
                movieId, title, genre, language, durationMinutes);
    }
}
