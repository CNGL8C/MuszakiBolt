package com.example.muszakibolt.models;

public class User {
    private String username;
    private String email;
    private String phone;
    private String address;
    private String accountType;

    public User(String username, String email, String phone, String address, String accountType) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.accountType = accountType;
    }
    public User(){
        this.username = "";
        this.email = "";
        this.phone = "";
        this.address = "";
        this.accountType = "";
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
    @Override
    public String toString() {
        return username+", e-mail:"+email+",telefon: "+phone+" ,cím: " + address + ",fiók típus: " + accountType;
    }
}
