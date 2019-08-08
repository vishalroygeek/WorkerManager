package com.vishalroy.workermanager.Models;

public class DailyPayments {

    private String user_id, amount, name, image, type;
    private long date;

    public DailyPayments(){}

    public DailyPayments(String user_id, String amount, String name, String image, String type, long date) {
        this.user_id = user_id;
        this.amount = amount;
        this.name = name;
        this.image = image;
        this.type = type;
        this.date = date;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
