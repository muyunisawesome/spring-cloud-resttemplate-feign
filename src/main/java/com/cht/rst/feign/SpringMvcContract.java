package com.cht.rst.feign;

import com.cht.rst.feign.annotatin.PathVariableParameterProcessor;
import com.cht.rst.feign.annotatin.RequestHeaderParameterProcessor;
import com.cht.rst.feign.annotatin.RequestParamParameterProcessor;
import com.cht.rst.feign.inner.ChtFeign;
import com.cht.rst.feign.inner.Contract;
import com.cht.rst.feign.inner.MethodMetadata;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cht.rst.feign.inner.Util.checkState;
import static com.cht.rst.feign.inner.Util.emptyToNull;

/**
 * Base on Spring MVC Annotation's for parse method which with to be called
 *
 * @author cht
 */
public class SpringMvcContract extends Contract.BaseContract implements ResourceLoaderAware {

    private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    private final Map<Class<? extends Annotation>, AnnotatedParameterProcessor> annotatedArgumentProcessors;
    private final Map<String, Method> processedMethods = new HashMap<>();

    private ResourceLoader resourceLoader = new DefaultResourceLoader();

    public SpringMvcContract() {
        this(Collections.emptyList());
    }

    public SpringMvcContract(
            List<AnnotatedParameterProcessor> annotatedParameterProcessors) {
        this(annotatedParameterProcessors, new DefaultConversionService());
    }

