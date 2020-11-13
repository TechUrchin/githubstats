package com.ghstats.reports.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/reports")
public class HelloController {

    @GetMapping(path = "/hello")
    private String sayHello() {
        return "hello friends from reports app yo oy oyoyooyo";
    }
}
