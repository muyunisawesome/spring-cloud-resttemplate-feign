package com.cht.rst.feign.bean;

import com.cht.rst.feign.EnableChtFeignClients;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@EnableAutoConfiguration
@RestController
@EnableChtFeignClients
public class Application {

    @RequestMapping(method = RequestMethod.GET, value = "/hello")
    public Hello getHello() {
        return new Hello("hello world 1");
    }
}
