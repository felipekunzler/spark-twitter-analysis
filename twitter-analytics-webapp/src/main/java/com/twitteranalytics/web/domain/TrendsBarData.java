package com.twitteranalytics.web.domain;

import java.util.Map;

public class TrendsBarData {

    private Sentiments sentiments;
    private Map<String, Sentiments> trends;

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
}
