package org.kew.reconciliation;

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
			InputStream is = Version.class.getResourceAsStream("/META-INF/maven/org.kew.rmf/reconciliation-service/pom.properties");
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
