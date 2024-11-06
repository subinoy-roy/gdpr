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

@ControllerAdvice
public class MaskedResponseAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.hasMethodAnnotation(MaskedResponse.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if(body==null){
            return null;
        }

        maskFields(body);
        return body;
    }

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

    private String maskValue(String value) {
        if (value.length() <= 4) {
            return "****";
        }
        return "****" + value.substring(value.length() - 4);
    }
}
