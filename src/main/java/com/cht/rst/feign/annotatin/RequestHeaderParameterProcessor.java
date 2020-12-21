package com.cht.rst.feign.annotatin;

import com.cht.rst.feign.AnnotatedParameterProcessor;
import org.springframework.web.bind.annotation.RequestHeader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static com.cht.rst.feign.inner.Util.checkState;
import static com.cht.rst.feign.inner.Util.emptyToNull;

public class RequestHeaderParameterProcessor implements AnnotatedParameterProcessor {

    private static final Class<RequestHeader> ANNOTATION = RequestHeader.class;

    @Override
    public Class<? extends Annotation> getAnnotationType() {
        return ANNOTATION;
    }

    @Override
    public boolean processArgument(AnnotatedParameterContext context,
                                   Annotation annotation, Method method) {
        String name = ANNOTATION.cast(annotation).value();
        checkState(emptyToNull(name) != null, "RequestHeader.value() was empty on parameter %s", context.getParameterIndex());
        context.setHeaderName(name);
        return true;
    }

}