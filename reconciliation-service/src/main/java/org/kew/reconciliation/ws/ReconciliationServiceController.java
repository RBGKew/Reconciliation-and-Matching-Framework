package org.kew.reconciliation.ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.kew.reconciliation.queryextractor.QueryStringToPropertiesExtractor;
import org.kew.reconciliation.refine.domain.metadata.Metadata;
import org.kew.reconciliation.refine.domain.metadata.Type;
import org.kew.reconciliation.refine.domain.query.Query;
import org.kew.reconciliation.refine.domain.response.FlyoutResponse;
import org.kew.reconciliation.refine.domain.response.QueryResponse;
import org.kew.reconciliation.refine.domain.response.QueryResult;
import org.kew.reconciliation.service.ReconciliationService;
import org.kew.stringmod.dedupl.configuration.Property;
import org.kew.stringmod.dedupl.exception.MatchExecutionException;
import org.kew.stringmod.dedupl.exception.TooManyMatchesException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

/**
 * Implements an <a href="https://github.com/OpenRefine/OpenRefine/wiki/Reconciliation-Service-Api">OpenRefine Reconciliation Service</a>
 * on top of a match configuration.
 */
@Controller
public class ReconciliationServiceController {
	private static Logger logger = LoggerFactory.getLogger(ReconciliationServiceController.class);

	@Autowired
	private ReconciliationService reconciliationService;

	@Autowired
	private ServletContext servletContext;

	@Autowired
	private ObjectMapper jsonMapper;

	private RestTemplate template = new RestTemplate();

	/**
	 * Retrieve reconciliation service metadata.
	 */
	@RequestMapping(value = "/reconcile/{configName}", method={RequestMethod.GET,RequestMethod.POST}, produces="application/json; charset=UTF-8")
	public ResponseEntity<String> getMetadata(HttpServletRequest request, @PathVariable String configName, @RequestParam(value="callback",required=false) String callback, Model model) throws JsonGenerationException, JsonMappingException, IOException {
		logger.info("{}: Get Metadata request", configName);

		String myUrl = request.getScheme() + "://" + request.getServerName() + (request.getServerPort() == 80 ? "" : (":" + request.getServerPort()));
		String basePath = servletContext.getContextPath() + "/reconcile/" + configName;

		Metadata metadata;
		try {
			metadata = reconciliationService.getMetadata(configName);
		}
		catch (MatchExecutionException e) {
			return new ResponseEntity<String>(e.toString(), HttpStatus.NOT_FOUND);
		}

		if (metadata != null) {
			String metadataJson = jsonMapper.writeValueAsString(metadata).replace("LOCAL", myUrl).replace("BASE", basePath);
			return new ResponseEntity<String>(wrapResponse(callback, metadataJson), HttpStatus.OK);
		}
		return null;
	}

	/**
	 * Perform multiple reconciliation queries (no callback)
	 */
	@RequestMapping(value = "/reconcile/{configName}", method={RequestMethod.GET,RequestMethod.POST}, params={"queries"}, produces="application/json; charset=UTF-8")
	public @ResponseBody String doMultipleQueries(@PathVariable String configName, @RequestParam("queries") String queries) {
		return doMultipleQueries(configName, queries, null);
	}

	/**
	 * Perform multiple reconciliation queries (no callback)
	 */
	@RequestMapping(value = "/reconcile/{configName}", method={RequestMethod.GET,RequestMethod.POST}, params={"queries","callback"}, produces="application/json; charset=UTF-8")
	public @ResponseBody String doMultipleQueries(@PathVariable String configName, @RequestParam("queries") String queries, @RequestParam(value="callback",required=false) String callback) {
		logger.info("{}: Multiple query request {}", configName, queries);

		String jsonres = null;
		Map<String,QueryResponse<QueryResult>> res = new HashMap<>();
		try {
			// Convert JSON to map of queries
			Map<String,Query> qs = jsonMapper.readValue(queries, new TypeReference<Map<String,Query>>() {});
			for (String key : qs.keySet()) {
				Query q = qs.get(key);
				QueryResult[] qres = doQuery(q, configName);
				QueryResponse<QueryResult> response = new QueryResponse<>();
				response.setResult(qres);
				res.put(key,response);
			}
			jsonres = jsonMapper.writeValueAsString(res);
		}
		catch (Exception e) {
			logger.error(configName + ": Error with multiple query call", e);
		}
		return wrapResponse(callback, jsonres);
	}

