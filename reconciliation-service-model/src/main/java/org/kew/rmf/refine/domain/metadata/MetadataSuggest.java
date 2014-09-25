package org.kew.rmf.refine.domain.metadata;

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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;
		MetadataSuggest other = (MetadataSuggest) obj;

		if (entity == null) {
			if (other.entity != null)
				return false;
		}
		else if (!entity.equals(other.entity))
			return false;

		if (property == null) {
			if (other.property != null)
				return false;
		}
		else if (!property.equals(other.property))
			return false;

		if (type == null) {
			if (other.type != null)
				return false;
		}
		else if (!type.equals(other.type))
			return false;

		return true;
	}

	/* • Getters and setters • */
	public MetadataSuggestDetails getType() { return type; }
	public void setType(MetadataSuggestDetails type) { this.type = type; }
	public MetadataSuggestDetails getProperty() { return property; }
	public void setProperty(MetadataSuggestDetails property) { this.property = property; }
	public MetadataSuggestDetails getEntity() { return entity; }
	public void setEntity(MetadataSuggestDetails entity) { this.entity = entity; }
}