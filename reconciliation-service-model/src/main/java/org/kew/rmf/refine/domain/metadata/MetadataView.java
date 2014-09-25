package org.kew.rmf.refine.domain.metadata;

public class MetadataView {
	private String url;

	@Override
	public String toString() {
		return "MetadataView [url="+url+"]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;
		MetadataView other = (MetadataView) obj;

		if (url == null) {
			if (other.url != null)
				return false;
		}
		else if (!url.equals(other.url))
			return false;

		return true;
	}

	/* • Getters and setters • */
	public String getUrl() { return url; }
	public void setUrl(String url) { this.url = url; }
}