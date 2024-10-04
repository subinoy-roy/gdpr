package com.roy.gdprspring.controllers;

//import com.roy.gdprspring.annotations.Encrypt;
import com.roy.gdprspring.annotations.Logged;
import com.roy.gdprspring.annotations.MaskedResponse;
import com.roy.gdprspring.models.User;
import com.roy.gdprspring.services.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
public class HelloController {
    private static final Logger log = LoggerFactory.getLogger(HelloController.class);

    final
    HelloService helloService;

    public HelloController(HelloService helloService) {
        this.helloService = helloService;
    }

    @Logged
    @MaskedResponse
    @GetMapping("/hello")
    String hello(@RequestBody User user){
        log.info("This is cool");
        return "Hello " + helloService.saveUser(user);
    }
}
