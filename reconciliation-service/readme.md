# Reconciliation Service

The web application uses the properties in reconciliation-service.properties to know
where source data files are, and where temporary Lucene indices should go.

This file is excluded from the created WAR, and server configurations must include
the file on the classpath.

To run the application for development purposes:
	`mvn jetty:run`
