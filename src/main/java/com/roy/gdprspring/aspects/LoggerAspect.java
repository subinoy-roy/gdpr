package com.roy.gdprspring.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggerAspect {

    Logger logger = LoggerFactory.getLogger(LoggerAspect.class);

    @Pointcut("@annotation(com.roy.gdprspring.annotations.Logged)")
    public void logged() {}

    @Before("logged()")
    public void loggedPointcut(JoinPoint joinPoint) {
        Arrays.stream(joinPoint.getArgs()).forEach(x->{
            logger.info("{}",x);
        });
    }
}
