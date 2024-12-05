package com.roy.gdprspring.advices;

import com.roy.gdprspring.annotations.MaskedField;
import com.roy.gdprspring.annotations.MaskedResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Field;

/**
 * Controller advice to handle responses annotated with @MaskedResponse.
 * This advice masks fields annotated with @MaskedField in the response body.
 */
@ControllerAdvice
public class MaskedResponseAdvice implements ResponseBodyAdvice<Object> {

    /**
     * Checks if the response should be processed by this advice.
     *
     * @param returnType the return type of the controller method
     * @param converterType the selected converter type
     * @return true if the method is annotated with @MaskedResponse, false otherwise
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.hasMethodAnnotation(MaskedResponse.class);
    }

    /**
     * Processes the response body before it is written to the response.
     *
     * @param body the body to be written
     * @param returnType the return type of the controller method
     * @param selectedContentType the selected content type
     * @param selectedConverterType the selected converter type
     * @param request the current request
     * @param response the current response
     * @return the processed body
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if(body == null){
            return null;
        }

        maskFields(body);
        return body;
    }

    /**
     * Masks the fields of the given object that are annotated with @MaskedField.
     *
     * @param body the object whose fields are to be masked
     */
    private void maskFields(Object body) {
        Field[] fields = body.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(MaskedField.class)) {
                field.setAccessible(true);
                try {
                    Object value = field.get(body);
                    if (value != null) {
                        field.set(body, maskValue(value.toString()));
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Masks the given value by replacing all but the last four characters with asterisks.
     *
     * @param value the value to be masked
     * @return the masked value
     */
    private String maskValue(String value) {
        if (value.length() <= 4) {
            return "****";
        }
        return "****" + value.substring(value.length() - 4);
    }
}