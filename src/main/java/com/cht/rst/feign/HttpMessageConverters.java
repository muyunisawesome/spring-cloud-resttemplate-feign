package com.cht.rst.feign;

import org.springframework.http.converter.HttpMessageConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Provider the capacity to custom HttpMessageConverter by user's app
 */
public class HttpMessageConverters {

    private final List<HttpMessageConverter<?>> converters;


    public HttpMessageConverters(HttpMessageConverter<?>... additionalConverters) {
        this(Arrays.asList(additionalConverters));
    }

    public HttpMessageConverters(Collection<HttpMessageConverter<?>> additionalConverters) {
        this.converters = new ArrayList<>(additionalConverters);
    }

    public List<HttpMessageConverter<?>> getConverters() {
        return this.converters;
    }
}
