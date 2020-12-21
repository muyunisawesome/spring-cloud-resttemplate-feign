package com.cht.rst.feign;

import org.springframework.cloud.context.named.NamedContextFactory;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author cht
 */
class ChtFeignClientSpecification implements NamedContextFactory.Specification {

    private String name;

    private Class<?>[] configuration;

    public ChtFeignClientSpecification() {
    }

    public ChtFeignClientSpecification(String name, Class<?>[] configuration) {
        this.name = name;
        this.configuration = configuration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?>[] getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Class<?>[] configuration) {
        this.configuration = configuration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChtFeignClientSpecification that = (ChtFeignClientSpecification) o;
        return Objects.equals(name, that.name) &&
                Arrays.equals(configuration, that.configuration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, configuration);
    }

    @Override
    public String toString() {
        return new StringBuilder("FeignClientSpecification{")
                .append("name='").append(name).append("', ")
                .append("configuration=").append(Arrays.toString(configuration))
                .append("}").toString();
    }

}
