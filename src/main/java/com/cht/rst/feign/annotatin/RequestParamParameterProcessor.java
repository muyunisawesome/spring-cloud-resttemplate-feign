package com.cht.rst.feign.annotatin;

import com.cht.rst.feign.AnnotatedParameterProcessor;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static com.cht.rst.feign.inner.Util.checkState;
import static com.cht.rst.feign.inner.Util.emptyToNull;

public class RequestParamParameterProcessor implements AnnotatedParameterProcessor {

    private static final Class<RequestParam> ANNOTATION = RequestParam.class;

    @Override
    public Class<? extends Annotation> getAnnotationType() {
        return ANNOTATION;
    }

    @Override
    public boolean processArgument(AnnotatedParameterContext context, Annotation annotation, Method method) {

        RequestParam requestParam = ANNOTATION.cast(annotation);
        String name = requestParam.value();
        checkState(emptyToNull(name) != null, "RequestParam.value() was empty on parameter %s", context.getParameterIndex());
        context.setParameterName(name);
        return true;
    }

}
