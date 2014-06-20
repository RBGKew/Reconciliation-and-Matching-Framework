package org.kew.reconciliation.refine.domain.response;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class QueryResponse {
	private QueryResult[] result;

	@Override
	public String toString() {
		return "QueryResponse [result=" + Arrays.toString(result) + "]";
	}

	/* • Getters and setters • */
	public QueryResult[] getResult() {
		return result;
	}
	public void setResult(QueryResult[] result) {
		this.result = result;
	}
}
