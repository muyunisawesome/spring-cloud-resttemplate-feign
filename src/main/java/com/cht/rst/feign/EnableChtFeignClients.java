package com.cht.rst.feign;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(FeignClientsRegistrar.class)
public @interface EnableChtFeignClients {

    /**
     * Alias for the {@link #basePackages()} attribute
     */
    String[] value() default {};


    String[] basePackages() default {};


    Class<?>[] defaultConfiguration() default {};
}
