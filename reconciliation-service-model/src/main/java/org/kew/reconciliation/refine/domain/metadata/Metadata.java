package org.kew.reconciliation.refine.domain.metadata;

import java.util.Arrays;

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
	private Type[] defaultTypes;

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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		Metadata other = (Metadata) obj;

		if (!Arrays.equals(defaultTypes, other.defaultTypes))
			return false;

		if (identifierSpace == null) {
			if (other.identifierSpace != null)
				return false;
		}
		else if (!identifierSpace.equals(other.identifierSpace))
			return false;

		if (name == null) {
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;

		if (preview == null) {
			if (other.preview != null)
				return false;
		}
		else if (!preview.equals(other.preview))
			return false;

		if (schemaSpace == null) {
			if (other.schemaSpace != null)
				return false;
		}
		else if (!schemaSpace.equals(other.schemaSpace))
			return false;

		if (suggest == null) {
			if (other.suggest != null)
				return false;
		}
		else if (!suggest.equals(other.suggest))
			return false;

		if (view == null) {
			if (other.view != null)
				return false;
		}
		else if (!view.equals(other.view))
			return false;

		return true;
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
	public Type[] getDefaultTypes() { return defaultTypes; }
	public void setDefaultTypes(Type[] defaultTypes) { this.defaultTypes = defaultTypes; }
}
