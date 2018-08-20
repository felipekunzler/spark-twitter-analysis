package com.twitteranalytics.web.domain;


import org.springframework.data.annotation.Id;

import java.time.LocalDate;

public class Component {

    @Id
    private String id;
    private String keyword;
    private LocalDate from;
    private LocalDate to;
    private String type;
    private String user;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public LocalDate getFrom() {
        return from;
    }

    public void setFrom(LocalDate from) {
        this.from = from;
    }

    public LocalDate getTo() {
        return to;
    }

    public void setTo(LocalDate to) {
        this.to = to;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Component{" +
                "id='" + id + '\'' +
                ", keyword='" + keyword + '\'' +
                ", from=" + from +
                ", to=" + to +
                ", type='" + type + '\'' +
                ", user='" + user + '\'' +
                '}';
    }
}
