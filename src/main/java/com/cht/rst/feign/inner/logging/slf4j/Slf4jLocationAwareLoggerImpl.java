package com.cht.rst.feign.inner.logging.slf4j;

import com.cht.rst.feign.inner.logging.Log;
import com.cht.rst.feign.inner.logging.LogFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.spi.LocationAwareLogger;

class Slf4jLocationAwareLoggerImpl implements Log {

	private static Marker MARKER = MarkerFactory.getMarker(LogFactory.MARKER);

	private static final String FQCN = Slf4jImpl.class.getName();

	private LocationAwareLogger logger;

	Slf4jLocationAwareLoggerImpl(LocationAwareLogger logger) {
		this.logger = logger;
	}

	@Override
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	@Override
	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}

	@Override
	public void error(String s, Throwable e) {
		logger.log(MARKER, FQCN, LocationAwareLogger.ERROR_INT, s, null, e);
	}

	@Override
	public void error(String s) {
		logger.log(MARKER, FQCN, LocationAwareLogger.ERROR_INT, s, null, null);
	}

	@Override
	public void debug(String s) {
		logger.log(MARKER, FQCN, LocationAwareLogger.DEBUG_INT, s, null, null);
	}

	@Override
	public void trace(String s) {
		logger.log(MARKER, FQCN, LocationAwareLogger.TRACE_INT, s, null, null);
	}

	@Override
	public void info(String s) {
		logger.log(MARKER, FQCN, LocationAwareLogger.INFO_INT, s, null, null);
	}

	@Override
	public void warn(String s) {
		logger.log(MARKER, FQCN, LocationAwareLogger.WARN_INT, s, null, null);
	}

}
