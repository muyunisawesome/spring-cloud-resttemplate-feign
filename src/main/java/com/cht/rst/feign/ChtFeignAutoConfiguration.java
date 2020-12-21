package com.cht.rst.feign;

import com.cht.rst.feign.inner.ChtFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConditionalOnClass(ChtFeign.class)
public class ChtFeignAutoConfiguration {

    @Autowired(required = false)
    private List<ChtFeignClientSpecification> configurations = new ArrayList<>();


    @Bean
    public ChtFeignContext ChtFeignContext() {
        ChtFeignContext context = new ChtFeignContext();
        context.setConfigurations(this.configurations);
        return context;
    }
}
