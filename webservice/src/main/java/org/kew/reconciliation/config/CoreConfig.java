package org.kew.reconciliation.config;

import javax.annotation.Resource;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
//@PropertySource("classpath:${environment:cucumber}-reconciliation-service.properties")
//@EnableCaching
public class CoreConfig {

	@Resource
	private Environment env;

	// Doesn't do anything yet.  Custom beans can be defined here.
}
