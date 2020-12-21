package com.cht.rst.feign.inner;

import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.cht.rst.feign.inner.Util.checkNotNull;

public final class MethodMetadata implements Serializable {

    private static final long serialVersionUID = 1L;
    private String configKey;
    private transient Type returnType;
    //requestBody位置
    private Integer bodyIndex;
    //变量位置-变量名
    private final Map<Integer, String> indexToName = new LinkedHashMap<>();
    //uri变量位置
    private final List<Integer> uriVariableIndex = new ArrayList<>();
    //headers
    private final Map<Integer, String> indexToHeaderName = new LinkedHashMap<>();

    private String baseUrl;

    private String urlPart;

    private HttpMethod method;

    MethodMetadata() {
    }

    public String configKey() {
        return configKey;
    }

    public MethodMetadata configKey(String configKey) {
        this.configKey = configKey;
        return this;
    }

    public Type returnType() {
        return returnType;
    }

    public MethodMetadata returnType(Type returnType) {
        this.returnType = returnType;
        return this;
    }

    public Integer bodyIndex() {
        return bodyIndex;
    }

    public MethodMetadata bodyIndex(Integer bodyIndex) {
        this.bodyIndex = bodyIndex;
        return this;
    }

    /**
     * index of parameter to name
     *
     * @return Map of the index of parameter and name
     */
    public Map<Integer, String> indexToName() {
        return indexToName;
    }

    public List<Integer> uriVariableIndex() {
        return uriVariableIndex;
    }

    public Map<Integer, String> indexToHeaderName() {
        return indexToHeaderName;
    }

    public void baseUrl(String baseUrl) {

        /* verify that the target contains the scheme, host and port */
        if (!(StringUtils.hasLength(baseUrl) && baseUrl.startsWith("http"))) {
            throw new IllegalArgumentException("target values must be absolute.");
        }
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        this.baseUrl = baseUrl;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public String getUrlPart() {
        return this.urlPart;
    }

    public HttpMethod getMethod() {
        return this.method;
    }

    public String method() {
        return (method != null) ? method.name() : null;
    }

    public void method(HttpMethod method) {
        checkNotNull(method, "method");
        this.method = method;
    }

    public void setUrlPart(String urlPart) {
        this.urlPart = urlPart;
    }

    public String path() {
        StringBuilder path = new StringBuilder();
        if (this.baseUrl != null) {
            path.append(this.baseUrl);
        }
        if (this.urlPart != null) {
            path.append(this.urlPart);
        }
        if (path.length() == 0) {
            /* no path indicates the root uri */
            path.append("/");
        }
        return path.toString();
    }
}
