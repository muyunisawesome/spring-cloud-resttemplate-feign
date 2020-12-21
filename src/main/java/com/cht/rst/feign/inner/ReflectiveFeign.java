package com.cht.rst.feign.inner;

import com.cht.rst.feign.inner.InvocationHandlerFactory.MethodHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.cht.rst.feign.inner.Util.checkNotNull;

public class ReflectiveFeign extends ChtFeign {

    private final ParseHandlersByName targetToHandlersByName;
    private final InvocationHandlerFactory factory;

    ReflectiveFeign(ParseHandlersByName targetToHandlersByName, InvocationHandlerFactory factory) {
        this.targetToHandlersByName = targetToHandlersByName;
        this.factory = factory;
    }

    /**
     * creates an api binding to the {@code target}. As this invokes reflection, care should be taken
     * to cache the result.
     */
    @Override
    public <T> T newInstance(Target<T> target) {
        Map<String, MethodHandler> nameToHandler = targetToHandlersByName.apply(target);
        Map<Method, MethodHandler> methodToHandler = new LinkedHashMap<>();
        List<DefaultMethodHandler> defaultMethodHandlers = new LinkedList<>();

        for (Method method : target.type().getMethods()) {
            if (method.getDeclaringClass() == Object.class) {
                continue;
            } else if (Util.isDefault(method)) {
                DefaultMethodHandler handler = new DefaultMethodHandler(method);
                defaultMethodHandlers.add(handler);
                methodToHandler.put(method, handler);
            } else {
                methodToHandler.put(method, nameToHandler.get(ChtFeign.configKey(target.type(), method)));
            }
        }
        InvocationHandler handler = factory.create(target, methodToHandler);
        T proxy = (T) Proxy.newProxyInstance(target.type().getClassLoader(), new Class<?>[]{target.type()}, handler);

        for (DefaultMethodHandler defaultMethodHandler : defaultMethodHandlers) {
            defaultMethodHandler.bindTo(proxy);
        }
        return proxy;
    }

    static class FeignInvocationHandler implements InvocationHandler {

        private final Target target;
        private final Map<Method, InvocationHandlerFactory.MethodHandler> dispatch;

        FeignInvocationHandler(Target target, Map<Method, InvocationHandlerFactory.MethodHandler> dispatch) {
            this.target = checkNotNull(target, "target");
            this.dispatch = checkNotNull(dispatch, "dispatch for %s", target);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("equals".equals(method.getName())) {
                try {
                    Object otherHandler = args.length > 0 && args[0] != null
                            ? Proxy.getInvocationHandler(args[0]) : null;
                    return equals(otherHandler);
                } catch (IllegalArgumentException e) {
                    return false;
                }
            } else if ("hashCode".equals(method.getName())) {
                return hashCode();
            } else if ("toString".equals(method.getName())) {
                return toString();
            }

            return dispatch.get(method).invoke(args);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof FeignInvocationHandler) {
                FeignInvocationHandler other = (FeignInvocationHandler) obj;
                return target.equals(other.target);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return target.hashCode();
        }

        @Override
        public String toString() {
            return target.toString();
        }
    }

    static final class ParseHandlersByName {

        private final Contract contract;
        private final SynchronousMethodHandler.Factory factory;

        ParseHandlersByName(Contract contract, SynchronousMethodHandler.Factory factory) {
            this.contract = contract;
            this.factory = factory;
        }

        public Map<String, MethodHandler> apply(Target target) {
            //解析方法注解信息
            List<MethodMetadata> metadata = contract.parseAndValidateMetadata(target.type(), target.url());
            Map<String, MethodHandler> result = new LinkedHashMap<>();
            for (MethodMetadata md : metadata) {
                result.put(md.configKey(), factory.create(target, md));
            }
            return result;
        }
    }
}