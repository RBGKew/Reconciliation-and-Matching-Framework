package org.kew.rmf.refine.domain.response;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class QueryResponse<T> {
	private T[] result;

	@Override
	public String toString() {
		return "QueryResponse [result=" + Arrays.toString(result) + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		QueryResponse<?> other = (QueryResponse<?>) obj;

		if (!Arrays.equals(result, other.result))
			return false;

		return true;
	}

	/* • Getters and setters • */
	public T[] getResult() {
		return result;
	}
	public void setResult(T[] result) {
		this.result = result;
	}
}
