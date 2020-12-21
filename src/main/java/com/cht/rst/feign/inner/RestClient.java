package com.cht.rst.feign.inner;

import org.springframework.http.HttpMethod;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * restful client, default by RestTemplate of Spring
 */
public interface RestClient {

    /**
     * the template method to call the final rest invocation
     *
     * @param method
     * @param url
     * @param request
     * @param responseType
     * @param headerParams
     * @param <T>
     * @return
     */
    <T> T doExecute(HttpMethod method, String url, Object request, Type responseType,
                    Map<String, String> headerParams);
}
