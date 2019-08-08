package com.vishalroy.workermanager.Models;

public class Employees {

    private String name, phone, image, employee_type, id, wage;
    private long date_added, last_paid;
    private int advance;

    public Employees(){}

    public Employees(String name, String phone, String image, String employee_type, String id, String wage, long date_added, long last_paid, int advance) {
        this.name = name;
        this.phone = phone;
        this.image = image;
        this.employee_type = employee_type;
        this.id = id;
        this.wage = wage;
        this.date_added = date_added;
        this.last_paid = last_paid;
        this.advance = advance;
    }

    public String getWage() {
        return wage;
    }

    public void setWage(String wage) {
        this.wage = wage;
    }

    public long getAdvance() {
        return advance;
    }

    public void setAdvance(int advance) {
        this.advance = advance;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmployee_type() {
        return employee_type;
    }

    public void setEmployee_type(String employee_type) {
        this.employee_type = employee_type;
    }

    public long getDate_added() {
        return date_added;
    }

    public void setDate_added(long date_added) {
        this.date_added = date_added;
    }

    public long getLast_paid() {
        return last_paid;
    }

    public void setLast_paid(long last_paid) {
        this.last_paid = last_paid;
    }
}
