package com.roy.gdprspring.controllers;

import com.roy.gdprspring.annotations.Logged;
import com.roy.gdprspring.annotations.MaskedResponse;
import com.roy.gdprspring.dtos.UserDto;
import com.roy.gdprspring.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController("/")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    final
    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Logged
    @GetMapping("/hello")
    String hello(){
        return "Hello";
    }

    @Logged
    @MaskedResponse
    @PostMapping("/user")
    UserDto createUser(@RequestBody UserDto userDto){
        log.info("This is cool");
        return userService.saveUser(userDto);
    }

    @Logged
    @MaskedResponse
    @GetMapping("/user/{userId}")
    UserDto getUser(@PathVariable("userId") String userId){
        return userService.getUserById(Long.parseLong(userId));
    }
}
