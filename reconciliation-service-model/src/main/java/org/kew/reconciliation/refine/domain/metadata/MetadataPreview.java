package org.kew.reconciliation.refine.domain.metadata;

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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		MetadataPreview other = (MetadataPreview) obj;

		if (height != other.height)
			return false;

		if (url == null) {
			if (other.url != null)
				return false;
		}
		else if (!url.equals(other.url))
			return false;

		if (width != other.width)
			return false;

		return true;
	}

	/* • Getters and setters • */
	public String getUrl() { return url; }
	public void setUrl(String url) { this.url = url; }
	public int getWidth() { return width; }
	public void setWidth(int width) { this.width = width; }
	public int getHeight() { return height; }
	public void setHeight(int height) { this.height = height; }
}