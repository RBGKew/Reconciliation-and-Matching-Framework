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

import java.util.Set;

import javax.servlet.FilterRegistration;
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

		// Add Perf4J graphing servlet
		ServletRegistration.Dynamic servletRegistration = servletContext.addServlet("perf4j", org.perf4j.logback.servlet.GraphingServlet.class);
		servletRegistration.setInitParameter("graphNames", "graphOtherTimes,graphQueryTimes,graphQueriesPerSecond");
		servletRegistration.addMapping("/perf4j");

		String TAG_SWAP_FILTER_CLASS = "org.kew.servlet.filter.TagSwapFilter";
		try {
			Class.forName(TAG_SWAP_FILTER_CLASS, false, this.getClass().getClassLoader());
			String[] urlPatterns = {"/", "/about/*", "/admin", "/filematch/*", "/help"};

			FilterRegistration.Dynamic cssLinkFilter = servletContext.addFilter("CssLinkFilter",TAG_SWAP_FILTER_CLASS);
			cssLinkFilter.setInitParameter("include_file_name", "/var/lib/science-apps/web-resources/2014/head.chunk");
			cssLinkFilter.setInitParameter("tag_name", "[KEWCSS]");
			cssLinkFilter.addMappingForUrlPatterns(null, true, urlPatterns);

			FilterRegistration.Dynamic headerFilter = servletContext.addFilter("HeaderFilter", TAG_SWAP_FILTER_CLASS);
			headerFilter.setInitParameter("include_file_name", "/var/lib/science-apps/web-resources/2014/bodytop.chunk");
			headerFilter.setInitParameter("tag_name", "[KEWHEADER]");
			headerFilter.addMappingForUrlPatterns(null, true, urlPatterns);

			FilterRegistration.Dynamic footerFilter = servletContext.addFilter("FooterFilter", TAG_SWAP_FILTER_CLASS);
			footerFilter.setInitParameter("include_file_name", "/var/lib/science-apps/web-resources/2014/bodybottom.chunk");
			footerFilter.setInitParameter("tag_name", "[KEWFOOTER]");
			footerFilter.addMappingForUrlPatterns(null, true, urlPatterns);
		}
		catch (ClassNotFoundException e) {
			log.error("Kew servlet filters not in use, class {} not on classpath", TAG_SWAP_FILTER_CLASS);
		}
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