	/**
	 * Single reconciliation query, no callback.
	 */
	@RequestMapping(value = "/reconcile/{configName}", method={RequestMethod.GET,RequestMethod.POST}, params={"query"}, produces="application/json; charset=UTF-8")
	public ResponseEntity<String> doSingleQuery(@PathVariable String configName, @RequestParam("query") String query) {
		return doSingleQuery(configName, query, null);
	}

	/**
	 * Single reconciliation query.
	 */
	@RequestMapping(value = "/reconcile/{configName}", method={RequestMethod.GET,RequestMethod.POST}, params={"query","callback"}, produces="application/json; charset=UTF-8")
	public ResponseEntity<String> doSingleQuery(@PathVariable String configName, @RequestParam("query") String query, @RequestParam(value="callback",required=false) String callback) {
		logger.info("{}: Single query request {}", configName, query);

		String jsonres = null;
		try {
			Query q = jsonMapper.readValue(query, Query.class);
			QueryResult[] qres = doQuery(q, configName);
			QueryResponse<QueryResult> response = new QueryResponse<>();
			response.setResult(qres);
			jsonres = jsonMapper.writeValueAsString(response);
		}
		catch (JsonMappingException | JsonGenerationException e) {
			logger.warn(configName + ": Error parsing JSON query", e);
			return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		catch (IOException e) {
			return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		catch (MatchExecutionException e) {
			return new ResponseEntity<String>(e.toString(), HttpStatus.NOT_FOUND);
		}
		catch (TooManyMatchesException e) {
			return new ResponseEntity<String>(e.toString(), HttpStatus.CONFLICT);
		}

		return new ResponseEntity<String>(wrapResponse(callback, jsonres), HttpStatus.OK);
	}

	/**
	 * Single suggest query, no callback.
	 */
	@RequestMapping(value = "/reconcile/{configName}", method={RequestMethod.GET,RequestMethod.POST}, params={"prefix"}, produces="application/json; charset=UTF-8")
	public ResponseEntity<String> doSuggest(@PathVariable String configName, @RequestParam("prefix") String prefix) {
		return doSuggest(configName, prefix, null);
	}

	/**
	 * Single suggest query.
	 */
	@RequestMapping(value = "/reconcile/{configName}", method={RequestMethod.GET,RequestMethod.POST}, params={"prefix","callback"}, produces="application/json; charset=UTF-8")
	public ResponseEntity<String> doSuggest(@PathVariable String configName, @RequestParam("prefix") String prefix, @RequestParam(value="callback",required=false) String callback) {
		logger.info("{}: Suggest query, prefix {}", configName, prefix);

		Query q = new Query();
		q.setQuery(prefix);

		try {
			return doSingleQuery(configName, jsonMapper.writeValueAsString(q), callback);
		}
		catch (IOException e) {
			return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Type suggest, no callback.
	 */
	@RequestMapping(value = "/reconcile/{configName}/suggestType", method={RequestMethod.GET,RequestMethod.POST}, params={"prefix"}, produces="application/json; charset=UTF-8")
	public ResponseEntity<String> doSuggestType(@PathVariable String configName, @RequestParam("prefix") String prefix) {
		return doSuggestType(configName, prefix, null);
	}

	/**
	 * Type suggest.
	 */
	@RequestMapping(value = "/reconcile/{configName}/suggestType", method={RequestMethod.GET,RequestMethod.POST}, params={"prefix","callback"}, produces="application/json; charset=UTF-8")
	public ResponseEntity<String> doSuggestType(@PathVariable String configName, @RequestParam("prefix") String prefix, @RequestParam(value="callback",required=false) String callback) {
		logger.info("{}: Type suggest query, prefix {}", configName, prefix);

		String jsonres = null;
		try {
			Type[] defaultTypes = reconciliationService.getMetadata(configName).getDefaultTypes();
			QueryResponse<Type> response = new QueryResponse<>();
			response.setResult(defaultTypes);
			jsonres = jsonMapper.writeValueAsString(response);
		}
		catch (JsonMappingException | JsonGenerationException e) {
			logger.warn(configName + ": Error parsing JSON query", e);
			return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		catch (IOException e) {
			return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		catch (MatchExecutionException e) {
			return new ResponseEntity<String>(e.toString(), HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<String>(wrapResponse(callback, jsonres), HttpStatus.OK);
	}

	/**
	 * Type suggest flyout
	 */
	@RequestMapping(value = "/reconcile/{configName}/flyoutType/{id:.+}", method={RequestMethod.GET,RequestMethod.POST}, produces="application/json; charset=UTF-8")
	public ResponseEntity<String> doTypeFlyout(@PathVariable String configName, @PathVariable String id, @RequestParam(value="callback",required=false) String callback) {
		logger.info("{}: Type flyout for id {}", configName, id);

		try {
			Type[] defaultTypes = reconciliationService.getMetadata(configName).getDefaultTypes();
			Type type = null;

			for (Type t : defaultTypes) {
				if (t.getId().equals(id)) {
					type = t;
				}
			}

			String html = "<html><body><ul><li>"+type.getName()+" ("+type.getId()+")</li></ul></body></html>\n";
			FlyoutResponse jsonWrappedHtml = new FlyoutResponse(html);

			return new ResponseEntity<String>(wrapResponse(callback, jsonMapper.writeValueAsString(jsonWrappedHtml)), HttpStatus.OK);
		}
		catch (IOException e) {
			logger.warn(configName + ": Error in type flyout for id "+id, e);
			return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		catch (MatchExecutionException | NullPointerException e) {
			return new ResponseEntity<String>(e.toString(), HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Properties suggest, no callback.
	 */
	@RequestMapping(value = "/reconcile/{configName}/suggestProperty", method={RequestMethod.GET,RequestMethod.POST}, params={"prefix"}, produces="application/json; charset=UTF-8")
	public ResponseEntity<String> doSuggestProperty(@PathVariable String configName, @RequestParam("prefix") String prefix) {
		return doSuggestProperty(configName, prefix, null);
	}

	/**
	 * Properties suggest.
	 */
	@RequestMapping(value = "/reconcile/{configName}/suggestProperty", method={RequestMethod.GET,RequestMethod.POST}, params={"prefix","callback"}, produces="application/json; charset=UTF-8")
	public ResponseEntity<String> doSuggestProperty(@PathVariable String configName, @RequestParam("prefix") String prefix, @RequestParam(value="callback",required=false) String callback) {
		logger.info("{}: Property suggest query, prefix {}", configName, prefix);

		String jsonres = null;
		try {
			List<Type> filteredProperties = new ArrayList<>();
			List<Property> properties = reconciliationService.getReconciliationServiceConfiguration(configName).getProperties();
			for (Property p : properties) {
				String name = p.getQueryColumnName();

				// Filter by prefix
				if (name != null && name.toUpperCase().startsWith(prefix.toUpperCase())) {
					Type t = new Type();
					t.setId(name);
					t.setName(name);
					filteredProperties.add(t);
				}
			}
			logger.debug("Suggest Property query for {} filtered {} properties to {}", prefix, properties.size(), filteredProperties);

			QueryResponse<Type> response = new QueryResponse<>();
			response.setResult(filteredProperties.toArray(new Type[1]));
			jsonres = jsonMapper.writeValueAsString(response);
		}
		catch (JsonMappingException | JsonGenerationException e) {
			logger.warn(configName + ": Error parsing JSON query", e);
			return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		catch (IOException e) {
			return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		catch (MatchExecutionException e) {
			return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<String>(wrapResponse(callback, jsonres), HttpStatus.OK);
	}

	/**
	 * Properties suggest flyout
	 */
	@RequestMapping(value = "/reconcile/{configName}/flyoutProperty/{id:.+}", method={RequestMethod.GET,RequestMethod.POST}, produces="application/json; charset=UTF-8")
	public ResponseEntity<String> doPropertiesFlyout(@PathVariable String configName, @PathVariable String id, @RequestParam(value="callback",required=false) String callback) {
		logger.info("{}: In property flyout for id {}", configName, id);

		try {
			String html = "<html><body><ul><li>"+id+"</li></ul></body></html>\n";
			FlyoutResponse jsonWrappedHtml = new FlyoutResponse(html);

			return new ResponseEntity<String>(wrapResponse(callback, jsonMapper.writeValueAsString(jsonWrappedHtml)), HttpStatus.OK);
		}
		catch (IOException e) {
			logger.warn(configName + ": Error in properties flyout for id "+id, e);
			return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Entity suggest flyout
	 */
	@RequestMapping(value = "/reconcile/{configName}/flyout/{id:.+}", method={RequestMethod.GET,RequestMethod.POST}, produces="application/json; charset=UTF-8")
	public ResponseEntity<String> doSuggestFlyout(@PathVariable String configName, @PathVariable String id, @RequestParam(value="callback",required=false) String callback) {
		logger.info("{}: Suggest flyout request for {}", configName, id);

		try {
			String targetUrl = reconciliationService.getReconciliationServiceConfiguration(configName).getSuggestFlyoutUrl();

			ResponseEntity<String> httpResponse = template.getForEntity(targetUrl, String.class, id);

			if (httpResponse.getStatusCode() != HttpStatus.OK) {
				logger.debug("{}: Received HTTP {} from URL {} with id {}", configName, httpResponse.getStatusCode(), targetUrl, id);
			}

			FlyoutResponse jsonWrappedHtml = new FlyoutResponse(httpResponse.getBody());
			return new ResponseEntity<String>(wrapResponse(callback, jsonMapper.writeValueAsString(jsonWrappedHtml)), httpResponse.getStatusCode());
		}
		catch (MatchExecutionException | NullPointerException e) {
			return new ResponseEntity<String>(e.toString(), HttpStatus.NOT_FOUND);
		}
		catch (IOException e) {
			logger.warn(configName + ": Exception retrieving URL for id "+id, e);
			return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Wrap response into JSON-P if necessary.
	 */
	private String wrapResponse(String callback, String jsonres){
		if (callback != null) {
			return callback + "(" + jsonres + ")";
		}
		else {
			return jsonres;
		}
	}

	/**
	 * Perform match query against specified configuration.
	 */
	private QueryResult[] doQuery(Query q, String configName) throws TooManyMatchesException, MatchExecutionException {
		ArrayList<QueryResult> qr = new ArrayList<QueryResult>();

		org.kew.reconciliation.refine.domain.query.Property[] properties = q.getProperties();
		// If user didn't supply any properties, try converting the query string into properties.
		if (properties == null || properties.length == 0) {
			QueryStringToPropertiesExtractor propertiesExtractor = reconciliationService.getPropertiesExtractor(configName);

			if (propertiesExtractor != null) {
				properties = propertiesExtractor.extractProperties(q.getQuery());
				logger.debug("No properties provided, parsing query «{}» into properties {}", q.getQuery(), properties);
			}
			else {
				logger.info("No properties provided, no properties resulted from parsing query string «{}»", q.getQuery());
			}
		}
		else {
			// If the user supplied some properties, but didn't supply the key property, then it comes from the query
			String keyColumnName = reconciliationService.getReconciliationServiceConfiguration(configName).getProperties().get(0).getQueryColumnName();
			if (!containsProperty(properties, keyColumnName)) {
				properties = Arrays.copyOf(properties, properties.length + 1);

				org.kew.reconciliation.refine.domain.query.Property keyProperty = new org.kew.reconciliation.refine.domain.query.Property();
				keyProperty.setP(keyColumnName);
				keyProperty.setPid(keyColumnName);
				keyProperty.setV(q.getQuery());
				logger.debug("Key property {} taken from query {}", keyColumnName, q.getQuery());

				properties[properties.length-1] = keyProperty;
			}
		}

		if (properties == null || properties.length == 0) {
			logger.info("No properties provided for query «{}», query fails", q.getQuery());
			// no query
			return null;
		}

		// Build a map by looping over each property in the config, reading its value from the
		// request object, and applying any transformations specified in the config
		Map<String, String> userSuppliedRecord = new HashMap<String, String>();
		for (org.kew.reconciliation.refine.domain.query.Property p : properties) {
			if (logger.isTraceEnabled()) { logger.trace("Setting: {} to {}", p.getPid(), p.getV()); }
			userSuppliedRecord.put(p.getPid(), p.getV());
		}

		List<Map<String,String>> matches = reconciliationService.doQuery(configName, userSuppliedRecord);
		logger.debug("Found {} matches", matches.size());

		for (Map<String,String> match : matches) {
			QueryResult res = new QueryResult();
			res.setId(match.get("id"));
			// Set match to true if there's only one (which allows Open Refine to autoselect it), false otherwise
			res.setMatch(matches.size() == 1);
			// Set score to 100/(number of matches)
			res.setScore(100/matches.size());
			// Set name according to format
			res.setName(reconciliationService.getReconciliationResultFormatter(configName).formatResult(match));
			// Set type to default type
			res.setType(reconciliationService.getMetadata(configName).getDefaultTypes());
			qr.add(res);
		}

		return qr.toArray(new QueryResult[qr.size()]);
	}

	private boolean containsProperty(org.kew.reconciliation.refine.domain.query.Property[] properties, String property) {
		if (property == null) return false;
		for (org.kew.reconciliation.refine.domain.query.Property p : properties) {
			if (property.equals(p.getPid())) return true;
		}
		return false;
	}
}
