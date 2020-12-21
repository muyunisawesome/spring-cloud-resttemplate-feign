package com.cht.rst.feign.inner.logger;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.cht.rst.feign.inner.logging.Log;
import com.cht.rst.feign.inner.logging.LogFactory;
import org.springframework.http.HttpMethod;

import java.util.Map;

/**
 * DefaultLogger
 */
public class DefaultLogger implements Logger {

    private final Log log;

    public DefaultLogger() {
        this(LogFactory.MARKER);
    }

    public DefaultLogger(String logger) {

        log = LogFactory.getLog(logger);
    }

    public DefaultLogger(Class<?> clazz) {

        log = LogFactory.getLog(clazz);
    }


    @Override
    public void logRequest(String configKey, HttpMethod method, String finalUrl, Object requestBody,
                           Map<String, String> headerParams) {

        if (log.isDebugEnabled()) {
            log.debug(Logger.methodTag(configKey)
                    + String.format("[cht-feign] invocation start url = %s headers = %s requestBoy = %s",
                    finalUrl,
                    headerParams,
                    JSONObject.toJSONString(requestBody)));
        }
    }


    @Override
    public <T> void logResponse(String configKey, HttpMethod method, String finalUrl, Object requestBody,
                                Map<String, String> headerParams, T t, long elapsedTime) {


        if (log.isDebugEnabled()) {
            log.debug(Logger.methodTag(configKey)
                    + String.format("[cht-feign] invocation cost %s ms: result = %s",
                    elapsedTime,
                    JSONObject.toJSONString(t, SerializerFeature.WriteMapNullValue)));
        }
    }
}
