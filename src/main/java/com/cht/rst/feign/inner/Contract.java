package com.cht.rst.feign.inner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.cht.rst.feign.inner.Util.checkState;

/**
 * Defines what annotations and values are valid on interfaces.
 */
public interface Contract {

    /**
     * Called to parse the methods in the class that are linked to HTTP requests.
     *
     * @param targetType {@link Target#type() type} of the Feign interface.
     * @param baseUrl    {@link Target#url() url} of the Feign interface.
     */
    List<MethodMetadata> parseAndValidateMetadata(Class<?> targetType, String baseUrl);

    abstract class BaseContract implements Contract {

        @Override
        public List<MethodMetadata> parseAndValidateMetadata(Class<?> targetType, String baseUrl) {
            checkState(targetType.getTypeParameters().length == 0, "Parameterized types unsupported: %s",
                    targetType.getSimpleName());
            checkState(targetType.getInterfaces().length <= 1, "Only single inheritance supported: %s",
                    targetType.getSimpleName());
            if (targetType.getInterfaces().length == 1) {
                checkState(targetType.getInterfaces()[0].getInterfaces().length == 0,
                        "Only single-level inheritance supported: %s", targetType.getSimpleName());
            }
            Map<String, MethodMetadata> result = new LinkedHashMap<>();
            //
            for (Method method : targetType.getMethods()) {
                if (method.getDeclaringClass() == Object.class
                        || (method.getModifiers() & Modifier.STATIC) != 0
                        || Util.isDefault(method)) {
                    continue;
                }
                MethodMetadata metadata = parseAndValidateMetadata(targetType, baseUrl, method);
                checkState(!result.containsKey(metadata.configKey()), "Overrides unsupported: %s",
                        metadata.configKey());
                result.put(metadata.configKey(), metadata);
            }
            return new ArrayList<>(result.values());
        }

        /**
         * Called indirectly by {@link #parseAndValidateMetadata(Class, String)}.
         *
         * @param targetType targetType
         * @param baseUrl    baseUrl
         * @param method     method
         */
        protected MethodMetadata parseAndValidateMetadata(Class<?> targetType, String baseUrl, Method method) {

            MethodMetadata data = new MethodMetadata();
            data.returnType(method.getGenericReturnType());
            data.configKey(ChtFeign.configKey(targetType, method));
            data.baseUrl(baseUrl);
            for (Annotation methodAnnotation : method.getAnnotations()) {
                //解析方法上的注解
                processAnnotationOnMethod(data, methodAnnotation, method);
            }
            checkState(data.method() != null,
                    "Method %s not annotated with HTTP method type (ex. GET, POST)",
                    method.getName());
            //参数注解
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            int count = parameterAnnotations.length;
            for (int i = 0; i < count; i++) {
                //是否是http注解而非requestBody
                boolean isHttpAnnotation = false;
                if (parameterAnnotations[i] != null) {
                    //解析参数上的注解
                    isHttpAnnotation = processAnnotationsOnParameter(data, parameterAnnotations[i], i);
                }
                if (!isHttpAnnotation) {
                    //说明是requestBody
                    checkState(data.bodyIndex() == null, "Method has too many Body parameters: %s", method);
                    data.bodyIndex(i);
                }
            }

            return data;
        }

        /**
         * process annotation on method of interface which was annotated with @ChtFeignClient
         *
         * @param data       metadata collected so far relating to the current java method.
         * @param annotation annotations present on the current method annotation.
         * @param method     method currently being processed.
         */
        protected abstract void processAnnotationOnMethod(MethodMetadata data,
                                                          Annotation annotation,
                                                          Method method);

        /**
         * @param data        metadata collected so far relating to the current java method.
         * @param annotations annotations present on the current parameter annotation.
         * @param paramIndex  if you find a name in {@code annotations}, call
         *                    {@link #nameParam(MethodMetadata, String, int)} with this as the last parameter.
         * @return true if you called {@link #nameParam(MethodMetadata, String, int)} after finding an
         * http-relevant annotation.
         */
        protected abstract boolean processAnnotationsOnParameter(MethodMetadata data,
                                                                 Annotation[] annotations,
                                                                 int paramIndex);

        /**
         * links a parameter name to its index in the method signature.
         */
        protected void nameParam(MethodMetadata data, String name, int i) {
            data.indexToName().put(i, name);
        }

        protected void uriVariableIndex(MethodMetadata data, int i) {
            //添加位置
            data.uriVariableIndex().add(i);
        }

        protected void headerNameParam(MethodMetadata data, String name, int i) {
            data.indexToHeaderName().put(i, name);
        }

    }

}
