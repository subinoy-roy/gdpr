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
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@Aspect
@Component
public class EncryptionAspect {

    private static final Logger log = LoggerFactory.getLogger(EncryptionAspect.class);
    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EncryptionAspect(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://encrypo-app:80").build();
    }

    @Pointcut("@annotation(com.roy.gdprspring.annotations.Encrypt)")
    public void encryptPointCut() {}

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
