package com.example.proxy;

import com.example.annotation.RequestBody;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DynamicProxyHandler implements InvocationHandler  {

    private final String requestBody;

    public DynamicProxyHandler(String requestBody) {
        this.requestBody = requestBody;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
        
        //메소드에 있는 애노테이션 전부 검사
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        
        Class<?>[] parameterTypes = method.getParameterTypes();

        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {

                if (annotation instanceof RequestBody) {
                    return parseStringToClass(requestBody, parameterTypes[i]);
                }

            }
        }

        return null;
    }


    private <T> T parseStringToClass(String requestBody, Class<T> clazz) throws Exception {
        try {

            Pattern pattern = Pattern.compile("\"([^\"]*)\"\\s*:\\s*\"([^\"]*)\"");
            Matcher matcher = pattern.matcher(requestBody);

            T instance = clazz.getDeclaredConstructor().newInstance();

            while (matcher.find()) {
                String property = matcher.group(1);
                String value = matcher.group(2);
                Field field = clazz.getDeclaredField(property);
                field.setAccessible(true);
                field.set(instance, value);
            }

            return instance;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }
}
