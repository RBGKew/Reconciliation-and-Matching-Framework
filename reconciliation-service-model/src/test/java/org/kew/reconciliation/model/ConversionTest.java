package org.kew.reconciliation.model;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.kew.reconciliation.refine.domain.metadata.Metadata;
import org.kew.reconciliation.refine.domain.query.Property;
import org.kew.reconciliation.refine.domain.query.Query;
import org.kew.reconciliation.refine.domain.response.QueryResponse;
import org.kew.reconciliation.refine.domain.response.QueryResult;

public class ConversionTest {

	/**
	 * Show that we can convert to / from JSON and the POJOs used in communication with reconciliation service.
	 */
	@Test
	public void testConversion() {

		// We use a Jackson object mapper:
		ObjectMapper jsonMapper = new ObjectMapper();

		/*
		 * Create a metadata object and set some values
		 * Using sample from:
		 * http://code.google.com/p/google-refine/wiki/ReconciliationServiceApi
		 * http://standard-reconcile.freebaseapps.com/reconcile?callback=jsonp
		 */
		Metadata metadata = new Metadata();
		metadata.setName("Freebase Reconciliation Service");
		metadata.setSchemaSpace("http://rdf.freebase.com/ns/type.object.mid");
		metadata.setIdentifierSpace("http://rdf.freebase.com/ns/type.object.id");
		Metadata.MetadataView metadataView = metadata.new MetadataView();
		metadataView.setUrl("http://www.freebase.com/view{{id}}");
		metadata.setView(metadataView);

		try {
			System.out.println(metadata.toString());
			System.out.println(jsonMapper.writeValueAsString(metadata));
		}
		catch (Exception e){
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

		/*
		 * Create a query object and set some values:
		 */
		Query query = new Query();
		query.setQuery("Poa annua");
		query.setLimit(3);
		query.setType("/scientific_name");
		query.setType_strict("any");
		// Properties are a flexible way to pass more data into the service:
		Property property = new Property();
		property.setP("author");
		property.setV("L.");
		List<Property> properties = new ArrayList<Property>();
		properties.add(property);
		query.setProperties(properties.toArray(new Property[0]));

		try {
			System.out.println(query.toString());
			String json = jsonMapper.writeValueAsString(query);
			System.out.println(json);
			// Now "round trip" it - i.e. use this JSON to build a new Java object representation:
			Query query_roundtripped = jsonMapper.readValue(json, Query.class);
			System.out.println(query_roundtripped.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

		/*
		 * Create a query response object and set some values:
		 */
		QueryResponse queryResponse = new QueryResponse();
		// A response can contain multiple queryresults
		List<QueryResult> queryResults = new ArrayList<QueryResult>();
		// Build one queryresult
		QueryResult queryResult = new QueryResult();
		queryResult.setId("12345");
		queryResult.setName("Poa annua");
		List<String> types = new ArrayList<String>();
		types.add("/scientific_name");
		queryResult.setType(types.toArray(new String[0]));
		queryResult.setScore(100);
		queryResult.setMatch(true);
		// Add to list
		queryResults.add(queryResult);
		// Set list of queryresults onto response
		queryResponse.setResult(queryResults.toArray(new QueryResult[0]));

		try {
			System.out.println(queryResponse.toString());
			String json = jsonMapper.writeValueAsString(queryResponse);
			System.out.println(json);
			// Now "round trip" it - i.e. use this JSON to build a new Java object representation:
			QueryResponse queryResponse_roundtripped = jsonMapper.readValue(json, QueryResponse.class);
			System.out.println(queryResponse_roundtripped.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
}
