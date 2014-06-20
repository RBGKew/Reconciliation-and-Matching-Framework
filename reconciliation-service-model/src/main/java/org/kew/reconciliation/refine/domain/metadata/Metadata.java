package org.kew.reconciliation.refine.domain.metadata;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents the Metadata class of the OpenRefine Reconciliation Service API.
 *
 * See https://github.com/OpenRefine/OpenRefine/wiki/Reconciliation-Service-API
 */
@XmlRootElement
public class Metadata {
	private String name;
	private String identifierSpace;
	private String schemaSpace;
	private MetadataView view;
	private MetadataPreview preview;
	private MetadataSuggest suggest;
	private String[] defaultTypes;

	@Override
	public String toString() {
		return "Metadata ["
				+ "name=" + name
				+ ", identifierSpace=" + identifierSpace
				+ ", schemaSpace=" + schemaSpace
				+ ", view=" + view
				+ ", preview=" + preview
				+ ", suggest=" + suggest
				+ ", defaultTypes=" + defaultTypes
				+ "]";
	}

	public class MetadataView {
		private String url;

		@Override
		public String toString() {
			return "MetadataView [url="+url+"]";
		}

		/* • Getters and setters • */
		public String getUrl() { return url; }
		public void setUrl(String url) { this.url = url; }
	}

	public class MetadataPreview {
		private String url;
		private int width;
		private int height;

		@Override
		public String toString() {
			return "MetadataPreview ["
					+ "url=" + url
					+ ", width=" + width
					+ ", height=" + height
					+ "]";
		}

		/* • Getters and setters • */
		public String getUrl() { return url; }
		public void setUrl(String url) { this.url = url; }
		public int getWidth() { return width; }
		public void setWidth(int width) { this.width = width; }
		public int getHeight() { return height; }
		public void setHeight(int height) { this.height = height; }
	}

	public class MetadataSuggest {
		private MetadataSuggestDetails type;
		private MetadataSuggestDetails property;
		private MetadataSuggestDetails entity;

		@Override
		public String toString() {
			return "MetadataSuggest ["
					+ "type=" + type
					+ ", property=" + property
					+ ", entity=" + entity
					+ "]";
		}

		public class MetadataSuggestDetails {
			private String service_url;
			private String service_path;
			private String flyout_service_url;
			private String flyout_service_path;

			@Override
			public String toString() {
				return "MetadataSuggestDetails ["
						+ "service_url=" + service_url
						+ ", service_path=" + service_path
						+ ", flyout_service_url=" + flyout_service_url
						+ ", flyout_service_path=" + flyout_service_path
						+ "]";
			}

			/* • Getters and setters • */
			public String getService_url() { return service_url; }
			public void setService_url(String serviceUrl) { this.service_url = serviceUrl; }
			public String getService_path() { return service_path; }
			public void setService_path(String servicePath) { this.service_path = servicePath; }
			public String getFlyout_service_url() { return flyout_service_url; }
			public void setFlyout_service_url(String flyoutServiceUrl) { this.flyout_service_url = flyoutServiceUrl; }
			public String getFlyout_service_path() { return flyout_service_path; }
			public void setFlyout_service_path(String flyoutServicePath) { this.flyout_service_path = flyoutServicePath; }
		}

		/* • Getters and setters • */
		public MetadataSuggestDetails getType() { return type; }
		public void setType(MetadataSuggestDetails type) { this.type = type; }
		public MetadataSuggestDetails getProperty() { return property; }
		public void setProperty(MetadataSuggestDetails property) { this.property = property; }
		public MetadataSuggestDetails getEntity() { return entity; }
		public void setEntity(MetadataSuggestDetails entity) { this.entity = entity; }
	}

	/* • Getters and setters • */
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getIdentifierSpace() { return identifierSpace; }
	public void setIdentifierSpace(String identifierSpace) { this.identifierSpace = identifierSpace; }
	public String getSchemaSpace() { return schemaSpace; }
	public void setSchemaSpace(String schemaSpace) { this.schemaSpace = schemaSpace; }
	public MetadataView getView() { return view; }
	public void setView(MetadataView view) { this.view = view; }
	public MetadataPreview getPreview() { return preview; }
	public void setPreview(MetadataPreview preview) { this.preview = preview; }
	public MetadataSuggest getSuggest() { return suggest; }
	public void setSuggest(MetadataSuggest suggest) { this.suggest = suggest; }
	public String[] getDefaultTypes() { return defaultTypes; }
	public void setDefaultTypes(String[] defaultTypes) { this.defaultTypes = defaultTypes; }
}
