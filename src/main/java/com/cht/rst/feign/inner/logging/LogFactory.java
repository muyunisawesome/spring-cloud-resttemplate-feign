package com.cht.rst.feign.inner.logging;

import com.cht.rst.feign.inner.logging.commons.JakartaCommonsLoggingImpl;
import com.cht.rst.feign.inner.logging.jdk14.Jdk14LoggingImpl;
import com.cht.rst.feign.inner.logging.log4j.Log4jImpl;
import com.cht.rst.feign.inner.logging.log4j2.Log4j2Impl;
import com.cht.rst.feign.inner.logging.nologging.NoLoggingImpl;
import com.cht.rst.feign.inner.logging.slf4j.Slf4jImpl;
import com.cht.rst.feign.inner.logging.stdout.StdOutImpl;

import java.lang.reflect.Constructor;

public final class LogFactory {

    public static final String MARKER = "CHT-FEIGN";

    private static Constructor<? extends Log> logConstructor;

    static {
        tryImplementation(LogFactory::useSlf4jLogging);
        tryImplementation(LogFactory::useCommonsLogging);
        tryImplementation(LogFactory::useLog4J2Logging);
        tryImplementation(LogFactory::useLog4JLogging);
        tryImplementation(LogFactory::useJdkLogging);
        tryImplementation(LogFactory::useNoLogging);
    }

    private LogFactory() {
        // disable construction
    }

    public static Log getLog(Class<?> aClass) {
        return getLog(aClass.getName());
    }

    public static Log getLog(String logger) {
        try {
            return logConstructor.newInstance(new Object[]{logger});
        } catch (Throwable t) {
            throw new LogException("Error creating logger for logger " + logger + ".  Cause: " + t, t);
        }
    }

    public static synchronized void useCustomLogging(Class<? extends Log> clazz) {
        setImplementation(clazz);
    }

    public static synchronized void useSlf4jLogging() {
        setImplementation(Slf4jImpl.class);
    }

    public static synchronized void useCommonsLogging() {
        setImplementation(JakartaCommonsLoggingImpl.class);
    }

    public static synchronized void useLog4JLogging() {
        setImplementation(Log4jImpl.class);
    }

    public static synchronized void useLog4J2Logging() {
        setImplementation(Log4j2Impl.class);
    }

    public static synchronized void useJdkLogging() {
        setImplementation(Jdk14LoggingImpl.class);
    }

    public static synchronized void useStdOutLogging() {
        setImplementation(StdOutImpl.class);
    }

    public static synchronized void useNoLogging() {
        setImplementation(NoLoggingImpl.class);
    }

    private static void tryImplementation(Runnable runnable) {
        if (logConstructor == null) {
            try {
                runnable.run();
            } catch (Throwable t) {
                // ignore
            }
        }
    }

    private static void setImplementation(Class<? extends Log> implClass) {
        try {
            Constructor<? extends Log> candidate = implClass.getConstructor(new Class[]{String.class});
            Log log = candidate.newInstance(new Object[]{LogFactory.class.getName()});
            if (log.isDebugEnabled()) {
                log.debug("Logging initialized using '" + implClass + "' adapter.");
            }
            logConstructor = candidate;
        } catch (Throwable t) {
            throw new LogException("Error setting Log implementation.  Cause: " + t, t);
        }
    }

}
