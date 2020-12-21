package com.cht.rst.feign.inner;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Controls reflective method dispatch.
 */
public interface InvocationHandlerFactory {

    InvocationHandler create(Target target, Map<Method, MethodHandler> dispatch);

    /**
     * Like {@link InvocationHandler#invoke(Object, Method, Object[])}, except for a
     * single method.
     */
    interface MethodHandler {

        Object invoke(Object[] argv) throws Throwable;
    }

    final class Default implements InvocationHandlerFactory {

        @Override
        public InvocationHandler create(Target target, Map<Method, MethodHandler> dispatch) {
            return new ReflectiveFeign.FeignInvocationHandler(target, dispatch);
        }
    }
}