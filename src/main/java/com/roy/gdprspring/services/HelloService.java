package com.roy.gdprspring.services;

import com.roy.gdprspring.annotations.Encrypt;
import com.roy.gdprspring.models.User;
import org.springframework.stereotype.Service;

@Service
public class HelloService {
    @Encrypt
    public User saveUser(User user) {
        return user;
    }
}
