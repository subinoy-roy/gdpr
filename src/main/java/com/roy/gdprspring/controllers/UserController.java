package com.roy.gdprspring.controllers;

import com.roy.gdprspring.annotations.Logged;
import com.roy.gdprspring.annotations.MaskedResponse;
import com.roy.gdprspring.dtos.UserDto;
import com.roy.gdprspring.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing users.
 */
@RestController("/")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    final UserService userService;

    /**
     * Constructor for UserController.
     *
     * @param userService the user service to be used by this controller
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint to return a simple greeting message.
     *
     * @return a greeting message "Hello"
     */
    @Logged
    @GetMapping("/hello")
    String hello() {
        return "Hello";
    }

    /**
     * Endpoint to create a new user.
     *
     * @param userDto the user data transfer object containing user details
     * @return the created user data transfer object
     */
    @Logged
    @MaskedResponse
    @PostMapping("/user")
    UserDto createUser(@RequestBody UserDto userDto) {
        log.info("This is cool");
        return userService.saveUser(userDto);
    }

    /**
     * Endpoint to get a user by their ID.
     *
     * @param userId the ID of the user to retrieve
     * @return the user data transfer object of the retrieved user
     */
    @Logged
    @MaskedResponse
    @GetMapping("/user/{userId}")
    UserDto getUser(@PathVariable("userId") String userId) {
        return userService.getUserById(Long.parseLong(userId));
    }
}