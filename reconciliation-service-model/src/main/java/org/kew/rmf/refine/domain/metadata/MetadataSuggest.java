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