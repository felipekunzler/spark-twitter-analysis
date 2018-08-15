package com.twitteranalytics.web;

import com.twitteranalytics.web.domain.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private ComponentRepository repository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        repository.deleteAll();
        repository.save(new Component("a", "ca"));
        repository.save(new Component("b", "cb"));

        System.out.println("Component found with findAll():");
        System.out.println("-------------------------------");
        for (Component component : repository.findAll()) {
            System.out.println(component);
        }
        System.out.println();
    }

}
