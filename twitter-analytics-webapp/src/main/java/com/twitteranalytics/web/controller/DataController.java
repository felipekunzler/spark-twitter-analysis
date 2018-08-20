package com.twitteranalytics.web.controller;

import com.twitteranalytics.web.domain.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class DataController {

    @GetMapping("/analytics")
    public Component getData(Principal principal) {
        System.out.println(principal);
        Component c = new Component();
        c.setKeyword("Microsoft");
        return c;
    }

}
