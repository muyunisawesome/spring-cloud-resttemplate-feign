package com.cht.rst.feign;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ChtFeignClient {

    @AliasFor("name")
    String value() default "";

    @AliasFor("value")
    String name() default "";

    String contextId() default "";

    String url() default "";

    Class<?>[] configuration() default {};
}
