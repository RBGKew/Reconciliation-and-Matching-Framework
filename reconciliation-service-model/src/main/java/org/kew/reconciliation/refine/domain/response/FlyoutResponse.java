package org.kew.reconciliation.refine.domain.response;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * The flyout response is simply HTML wrapped in JSON.
 */
@XmlRootElement
public class FlyoutResponse {
	private String html;

	public FlyoutResponse() {}

	public FlyoutResponse(String html) {
		this.html = html;
	}

	public String getHtml() {
		return html;
	}
	public void setHtml(String html) {
		this.html = html;
	}
}
