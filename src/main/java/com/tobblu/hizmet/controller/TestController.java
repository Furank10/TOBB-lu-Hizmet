package com.tobblu.hizmet.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*") // test
public class TestController {

    @GetMapping("/api/test")
    public String test() {
        return "Backend is alive! 🚀";
    }
}