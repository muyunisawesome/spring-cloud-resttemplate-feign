package com.cht.rst.feign.inner.logging.slf4j;

import com.cht.rst.feign.inner.logging.Log;
import org.slf4j.Logger;


class Slf4jLoggerImpl implements Log {

	private Logger log;

	public Slf4jLoggerImpl(Logger logger) {
		log = logger;
	}

	@Override
	public boolean isDebugEnabled() {
		return log.isDebugEnabled();
	}

	@Override
	public boolean isTraceEnabled() {
		return log.isTraceEnabled();
	}

	@Override
	public void error(String s, Throwable e) {
		log.error(s, e);
	}

	@Override
	public void error(String s) {
		log.error(s);
	}

	@Override
	public void debug(String s) {
		log.debug(s);
	}

	@Override
	public void trace(String s) {
		log.trace(s);
	}

	@Override
	public void info(String s) {
		log.info(s);
	}

	@Override
	public void warn(String s) {
		log.warn(s);
	}

}
