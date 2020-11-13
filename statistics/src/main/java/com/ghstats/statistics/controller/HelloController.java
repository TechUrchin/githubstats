package com.ghstats.statistics.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/stats")

public class HelloController {
    @GetMapping(path = "/hello")
    private String sayHello() {
        return "passive aggressive hello for jay from statistics app *angrykissyface*";
    }
}