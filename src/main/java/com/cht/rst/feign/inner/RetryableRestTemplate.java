package com.cht.rst.feign.inner;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * The finally kernel client to call http invocation
 */
public class RetryableRestTemplate implements RestClient {

    private final RestTemplate restTemplate;

    public RetryableRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public <T> T doExecute(HttpMethod method, String url, Object request, Type responseType,
                           Map<String, String> headerParams) {
        HttpHeaders httpHeaders = new HttpHeaders();
        headerParams.forEach((k, v) -> httpHeaders.add(k, v));
        HttpEntity<Object> httpEntity = new HttpEntity<>(request, httpHeaders);

        ParameterizedTypeReference<T> objectParameterizedTypeReference =
                ParameterizedTypeReference.forType(responseType);

        ResponseEntity<T> result = restTemplate.exchange(url, method, httpEntity, objectParameterizedTypeReference);
        return result.getBody();
    }

}
