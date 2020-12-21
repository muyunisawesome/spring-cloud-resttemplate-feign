package com.cht.rst.feign;

import org.springframework.cloud.context.named.NamedContextFactory;

/**
 * A factory that creates instances of feign classes. It creates a Spring
 * ApplicationContext per client name, and extracts the beans that it needs from there.
 *
 * @author cht
 */
public class ChtFeignContext extends NamedContextFactory<ChtFeignClientSpecification> {

	public ChtFeignContext() {
		super(ChtFeignClientsConfiguration.class, "chtFeign", "chtFeign.client.name");
	}

}
