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
package org.kew.rmf.reconciliation.ws.dto;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.kew.rmf.utils.Dictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates properties necessary to display a bean nicely in the web interface.
 */
public class DisplayBean<T> {
	private static Logger logger = LoggerFactory.getLogger(DisplayBean.class);

	private String name;
	private String qualifiedName;
	private List<String> configuration;

	public DisplayBean(T javaBean) {
		Class<? extends Object> clazz = javaBean.getClass();
		setName(clazz.getSimpleName().replaceAll("([a-z])([A-Z])", "$1 $2"));
		setQualifiedName(clazz.getCanonicalName());

		List<String> configuration = new ArrayList<>();

		// Find getter/setter pairs, display the name and value.
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(clazz, Object.class);
			for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
				String configurationItem;

				try {
					Method getter = descriptor.getReadMethod();
					Method setter = descriptor.getWriteMethod();

					if (setter != null) {
						configurationItem = descriptor.getName().replaceAll("([a-z])([A-Z])", "$1 $2").toLowerCase();

						if (getter != null) {
							Class<?> propertyType = descriptor.getPropertyType();

							// Quote string values
							if (propertyType.equals(String.class)) {
								configurationItem += ": \"<code>" + getter.invoke(javaBean) + "</code>\"";
							}

							// Recurse for lists
							else if (Iterable.class.isAssignableFrom(propertyType)) {
								Iterable<?> l = (Iterable<?>) getter.invoke(javaBean);
								configurationItem += ": {";
								for (Object li : l) {
									DisplayBean<?> dto = new DisplayBean<Object>(li);
									configurationItem += dto.getName();
									if (dto.getConfiguration() != null && dto.getConfiguration().size() > 0) {
										configurationItem += " " + dto.getConfiguration();
									}
									configurationItem += "; ";
								}
								configurationItem = configurationItem.substring(0, configurationItem.length()-2);
								configurationItem += "}";
							}

							// Dictionaries
							if (propertyType.equals(Dictionary.class)) {
								configurationItem += ": \"<code>" + getter.invoke(javaBean) + "</code>\"";
							}

							// Otherwise
							else {
								configurationItem += ": <code>" + getter.invoke(javaBean) + "</code>";
							}
						}
						else {
							configurationItem += ": [Unknown]";
						}
						configuration.add(configurationItem);
					}
				}
				catch (ReflectiveOperationException | IllegalArgumentException e) {
					logger.warn("Error retrieving property details of "+javaBean+"."+descriptor, e);
				}
			}
		}
		catch (IntrospectionException e) {
			logger.warn("Error retrieving properties of "+javaBean, e);
		}

		setConfiguration(configuration);
	}

	/* • Getters and setters • */
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public String getQualifiedName() { return qualifiedName; }
	public void setQualifiedName(String qualifiedName) { this.qualifiedName = qualifiedName; }

	public List<String> getConfiguration() { return configuration; }
	public void setConfiguration(List<String> configuration) { this.configuration = configuration; }
}
