package com.example.muszakibolt.models;

import java.io.Serializable;
import java.util.LinkedList;

public class User implements Serializable {
    private String username;
    private String email;
    private String phone;
    private String address;
    private String accountType;
    private final LinkedList<Article> cart;

    public User(String username, String email, String phone, String address, String accountType) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.accountType = accountType;
        this.cart = new LinkedList<>();
    }
    public User(){
        this.username = "";
        this.email = "";
        this.phone = "";
        this.address = "";
        this.accountType = "";
        this.cart = new LinkedList<>();
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getAccountType() {
        return accountType;
    }
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
    public LinkedList<Article> getCart() {
        return cart;
    }
    @Override
    public String toString() {
        return username+", e-mail:"+email+",telefon: "+phone+" ,cím: " + address + ",fiók típus: " + accountType;
    }
}
