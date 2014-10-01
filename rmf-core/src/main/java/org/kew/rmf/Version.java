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
package org.kew.rmf;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds version information.
 */
public class Version {
	private static Logger logger = LoggerFactory.getLogger(Version.class);

	private static String VERSION;

	static {
		try {
			InputStream is = Version.class.getResourceAsStream("/META-INF/maven/org.kew.rmf/deduplicator/pom.properties");

			if (is == null) {
				logger.info("Could not load Maven properties file — probably a development execution.");
				VERSION = "UNKNOWN";
			}
			else {
				Properties prop = new Properties();
				prop.load(is);
				VERSION = prop.get("version").toString();
			}
		}
		catch (Exception e) {
			logger.error("Could not determine application version", e);
			VERSION = "UNKNOWN";
		}

		logger.info("Reconciliation service version: {}", VERSION);
	}

	/* Getters and setters */
	public static String getVersion() {
		return VERSION;
	}
}
