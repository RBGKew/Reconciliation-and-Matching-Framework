package org.kew.reconciliation.config;

import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class WebAppInitializer implements WebApplicationInitializer {
	private static Logger log = LoggerFactory.getLogger(WebAppInitializer.class);

	@Override
	public void onStartup(ServletContext servletContext) {
		WebApplicationContext rootContext = createRootContext(servletContext);

		configureSpringMvc(servletContext, rootContext);
	}

	private WebApplicationContext createRootContext(ServletContext servletContext) {
		AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
		rootContext.register(CoreConfig.class);
		rootContext.refresh();

		servletContext.addListener(new ContextLoaderListener(rootContext));
		servletContext.setInitParameter("defaultHtmlEscape", "true");

		return rootContext;
	}

	private void configureSpringMvc(ServletContext servletContext, WebApplicationContext rootContext) {
		AnnotationConfigWebApplicationContext mvcContext = new AnnotationConfigWebApplicationContext();
		mvcContext.register(MvcConfig.class);

		mvcContext.setParent(rootContext);
		ServletRegistration.Dynamic appServlet = servletContext.addServlet("reconciliation-service", new DispatcherServlet(mvcContext));
		appServlet.setLoadOnStartup(1);
		Set<String> mappingConflicts = appServlet.addMapping("/");

		if (!mappingConflicts.isEmpty()) {
			for (String s : mappingConflicts) {
				log.error("Mapping conflict: " + s);
			}
			throw new IllegalStateException("'webservice' cannot be mapped to '/'");
		}
	}
}
