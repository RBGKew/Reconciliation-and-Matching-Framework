/*
 * Reconciliation and Matching Framework
 * Copyright © 2014 Royal Botanic Gardens, Kew
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kew.rmf.reconciliation.config;

import java.util.Properties;

import org.codehaus.jackson.map.ObjectMapper;
import org.kew.rmf.reconciliation.ws.ReconciliationExceptionResolver;
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
import org.springframework.ui.context.ThemeSource;
import org.springframework.ui.context.support.ResourceBundleThemeSource;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ThemeResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.theme.CookieThemeResolver;
import org.springframework.web.servlet.theme.ThemeChangeInterceptor;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.springframework.web.servlet.view.tiles2.TilesConfigurer;
import org.springframework.web.servlet.view.tiles2.TilesView;

@Configuration
@EnableWebMvc
@ComponentScan(basePackageClasses = {
		org.kew.rmf.reconciliation.service.ReconciliationService.class,
		org.kew.rmf.reconciliation.ws.BaseController.class
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
	 * Theme source
	 */
	@Bean
	public ThemeSource themeSource() {
		ResourceBundleThemeSource themeSource = new ResourceBundleThemeSource();
		themeSource.setBasenamePrefix("kew2014-");
		return themeSource;
	}

	/**
	 * Theme resolver
	 */
	@Bean
	public ThemeResolver themeResolver() {
		CookieThemeResolver themeResolver = new CookieThemeResolver();
		themeResolver.setDefaultThemeName("default");
		return themeResolver;
	}

	/**
	 * Theme interceptor (optional, without it the theme won't be changed).
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		ThemeChangeInterceptor themeChangeInterceptor = new ThemeChangeInterceptor();
		themeChangeInterceptor.setParamName("theme");
		registry.addInterceptor(themeChangeInterceptor);
	}

	/**
	 * Tiles2 views configuration.
	 */
	@Bean
	public TilesConfigurer tilesConfigurer() {
		TilesConfigurer tilesConfig = new TilesConfigurer();
		tilesConfig.setDefinitions(new String[] {
				"/WEB-INF/kew-layouts/layouts.xml",
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
		viewResolver.setContentType("text/html;charset=UTF-8");
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
		// Wait up to 30 seconds for the tasks to exit themselves, after receiving an interrupt from the executor.
		taskExecutor.setDaemon(true);
		taskExecutor.setAwaitTerminationSeconds(30);
		taskExecutor.initialize();
		return taskExecutor;
	}

	@Bean
	public TimingAspect timingAspect() {
		return new TimingAspect();
	}

	/**
	 * Generate error pages for unhandled exceptions.
	 */
	@Bean
	public SimpleMappingExceptionResolver simpleMappingExceptionResolver() {
		ReconciliationExceptionResolver r = new ReconciliationExceptionResolver();

		Properties mappings = new Properties();
		mappings.setProperty("UnknownReconciliationServiceException", "404");

		r.setExceptionMappings(mappings);
		r.setDefaultErrorView("500");
		r.setDefaultStatusCode(500);
		r.setPreventResponseCaching(true);
		r.setWarnLogCategory("org.kew.rmf.reconciliation.error");

		return r;
	}
}
