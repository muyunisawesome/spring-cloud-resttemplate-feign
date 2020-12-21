package com.cht.rst.feign;

import com.cht.rst.feign.inner.MethodMetadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Feign contract method parameter processor.
 *
 * @author cht
 */
public interface AnnotatedParameterProcessor {

    /**
     * Retrieves the processor supported annotation type.
     *
     * @return the annotation type
     */
    Class<? extends Annotation> getAnnotationType();

    /**
     * Process the annotated parameter.
     *
     * @param context    the parameter context
     * @param annotation the annotation instance
     * @param method     the method that contains the annotation
     * @return whether the parameter is http
     */
    boolean processArgument(AnnotatedParameterContext context, Annotation annotation, Method method);

    /**
     * Specifies the parameter context.
     *
     * @author cht
     */
    interface AnnotatedParameterContext {

        /**
         * Retrieves the method metadata.
         *
         * @return the method metadata
         */
        MethodMetadata getMethodMetadata();

        /**
         * Retrieves the index of the parameter.
         *
         * @return the parameter index
         */
        int getParameterIndex();

        /**
         * Sets the parameter name.
         *
         * @param name the name of the parameter
         */
        void setParameterName(String name);

        void setUriVariableIndex();

        void setHeaderName(String name);
    }
}
