package org.kew.rmf.refine.domain.metadata;

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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		MetadataSuggestDetails other = (MetadataSuggestDetails) obj;

		if (flyout_service_path == null) {
			if (other.flyout_service_path != null)
				return false;
		}
		else if (!flyout_service_path.equals(other.flyout_service_path))
			return false;

		if (flyout_service_url == null) {
			if (other.flyout_service_url != null)
				return false;
		}
		else if (!flyout_service_url.equals(other.flyout_service_url))
			return false;

		if (service_path == null) {
			if (other.service_path != null)
				return false;
		}
		else if (!service_path.equals(other.service_path))
			return false;

		if (service_url == null) {
			if (other.service_url != null)
				return false;
		}
		else if (!service_url.equals(other.service_url))
			return false;

		return true;
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