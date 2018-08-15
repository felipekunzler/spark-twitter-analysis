package com.twitteranalytics.web.domain;


import org.springframework.data.annotation.Id;

public class Component {

    @Id
    public String id;
    public String name;

    public Component() {

    }

    public Component(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Component{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
