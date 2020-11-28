package com.tracker.covid.models;

public class CommonData {

    private String state;

    private String country;

    private int diffFromPrevDay;

    private String percentDiff;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getDiffFromPrevDay() {
        return diffFromPrevDay;
    }

    public void setDiffFromPrevDay(int diffFromPrevDay) {
        this.diffFromPrevDay = diffFromPrevDay;
    }

    public String getPercentDiff() {
        return percentDiff;
    }

    public void setPercentDiff(String percentDiff) {
        this.percentDiff = percentDiff;
    }
}
