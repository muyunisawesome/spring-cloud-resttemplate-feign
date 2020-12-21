package com.cht.rst.feign.inner.logger;

import org.springframework.http.HttpMethod;

import java.util.Map;

/**
 * the logger control of cht-feign
 */
public interface Logger {

    static String methodTag(String configKey) {
        return new StringBuilder().append('[').append(configKey, 0, configKey.indexOf('('))
                .append("] ").toString();
    }

    default void logRequest(String configKey, HttpMethod method, String finalUrl, Object requestBody,
                            Map<String, String> headerParams) {

    }

    default <T> void logResponse(String configKey, HttpMethod method, String finalUrl, Object requestBody,
                                 Map<String, String> headerParams, T t, long elapsedTime) {

    }

    enum Level {
        DEBUG,
        INFO,
        WARN,
        ERROR
    }
}
