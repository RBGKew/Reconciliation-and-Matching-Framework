package org.kew.reconciliation.refine.domain.response;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class QueryResult {
	private String id;
	private String name;
	private String[] type;
	private double score;
	private boolean match;

	@Override
	public String toString() {
		return "QueryResult [id=" + id + ", name=" + name + ", type="
				+ Arrays.toString(type) + ", score=" + score + ", match="
				+ match + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		QueryResult other = (QueryResult) obj;

		if (id == null) {
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;

		if (match != other.match)
			return false;

		if (name == null) {
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;

		if (Double.doubleToLongBits(score) != Double.doubleToLongBits(other.score))
			return false;

		if (!Arrays.equals(type, other.type))
			return false;

		return true;
	}

	/* • Getters and setters • */
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String[] getType() {
		return type;
	}
	public void setType(String[] type) {
		this.type = type;
	}

	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}

	public boolean isMatch() {
		return match;
	}
	public void setMatch(boolean match) {
		this.match = match;
	}
}
