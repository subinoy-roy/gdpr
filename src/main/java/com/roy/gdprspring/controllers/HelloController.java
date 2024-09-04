package com.roy.gdprspring.controllers;

import com.roy.gdprspring.aspects.Encrypt;
import com.roy.gdprspring.aspects.Logged;
import com.roy.gdprspring.aspects.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
public class HelloController {
    @Logged
    @Encrypt
    @GetMapping("/hello")
    String hello(@RequestBody User user){
        return "Hello " + user.getName();
    }
}
