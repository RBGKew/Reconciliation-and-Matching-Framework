/*
 * Reconciliation and Matching Framework
 * Copyright © 2014 Royal Botanic Gardens, Kew
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kew.rmf.refine.domain.response;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlRootElement;

import org.kew.rmf.refine.domain.metadata.Type;

@XmlRootElement
public class QueryResult {
	private String id;
	private String name;
	private Type[] type;
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

	public Type[] getType() {
		return type;
	}
	public void setType(Type[] type) {
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
