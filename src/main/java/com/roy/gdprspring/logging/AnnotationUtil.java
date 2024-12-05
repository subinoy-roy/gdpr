package com.roy.gdprspring.logging;

import org.springframework.aop.framework.AopProxyUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Utility class for handling annotations.
 */
public class AnnotationUtil {

    /**
     * Default constructor for AnnotationUtil.
     */
    public AnnotationUtil() {
    }

    /**
     * Checks if a specific annotation is present on a given field.
     *
     * @param field the field to check for the annotation
     * @param clazz the class of the annotation to look for
     * @return true if the annotation is present, false otherwise
     */
    boolean isAnnotationPresent(Field field, Class<?> clazz) {
        // This section of code is kept for future reference
        // Arrays.stream(fields).forEach(field -> {
        //     Arrays.stream(field.getDeclaredAnnotations()).forEach(annotation -> {
        //         Class<?> try1 = AopUtils.getTargetClass(annotation);
        //         System.out.println("AopUtils.getTargetClass : " + try1);
        //         Class<?> try2 = ClassUtils.getUserClass(annotation);
        //         System.out.println("ClassUtils.getUserClass : " + try2);
        //         Class<?> try3 = AopProxyUtils.ultimateTargetClass(annotation);
        //         System.out.println("AopProxyUtils.ultimateTargetClass : " + try3);
        //         Class<?> finalTry = AopProxyUtils.proxiedUserInterfaces(annotation)[0];
        //         System.out.println ("AopProxyUtils.proxiedUserInterfaces[0] : " + finalTry);
        //     });
        // });
        AtomicBoolean retValue = new AtomicBoolean(false);
        Arrays.stream(field.getDeclaredAnnotations())
                .forEach(annotation -> retValue.set(Arrays.stream(
                        AopProxyUtils.proxiedUserInterfaces(annotation))
                        .map(Class::getName)
                .toList()
                .contains(clazz.getName())));
        return retValue.get();
    }
}