    public SpringMvcContract(
            List<AnnotatedParameterProcessor> annotatedParameterProcessors,
            ConversionService conversionService) {
        Assert.notNull(annotatedParameterProcessors,
                "Parameter processors can not be null.");
        Assert.notNull(conversionService, "ConversionService can not be null.");

        List<AnnotatedParameterProcessor> processors = getDefaultAnnotatedArgumentsProcessors();

        this.annotatedArgumentProcessors = toAnnotatedArgumentProcessorMap(processors);
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public MethodMetadata parseAndValidateMetadata(Class<?> targetType, String baseUrl, Method method) {

        this.processedMethods.put(ChtFeign.configKey(targetType, method), method);
        return super.parseAndValidateMetadata(targetType, baseUrl, method);
    }

    @Override
    protected void processAnnotationOnMethod(MethodMetadata data, Annotation methodAnnotation, Method method) {
        if (!RequestMapping.class.isInstance(methodAnnotation) && !methodAnnotation
                .annotationType().isAnnotationPresent(RequestMapping.class)) {
            return;
        }

        RequestMapping methodMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
        // HTTP Method
        RequestMethod[] methods = methodMapping.method();
        if (methods.length == 0) {
            methods = new RequestMethod[]{RequestMethod.GET};
        }
        checkOne(method, methods, "method");
        data.method(HttpMethod.valueOf(methods[0].name()));

        // path
        checkAtMostOne(method, methodMapping.value(), "value");
        if (methodMapping.value().length > 0) {
            String pathValue = emptyToNull(methodMapping.value()[0]);
            if (pathValue != null) {
                //解析url，使用spring环境中的变量
                pathValue = resolve(pathValue);
                // Append path from @RequestMapping if value is present on method
                if (!pathValue.startsWith("/") && !data.path().endsWith("/")) {
                    pathValue = "/" + pathValue;
                }
                //设置后url
                data.setUrlPart(pathValue);
            }
        }
    }

    private String resolve(String value) {
        if (StringUtils.hasText(value)
                && this.resourceLoader instanceof ConfigurableApplicationContext) {
            return ((ConfigurableApplicationContext) this.resourceLoader).getEnvironment()
                    .resolvePlaceholders(value);
        }
        return value;
    }

    private void checkAtMostOne(Method method, Object[] values, String fieldName) {
        checkState(values != null && (values.length == 0 || values.length == 1),
                "Method %s can only contain at most 1 %s field. Found: %s",
                method.getName(), fieldName,
                values == null ? null : Arrays.asList(values));
    }

    private void checkOne(Method method, Object[] values, String fieldName) {
        checkState(values != null && values.length == 1,
                "Method %s can only contain 1 %s field. Found: %s", method.getName(),
                fieldName, values == null ? null : Arrays.asList(values));
    }

    @Override
    protected boolean processAnnotationsOnParameter(MethodMetadata data,
                                                    Annotation[] annotations, int paramIndex) {
        boolean isHttpAnnotation = false;

        AnnotatedParameterProcessor.AnnotatedParameterContext context =
                new SpringMvcContract.SimpleAnnotatedParameterContext(data, paramIndex);
        Method method = this.processedMethods.get(data.configKey());
        for (Annotation parameterAnnotation : annotations) {
            //策略模式
            AnnotatedParameterProcessor processor =
                    this.annotatedArgumentProcessors.get(parameterAnnotation.annotationType());
            if (processor != null) {
                Annotation processParameterAnnotation;
                // synthesize, handling @AliasFor, while falling back to parameter name on
                // missing String #value():
                processParameterAnnotation = synthesizeWithMethodParameterNameAsFallbackValue(
                        parameterAnnotation, method, paramIndex);
                //use processor to parse argument
                isHttpAnnotation |= processor.processArgument(context, processParameterAnnotation, method);
            }
        }
        return isHttpAnnotation;
    }

    private Map<Class<? extends Annotation>, AnnotatedParameterProcessor> toAnnotatedArgumentProcessorMap(
            List<AnnotatedParameterProcessor> processors) {
        Map<Class<? extends Annotation>, AnnotatedParameterProcessor> result = new HashMap<>();
        for (AnnotatedParameterProcessor processor : processors) {
            result.put(processor.getAnnotationType(), processor);
        }
        return result;
    }

    private List<AnnotatedParameterProcessor> getDefaultAnnotatedArgumentsProcessors() {

        List<AnnotatedParameterProcessor> annotatedArgumentResolvers = new ArrayList<>();

        annotatedArgumentResolvers.add(new PathVariableParameterProcessor());
        annotatedArgumentResolvers.add(new RequestParamParameterProcessor());
        annotatedArgumentResolvers.add(new RequestHeaderParameterProcessor());

        return annotatedArgumentResolvers;
    }

    private Annotation synthesizeWithMethodParameterNameAsFallbackValue(
            Annotation parameterAnnotation, Method method, int parameterIndex) {
        Map<String, Object> annotationAttributes = AnnotationUtils.getAnnotationAttributes(parameterAnnotation);
        Object defaultValue = AnnotationUtils.getDefaultValue(parameterAnnotation);
        if (defaultValue instanceof String && defaultValue.equals(annotationAttributes.get(AnnotationUtils.VALUE))) {
            Type[] parameterTypes = method.getGenericParameterTypes();
            String[] parameterNames = PARAMETER_NAME_DISCOVERER.getParameterNames(method);
            if (shouldAddParameterName(parameterIndex, parameterTypes, parameterNames)) {
                annotationAttributes.put(AnnotationUtils.VALUE, parameterNames[parameterIndex]);
            }
        }
        return AnnotationUtils.synthesizeAnnotation(annotationAttributes, parameterAnnotation.annotationType(), null);
    }

    private boolean shouldAddParameterName(int parameterIndex, Type[] parameterTypes, String[] parameterNames) {
        // has a parameter name
        return parameterNames != null && parameterNames.length > parameterIndex
                // has a type
                && parameterTypes != null && parameterTypes.length > parameterIndex;
    }

    private class SimpleAnnotatedParameterContext implements AnnotatedParameterProcessor.AnnotatedParameterContext {

        private final MethodMetadata methodMetadata;

        private final int parameterIndex;

        public SimpleAnnotatedParameterContext(MethodMetadata methodMetadata, int parameterIndex) {
            this.methodMetadata = methodMetadata;
            this.parameterIndex = parameterIndex;
        }

        @Override
        public MethodMetadata getMethodMetadata() {
            return this.methodMetadata;
        }

        @Override
        public int getParameterIndex() {
            return this.parameterIndex;
        }

        @Override
        public void setParameterName(String name) {
            nameParam(this.methodMetadata, name, this.parameterIndex);
        }

        @Override
        public void setUriVariableIndex() {
            uriVariableIndex(this.methodMetadata, this.parameterIndex);
        }

        @Override
        public void setHeaderName(String name) {
            headerNameParam(this.methodMetadata, name, this.parameterIndex);
        }
    }
}
