package com.andorid.go_bengkel.model;

public class ReviewModel {
    private String rating;
    private String ulasan;

    public ReviewModel(String rating, String ulasan) {
        this.rating = rating;
        this.ulasan = ulasan;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getUlasan() {
        return ulasan;
    }

    public void setUlasan(String ulasan) {
        this.ulasan = ulasan;
    }
}
