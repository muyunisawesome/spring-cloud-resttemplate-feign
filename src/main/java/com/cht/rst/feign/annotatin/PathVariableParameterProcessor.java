package com.cht.rst.feign.annotatin;

import com.cht.rst.feign.AnnotatedParameterProcessor;
import org.springframework.web.bind.annotation.PathVariable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static com.cht.rst.feign.inner.Util.checkState;
import static com.cht.rst.feign.inner.Util.emptyToNull;

public class PathVariableParameterProcessor implements AnnotatedParameterProcessor {

    private static final Class<PathVariable> ANNOTATION = PathVariable.class;

    @Override
    public Class<? extends Annotation> getAnnotationType() {
        return ANNOTATION;
    }

    @Override
    public boolean processArgument(AnnotatedParameterContext context, Annotation annotation, Method method) {

        String name = ANNOTATION.cast(annotation).value();
        checkState(emptyToNull(name) != null,"PathVariable annotation was empty on param %s.", context.getParameterIndex());
        context.setUriVariableIndex();
        return true;
    }
}

