package com.cht.rst.feign.bean;

import com.cht.rst.feign.ChtFeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@ChtFeignClient(name = "test-api", url = "http://localhost:8080")
public interface TestClient {

    @RequestMapping(method = RequestMethod.GET, value = "/hello")
    Hello getHello();
}
