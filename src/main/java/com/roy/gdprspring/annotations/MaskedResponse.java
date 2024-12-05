package com.roy.gdprspring.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a method whose response should have certain fields masked.
 * Methods annotated with @MaskedResponse will have their response bodies processed
 * to mask fields annotated with @MaskedField.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MaskedResponse {
}