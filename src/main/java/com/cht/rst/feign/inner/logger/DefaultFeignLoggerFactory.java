package com.cht.rst.feign.inner.logger;

public class DefaultFeignLoggerFactory implements FeignLoggerFactory {

    private Logger logger;

    public DefaultFeignLoggerFactory(Logger logger) {
        this.logger = logger;
    }

    @Override
    public Logger create(Class<?> type) {
        return this.logger != null ? this.logger : new DefaultLogger(type);
    }

}
