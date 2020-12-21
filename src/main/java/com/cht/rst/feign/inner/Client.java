package com.cht.rst.feign.inner;

import com.cht.rst.feign.inner.logger.Logger;
import com.cht.rst.feign.plugin.ChtFeignInterceptor;

import java.io.IOException;
import java.util.Collection;

/**
 * This define the client of cht-feign
 */
public interface Client {

    /**
     * add some custom interceptor to feign, such as print the log of invocation,
     * dynamically modify the request info, etc.
     *
     * @param interceptors the interceptors which will be add
     */
    void addInterceptors(Collection<ChtFeignInterceptor> interceptors);

    /**
     * execute the http invocation
     *
     * @param request the metadata of the http request
     * @param argv    the input arguments make as the arguments of business method
     * @param logger  this logger will be used to record the log before and after the invocation
     * @param <T>     represent the return type
     * @return the object which  represented by <T>
     * @throws IOException
     */
    <T> T execute(MethodMetadata request, Object[] argv, Logger logger) throws IOException;
}
