package com.cht.rst.feign.bean;

import com.cht.rst.feign.ChtFeignClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, value = {
        "spring.application.name=feignclienttest"})
public class ChtFeignClientTests {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private TestClient testClient;

    @Test
    public void testAnnotations() {
        Map<String, Object> beans = this.context
                .getBeansWithAnnotation(ChtFeignClient.class);
        assertTrue("Wrong clients: " + beans,
                beans.containsKey(TestClient.class.getName()));
    }

    @Test
    public void testClient() {
        assertNotNull("testClient was null", this.testClient);
        assertTrue("testClient is not a java Proxy",
                Proxy.isProxyClass(this.testClient.getClass()));
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(this.testClient);
        assertNotNull("invocationHandler was null", invocationHandler);
    }
}
