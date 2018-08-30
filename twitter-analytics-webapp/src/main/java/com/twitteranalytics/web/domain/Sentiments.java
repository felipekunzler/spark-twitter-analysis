package com.twitteranalytics.web.domain;


public class Sentiments {

    private long positive;
    private long negative;
    private long neutral;

    public Sentiments() {
    }

    public Sentiments(long positive, long negative, long neutral) {
        this.positive = positive;
        this.negative = negative;
        this.neutral = neutral;
    }

    public long getPositive() {
        return positive;
    }

    public void setPositive(long positive) {
        this.positive = positive;
    }

    public long getNegative() {
        return negative;
    }

    public void setNegative(long negative) {
        this.negative = negative;
    }

    public long getNeutral() {
        return neutral;
    }

    public void setNeutral(long neutral) {
        this.neutral = neutral;
    }

    @Override
    public String toString() {
        return "Sentiments{" +
                ", positive=" + positive +
                ", negative=" + negative +
                ", neutral=" + neutral +
                '}';
    }
}
