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
