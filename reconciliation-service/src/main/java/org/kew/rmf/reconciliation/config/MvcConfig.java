package org.kew.rmf.reconciliation.config;

import org.codehaus.jackson.map.ObjectMapper;
import org.perf4j.slf4j.aop.TimingAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.springframework.web.servlet.view.tiles2.TilesConfigurer;
import org.springframework.web.servlet.view.tiles2.TilesView;

@Configuration
@EnableWebMvc
@ComponentScan(basePackageClasses = {
		org.kew.rmf.reconciliation.service.ReconciliationService.class,
		org.kew.rmf.reconciliation.ws.MatchController.class
})
@EnableAspectJAutoProxy
public class MvcConfig extends WebMvcConfigurerAdapter {
	static @Bean public PropertySourcesPlaceholderConfigurer myPropertySourcesPlaceholderConfigurer() {
		PropertySourcesPlaceholderConfigurer p = new PropertySourcesPlaceholderConfigurer();
		Resource[] resourceLocations = new Resource[] {
				new ClassPathResource("/META-INF/spring/reconciliation-service.properties")
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
	 * Static resources configuration
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/css/**").addResourceLocations("/css/").setCachePeriod(3600 * 30);
		registry.addResourceHandler("/img/**").addResourceLocations("/img/").setCachePeriod(3600 * 30);
		registry.addResourceHandler("/js/**").addResourceLocations("/js/").setCachePeriod(3600 * 30);
	}

	/**
	 * Default servlet handler for resources
	 */
	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
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

	/**
	 * TaskExecutor for asynchronous pooled tasks
	 */
	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(2);
		taskExecutor.setMaxPoolSize(2);
		taskExecutor.initialize();
		return taskExecutor;
	}

	@Bean
	public TimingAspect timingAspect() {
		return new TimingAspect();
	}
}
