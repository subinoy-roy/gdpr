package com.roy.gdprspring.aspects;

import com.roy.gdprspring.annotations.MaskedField;
import com.roy.gdprspring.annotations.PiiField;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;

@Aspect
@Component
public class ResponseMaskingAspect {
    @Pointcut("@annotation(com.roy.gdprspring.annotations.MaskedResponse)")
    public void maskedResponsePointCut() {}

    @Around("maskedResponsePointCut()")
    public Object maskedResponsePointCut(ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed(Arrays.stream(joinPoint.getArgs()).peek(s -> {
            Class<?> clazz = s.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if(field.isAnnotationPresent(PiiField.class)||field.isAnnotationPresent(MaskedField.class)){
                    field.setAccessible(true);
                    try {
                        field.set(s,"<-- HIDDEN -->");
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).toArray());
    }
}
