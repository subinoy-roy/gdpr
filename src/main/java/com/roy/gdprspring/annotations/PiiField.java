package com.roy.gdprspring.annotations;

import java.lang.annotation.*;

/**
 * Annotation to mark a field as containing Personally Identifiable Information (PII).
 * Fields annotated with @PiiField will be treated as sensitive information.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PiiField {
}