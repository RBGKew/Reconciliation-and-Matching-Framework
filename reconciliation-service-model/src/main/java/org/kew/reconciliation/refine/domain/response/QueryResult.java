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
