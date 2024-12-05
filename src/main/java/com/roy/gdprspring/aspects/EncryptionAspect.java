package com.roy.gdprspring.aspects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roy.gdprspring.annotations.PiiField;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

/**
 * Aspect for handling encryption of fields annotated with @PiiField.
 * This aspect intercepts methods annotated with @Encrypt and processes
 * the fields to encrypt their values.
 */
@Aspect
@Component
public class EncryptionAspect {
    private static final Logger log = LoggerFactory.getLogger(EncryptionAspect.class);
    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Constructor for EncryptionAspect.
     * Initializes the WebClient with the base URL for the encryption service.
     *
     * @param webClientBuilder the WebClient.Builder to build the WebClient instance
     */
    public EncryptionAspect(WebClient.Builder webClientBuilder, @Value("${encryption.service.url}") String encryptionServiceUrl) {
        System.out.println("EncryptionAspect constructor: "+ encryptionServiceUrl);
        this.webClient = webClientBuilder.baseUrl(encryptionServiceUrl).build();
    }

    /**
     * Pointcut that matches methods annotated with @Encrypt.
     */
    @Pointcut("@annotation(com.roy.gdprspring.annotations.Encrypt)")
    public void encryptPointCut() {}

    /**
     * Around advice that intercepts the method execution and processes the arguments
     * to encrypt fields annotated with @PiiField.
     *
     * @param joinPoint the join point representing the method execution
     * @return the result of the method execution with encrypted fields
     * @throws Throwable if any error occurs during method execution or encryption
     */
    @Around("encryptPointCut()")
    public Object encryptAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Object[] mod_args = Arrays.stream(args).peek(s -> {
            Class<?> clazz = s.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if(field.isAnnotationPresent(PiiField.class)){
                    field.setAccessible(true);
                    try {
                        Payload payload = new Payload();
                        payload.setPayload(field.get(s)+"");
                        String response = webClient.post()
                                .uri("/encrypt")
                                .bodyValue(payload)
                                .retrieve()
                                .bodyToMono(String.class)
                                .block(); // Use block() for synchronous, avoid in reactive
                        log.info(response);
                        Payload retPayload = objectMapper.readValue(response, Payload.class);
                        field.set(s,retPayload.getPayload());
                    } catch (IllegalAccessException | JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).toArray();
        return joinPoint.proceed(mod_args);
    }
}