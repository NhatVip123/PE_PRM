package com.example.se1751_net_he163424_tourmgtapp.entity;

public class Tour {
    private String code;
    private String title;
    private double price;
    private int members;

    public Tour(String code, String title, double price, int members) {
        this.code = code;
        this.title = title;
        this.price = price;
        this.members = members;
    }

    public String getCode() { return code; }
    public String getTitle() { return title; }
    public double getPrice() { return price; }
    public int getMembers() { return members; }
}