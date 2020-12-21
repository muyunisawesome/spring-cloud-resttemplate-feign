package com.cht.rst.feign;

import com.cht.rst.feign.inner.ChtFeign;
import com.cht.rst.feign.inner.Client;
import com.cht.rst.feign.inner.logger.DefaultFeignLoggerFactory;
import com.cht.rst.feign.inner.logger.DefaultLogger;
import com.cht.rst.feign.inner.logger.FeignLoggerFactory;
import com.cht.rst.feign.inner.logger.Logger;
import com.cht.rst.feign.inner.RestTemplateClient;
import com.cht.rst.feign.inner.Retryer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@Configuration
public class ChtFeignClientsConfiguration {

    private static final AtomicBoolean INITED = new AtomicBoolean(false);

    @Autowired(required = false)
    private ChtFeignConvertFactory<HttpMessageConverters> messageConverters;

    @Autowired(required = false)
    private Logger logger;

    @Bean
    @ConditionalOnMissingBean
    public Retryer feignRetryer() {
        return Retryer.NEVER_RETRY;
    }

    @Bean
    @ConditionalOnMissingBean
    public Logger feignLogger() {
        return new DefaultLogger();
    }

    @Bean
    @ConditionalOnMissingBean(FeignLoggerFactory.class)
    public FeignLoggerFactory feignLoggerFactory() {
        return new DefaultFeignLoggerFactory(logger);
    }

    @Bean
    @ConditionalOnBean(RestTemplate.class)
    public Client chtFeignClient(RestTemplate restTemplate) {
        if (Objects.nonNull(this.messageConverters) && Objects.nonNull(this.messageConverters.getObject())
                && !CollectionUtils.isEmpty(this.messageConverters.getObject().getConverters())) {

            if (INITED.compareAndSet(false, true)) {
                List<HttpMessageConverter<?>> oldMessageConverters = restTemplate.getMessageConverters();
                List<HttpMessageConverter<?>> newMessageConverters =
                        Arrays.asList(new HttpMessageConverter<?>[oldMessageConverters.size()]);
                Collections.copy(newMessageConverters, oldMessageConverters);
                for (HttpMessageConverter<?> messageConverter : this.messageConverters.getObject().getConverters()) {
                    boolean has = false;
                    for (int i = 0; i < oldMessageConverters.size(); i++) {
                        //如果相同类型，则替换第一个
                        if (messageConverter.getClass().equals(oldMessageConverters.get(i).getClass())) {
                            newMessageConverters.set(i, messageConverter);
                            has = true;
                            break;
                        }
                    }
                    //如果不相同，则添加到最后
                    if (!has) {
                        newMessageConverters.add(messageConverter);
                    }
                }
                restTemplate.setMessageConverters(newMessageConverters);
            }
        }
        return new RestTemplateClient(restTemplate);
    }


    @Bean
    @ConditionalOnMissingBean(RestTemplate.class)
    public Client chtFeignClient2() {
        RestTemplate restTemplate = new RestTemplate();
        if (Objects.nonNull(this.messageConverters) && Objects.nonNull(this.messageConverters.getObject())
                && !CollectionUtils.isEmpty(this.messageConverters.getObject().getConverters())) {
            if (INITED.compareAndSet(false, true)) {
                restTemplate.setMessageConverters(this.messageConverters.getObject().getConverters());
            }
        }
        return new RestTemplateClient(restTemplate);
    }

    @Bean
    @Scope("prototype")
    public ChtFeign.Builder feignBuilder(Retryer retryer) {
        return ChtFeign.builder().retryer(retryer);
    }
}
