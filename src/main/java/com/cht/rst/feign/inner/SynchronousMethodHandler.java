package com.cht.rst.feign.inner;

import com.cht.rst.feign.inner.logger.Logger;
import com.cht.rst.feign.plugin.ChtFeignInterceptor;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

import static com.cht.rst.feign.inner.Util.checkNotNull;

public class SynchronousMethodHandler implements InvocationHandlerFactory.MethodHandler {

    private final MethodMetadata metadata;
    private final Client client;
    private final Target<?> target;
    private final Retryer retryer;
    private final Logger logger;

    private SynchronousMethodHandler(Target<?> target,
                                     Client client,
                                     Retryer retryer,
                                     Logger logger,
                                     MethodMetadata metadata) {

        this.target = checkNotNull(target, "target");
        this.client = checkNotNull(client, "client for %s", target);
        this.retryer = checkNotNull(retryer, "retryer for %s", target);
        this.logger = checkNotNull(logger, "logger for %s", target);
        this.metadata = checkNotNull(metadata, "metadata for %s", target);

    }


    @Override
    public Object invoke(Object[] argv) throws Throwable {
        return client.execute(metadata, argv, logger);
    }


    static class Factory {

        private final Client client;
        private final Retryer retryer;
        private final Logger logger;

        Factory(Client client, Collection<ChtFeignInterceptor> interceptors, Retryer retryer, Logger logger) {
            this.client = checkNotNull(client, "client");
//            Collection<ChtFeignInterceptor> innerInterceptors =
//                    checkNotNull(interceptors, "interceptors");
            if (!CollectionUtils.isEmpty(interceptors)) {
                client.addInterceptors(interceptors);
            }
            this.retryer = checkNotNull(retryer, "retryer");
            this.logger = checkNotNull(logger, "logger");
        }

        public InvocationHandlerFactory.MethodHandler create(Target<?> target, MethodMetadata md) {
            return new SynchronousMethodHandler(target, client, retryer, logger, md);
        }
    }
}
