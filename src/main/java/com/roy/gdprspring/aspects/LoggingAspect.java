package com.roy.gdprspring.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Aspect for logging method execution details.
 * This aspect intercepts methods annotated with @Logged and logs their arguments.
 */
@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    /**
     * Pointcut that matches methods annotated with @Logged.
     */
    @Pointcut("@annotation(com.roy.gdprspring.annotations.Logged)")
    public void logged() {}

    /**
     * Before advice that logs the arguments of methods annotated with @Logged.
     *
     * @param joinPoint the join point representing the method execution
     */
    @Before("logged()")
    public void loggedPointcut(JoinPoint joinPoint) {
        Arrays.stream(joinPoint.getArgs()).forEach(x -> {
            logger.info("{}", x);
        });
    }
}