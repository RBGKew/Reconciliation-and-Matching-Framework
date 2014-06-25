package org.kew.reconciliation.config;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.springframework.web.servlet.view.tiles2.TilesConfigurer;
import org.springframework.web.servlet.view.tiles2.TilesView;

@Configuration
@EnableWebMvc
@ComponentScan(basePackageClasses = {
		org.kew.reconciliation.service.ReconciliationService.class,
		org.kew.stringmod.ws.MatchController.class
})
public class MvcConfig {
	static @Bean public PropertySourcesPlaceholderConfigurer myPropertySourcesPlaceholderConfigurer() {
		PropertySourcesPlaceholderConfigurer p = new PropertySourcesPlaceholderConfigurer();
		Resource[] resourceLocations = new Resource[] {
				new ClassPathResource("reconciliation-service.properties")
		};
		p.setLocations(resourceLocations);
		return p;
	}

	@Bean
	public ObjectMapper jsonMapper() {
		return new ObjectMapper();
	}

	/**
	 * Increase maximum upload size.
	 * TODO: Test.
	 */
	@Bean
	public MultipartResolver multipartResolver() {
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
		multipartResolver.setMaxUploadSize(100 * 1024 * 1024);
		return multipartResolver;
	}

	/**
	 * Tiles2 views configuration.
	 */
	@Bean
	public TilesConfigurer tilesConfigurer() {
		TilesConfigurer tilesConfig = new TilesConfigurer();
		tilesConfig.setDefinitions(new String[] {
				"/WEB-INF/layouts/layouts.xml",
				"/WEB-INF/views/**/views.xml"
		});
		tilesConfig.setCheckRefresh(true);
		return tilesConfig;
	}

	/**
	 * ViewResolver configuration for Tiles2-based views.
	 */
	@Bean
	public UrlBasedViewResolver viewResolver() {
		UrlBasedViewResolver viewResolver = new UrlBasedViewResolver();
		viewResolver.setViewClass(TilesView.class);
		return viewResolver;
	}
}
