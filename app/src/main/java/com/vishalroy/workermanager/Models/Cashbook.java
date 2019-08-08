package com.vishalroy.workermanager.Models;

public class Cashbook {

    private String name, amount, description;
    private long date;

    public Cashbook(){}

    public Cashbook(String name, String amount, String description, long date) {
        this.name = name;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
