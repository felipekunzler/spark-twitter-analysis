package com.twitteranalytics.web.domain;


import java.time.LocalDate;
import java.util.Map;

public class KeywordAnalysis {

    private LocalDate date;
    private String keyword;
    private Sentiments sentiments;
    private Map<String, Sentiments> trends;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Sentiments getSentiments() {
        return sentiments;
    }

    public void setSentiments(Sentiments sentiments) {
        this.sentiments = sentiments;
    }

    public Map<String, Sentiments> getTrends() {
        return trends;
    }

    public void setTrends(Map<String, Sentiments> trends) {
        this.trends = trends;
    }

    @Override
    public String toString() {
        return "KeywordAnalysis{" +
                "date=" + date +
                ", keyword='" + keyword + '\'' +
                ", sentiments=" + sentiments +
                ", trends=" + trends +
                '}';
    }
}
