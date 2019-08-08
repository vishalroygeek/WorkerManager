package com.vishalroy.workermanager.Models;

public class AttendanceRange {

    private long date;

    public AttendanceRange(){}

    public AttendanceRange(long date) {
        this.date = date;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
