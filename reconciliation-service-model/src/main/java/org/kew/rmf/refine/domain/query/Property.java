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
package org.kew.rmf.refine.domain.query;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Property {
	private String p;
	private String pid;
	private String v;

	@Override
	public String toString() {
		return "Property [p=" + p + ", pid=" + pid + ", v=" + v + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;

		Property other = (Property) obj;
		if (p == null) {
			if (other.p != null) return false;
		}
		else if (!p.equals(other.p)) return false;

		if (pid == null) {
			if (other.pid != null) return false;
		}
		else if (!pid.equals(other.pid)) return false;

		if (v == null) {
			if (other.v != null) return false;
		}
		else if (!v.equals(other.v)) return false;

		return true;
	}

	/* • Getters and setters • */
	public String getP() {
		return p;
	}
	public void setP(String p) {
		this.p = p;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getV() {
		return v;
	}
	public void setV(String v) {
		this.v = v;
	}
}
