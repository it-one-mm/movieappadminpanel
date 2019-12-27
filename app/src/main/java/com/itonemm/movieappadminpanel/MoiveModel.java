package com.itonemm.movieappadminpanel;

public class MoiveModel {
    String movieName;
    String movieImage;
    String movieVideo;
    String movieCategory;
    String movieSeries;

    public MoiveModel() {
    }

    public MoiveModel(String movieName, String movieImage, String movieVideo, String movieCategory, String movieSeries) {
        this.movieName = movieName;
        this.movieImage = movieImage;
        this.movieVideo = movieVideo;
        this.movieCategory = movieCategory;
        this.movieSeries = movieSeries;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getMovieImage() {
        return movieImage;
    }

    public void setMovieImage(String movieImage) {
        this.movieImage = movieImage;
    }

    public String getMovieVideo() {
        return movieVideo;
    }

    public void setMovieVideo(String movieVideo) {
        this.movieVideo = movieVideo;
    }

    public String getMovieCategory() {
        return movieCategory;
    }

    public void setMovieCategory(String movieCategory) {
        this.movieCategory = movieCategory;
    }

    public String getMovieSeries() {
        return movieSeries;
    }

    public void setMovieSeries(String movieSeries) {
        this.movieSeries = movieSeries;
    }
}
