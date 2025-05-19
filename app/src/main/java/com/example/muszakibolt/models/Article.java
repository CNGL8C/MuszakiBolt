package com.example.muszakibolt.models;

import java.io.Serializable;

public class Article implements Serializable {
    private String id;
    private String name;
    private String info;
    private String price;
    private float rating;
    private int image;

    public Article(String name, String info, String price, float rating, int image) {
        this.name = name;
        this.info = info;
        this.price = price;
        this.rating = rating;
        this.image = image;
    }

    public Article() {
        this.name = "";
        this.info = "";
        this.price = "";
        this.rating = 0;
        this.image = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
