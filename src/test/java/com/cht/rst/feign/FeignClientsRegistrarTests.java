package com.cht.rst.feign;

import java.util.Collections;

import org.junit.Test;
import org.springframework.mock.env.MockEnvironment;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author cht
 */
public class FeignClientsRegistrarTests {

    @Test
    public void goodName() {
        String name = testGetName("good-name");
        assertThat("name was wrong", name, is("good-name"));
    }

    @Test
    public void goodNameHttpPrefix() {
        String name = testGetName("http://good-name");
        assertThat("name was wrong", name, is("http://good-name"));
    }

    @Test
    public void goodNameHttpsPrefix() {
        String name = testGetName("https://goodname");
        assertThat("name was wrong", name, is("https://goodname"));
    }

    private String testGetName(String name) {
        FeignClientsRegistrar registrar = new FeignClientsRegistrar();
        registrar.setEnvironment(new MockEnvironment());
        return registrar.getName(Collections.<String, Object>singletonMap("name", name));
    }
}
