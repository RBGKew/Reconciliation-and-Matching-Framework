package org.kew.reconciliation.refine.domain.query;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Query {
	private String query;
	private String type;
	private Integer limit;
	private String type_strict;
	private Property[] properties;

	@Override
	public String toString() {
		return "Query [query=" + query + ", type=" + type + ", limit=" + limit
				+ ", type_strict=" + type_strict + ", properties="
				+ Arrays.toString(properties) + "]";
	}

	/* • Getters and setters • */
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public Integer getLimit() {
		return limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public String getType_strict() {
		return type_strict;
	}
	public void setType_strict(String type_strict) {
		this.type_strict = type_strict;
	}

	public Property[] getProperties() {
		return properties;
	}
	public void setProperties(Property[] properties) {
		this.properties = properties;
	}
}
