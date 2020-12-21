package com.cht.rst.feign;

import com.cht.rst.feign.inner.ChtFeign;
import com.cht.rst.feign.inner.Client;
import com.cht.rst.feign.inner.logger.FeignLoggerFactory;
import com.cht.rst.feign.inner.logger.Logger;
import com.cht.rst.feign.inner.Target;
import com.cht.rst.feign.plugin.ChtFeignInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


public class ChtFeignClientFactoryBean
        implements FactoryBean<Object>, InitializingBean, ApplicationContextAware {

    private Class<?> type;

    private String name;

    private String url;

    private String contextId;

    private ApplicationContext applicationContext;

    @Override
    public Object getObject() {
        return getTarget();
    }

    <T> T getTarget() {
        ChtFeignContext context = this.applicationContext.getBean(ChtFeignContext.class);
        ChtFeign.Builder builder = get(context, ChtFeign.Builder.class);
        //如果有，设置自定义client
        Client client = getOptional(context, Client.class);
        if (client != null) {
            builder.client(client);
        }
        //logger
        FeignLoggerFactory loggerFactory = get(context, FeignLoggerFactory.class);
        Logger logger = loggerFactory.create(this.type);
        builder.logger(logger);

        Map<String, ChtFeignInterceptor> instances = context.getInstances(this.contextId, ChtFeignInterceptor.class);
        if (Objects.nonNull(instances) && !CollectionUtils.isEmpty(instances.values())) {
            Collection<ChtFeignInterceptor> values = instances.values().stream()
                    .sorted((o1, o2) ->
                            Optional.ofNullable(o2.getClass().getAnnotation(Order.class).value()).orElse(0)
                                    - Optional.ofNullable(o1.getClass().getAnnotation(Order.class).value()).orElse(0))
                    .collect(Collectors.toList());
            builder.interceptors(values);
        }

        return (T) new DefaultTargeter().target(this, builder, context, new Target.HardCodedTarget<>(
                this.type, this.name, url));
    }

    protected <T> T getOptional(ChtFeignContext context, Class<T> type) {
        return context.getInstance(this.contextId, type);
    }

    protected <T> T get(ChtFeignContext context, Class<T> type) {
        T instance = context.getInstance(this.contextId, type);
        if (instance == null) {
            throw new IllegalStateException("No bean found of type " + type + " for " + this.contextId);
        }
        return instance;
    }

    @Override
    public Class<?> getObjectType() {
        return this.type;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasText(this.contextId, "Context id must be set");
        Assert.hasText(this.name, "Name must be set");
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.applicationContext = context;
    }
}
