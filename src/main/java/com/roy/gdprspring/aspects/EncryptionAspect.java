package com.roy.gdprspring.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
public class EncryptionAspect {
    @Pointcut("@annotation(com.roy.gdprspring.aspects.Encrypt)")
    public void encryptPointCut() {}

    @Around("encryptPointCut()")
    public Object encryptAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Object[] mod_args = Arrays.stream(args).map(s -> {
            Class clazz = s.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if(field.isAnnotationPresent(Pii.class)){
                    field.setAccessible(true);
                    try {
                        field.set(s,field.get(s)+"_ENCRYPTED");
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            return s;
        }).toArray();
        return joinPoint.proceed(mod_args);
    }
}
