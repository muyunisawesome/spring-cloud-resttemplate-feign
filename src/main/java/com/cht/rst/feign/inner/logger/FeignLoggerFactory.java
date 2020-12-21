package com.cht.rst.feign.inner.logger;

public interface FeignLoggerFactory {

    Logger create(Class<?> type);
}
