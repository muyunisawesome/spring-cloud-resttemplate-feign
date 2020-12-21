package com.cht.rst.feign.plugin;


import java.lang.reflect.Method;
import java.util.Map;

public interface ChtFeignInterceptor {

    default Object intercept(Object target, Method method, Object[] args) throws Throwable {

        //HttpMethod httpMethod = (HttpMethod) args[0];
        String url = (String) args[1];
        Object requestBody = args[2];
        Map<String, String> headerParams = (Map<String, String>) args[4];

        preProcess(url, requestBody, headerParams);
        Object result = method.invoke(target, args);
        postProcess(url, requestBody, headerParams, result);
        return result;
    }


    default void preProcess(String url, Object requestBody,
                            Map<String, String> headerParams) {
    }

    default void postProcess(String url, Object requestBody,
                             Map<String, String> headerParams, Object result) {
    }
}