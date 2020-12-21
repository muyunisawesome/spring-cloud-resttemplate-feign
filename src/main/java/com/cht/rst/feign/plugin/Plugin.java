package com.cht.rst.feign.plugin;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;

public class Plugin implements InvocationHandler {

    private Object target; //被拦截的目标对象

    private ChtFeignInterceptor interceptor; //拦截器

    public Plugin(Object target, ChtFeignInterceptor interceptor) {
        this.target = target;
        this.interceptor = interceptor;
    }

    public static Object wrap(Object target, Collection<ChtFeignInterceptor> interceptors) {
        Class<?> type = target.getClass();
        Class<?>[] interfaces = type.getInterfaces();
            if (interfaces.length > 0) {
            for (ChtFeignInterceptor interceptor : interceptors) {
                target = Proxy.newProxyInstance(type.getClassLoader(), interfaces, new Plugin(target,
                        interceptor));
            }
        }
        return target;
    }

    @Override
    public Object invoke(Object object, Method method, Object[] args) throws Throwable {
        if ("doExecute".equals(method.getName())) {
            return interceptor.intercept(target, method, args);
        }
        return method.invoke(target, args);
    }
}
