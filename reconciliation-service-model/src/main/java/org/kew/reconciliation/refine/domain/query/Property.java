package org.kew.reconciliation.refine.domain.query;

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
