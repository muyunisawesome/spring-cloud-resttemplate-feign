package com.cht.rst.feign;

import org.springframework.beans.BeansException;

@FunctionalInterface
public interface ChtFeignConvertFactory<T> {

    T getObject() throws BeansException;
}
