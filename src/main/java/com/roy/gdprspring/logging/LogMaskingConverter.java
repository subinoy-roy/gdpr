package com.roy.gdprspring.logging;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.roy.gdprspring.annotations.MaskedField;
import com.roy.gdprspring.annotations.PiiField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * LogMaskingConverter is a custom logback converter that masks fields annotated with @PiiField or @MaskedField.
 */
public class LogMaskingConverter extends ClassicConverter {

    private static final String PII_MASK = "*********";
    private static final Logger log = LoggerFactory.getLogger(LogMaskingConverter.class);
    private final AnnotationUtil annotationUtil = new AnnotationUtil();

    /**
     * Converts the logging event to a string, masking any fields annotated with @PiiField or @MaskedField.
     *
     * @param event the logging event
     * @return the formatted log message with masked fields
     */
    @Override
    public String convert(ILoggingEvent event) {
        String message = event.getFormattedMessage();
        // Check if the message contains an object and mask the annotated fields
        if (event.getArgumentArray() != null && event.getArgumentArray().length > 0) {
            Object[] arguments = event.getArgumentArray();
            Object[] arguments_copy = DeepCopyUtil.deepCopy(event.getArgumentArray());
            if(arguments_copy != null){
                for (Object arg : arguments_copy) {
                    maskFields(arg);
                }
                return Arrays.asList(arguments_copy).toString();
            }
            return Arrays.asList(arguments).toString();
        }
        return message;
    }

    /**
     * Masks the fields of the given object that are annotated with @PiiField or @MaskedField.
     *
     * @param obj the object whose fields are to be masked
     */
    private void maskFields(Object obj) {
        if (obj == null) {
            return;
        }

        Class<?> objClass = obj.getClass();
        Field[] fields = objClass.getDeclaredFields();
        for (Field field : fields) {
            if (annotationUtil.isAnnotationPresent(field, PiiField.class)|| annotationUtil.isAnnotationPresent(field, MaskedField.class)) {
                field.setAccessible(true);  // Make private fields accessible
                try {
                    // Mask the field if it's a string (or adjust for other types)
                    if (field.get(obj) instanceof String) {
                        field.set(obj, PII_MASK);
                    }
                } catch (IllegalAccessException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }
}