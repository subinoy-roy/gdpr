package com.roy.gdprspring.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggerAspect {

    @Pointcut("@annotation(Logged)")
    public void logged() {}

    @Before("logged()")
    public void loggedPointcut(JoinPoint joinPoint) {
        Arrays.stream(joinPoint.getArgs()).forEach((s)->{
            System.out.println(s);
        });
    }
}
