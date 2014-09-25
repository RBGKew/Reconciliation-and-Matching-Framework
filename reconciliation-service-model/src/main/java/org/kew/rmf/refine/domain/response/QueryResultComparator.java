package org.kew.rmf.refine.domain.response;

import java.util.Comparator;

public class QueryResultComparator implements Comparator<QueryResult>{
	@Override
	public int compare(QueryResult o1, QueryResult o2) {
		return Double.compare(o2.getScore(), o1.getScore());
	}
}
