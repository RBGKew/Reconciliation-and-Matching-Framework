package org.kew.stringmod.ws;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.kew.reconciliation.queryextractor.QueryStringToPropertiesExtractor;
import org.kew.reconciliation.refine.domain.metadata.Metadata;
import org.kew.reconciliation.refine.domain.metadata.Type;
import org.kew.reconciliation.refine.domain.query.Query;
import org.kew.reconciliation.refine.domain.response.QueryResponse;
import org.kew.reconciliation.refine.domain.response.QueryResult;
import org.kew.reconciliation.service.ReconciliationService;
import org.kew.stringmod.dedupl.configuration.Property;
import org.kew.stringmod.dedupl.exception.MatchExecutionException;
import org.kew.stringmod.dedupl.exception.TooManyMatchesException;
import org.kew.stringmod.dedupl.lucene.LuceneMatcher;
import org.kew.stringmod.lib.transformers.Transformer;
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
import org.springframework.web.multipart.MultipartFile;
import org.supercsv.io.CsvMapReader;
import org.supercsv.prefs.CsvPreference;

@Controller
public class MatchController {
	private static Logger logger = LoggerFactory.getLogger(MatchController.class);
	private static String tmpDir = System.getProperty("java.io.tmpdir");

	@Autowired
	private ReconciliationService reconciliationService;

	@Autowired
	private ServletContext servletContext;

	@Autowired
	private ObjectMapper jsonMapper;


	@RequestMapping(produces="text/html", value={"/","/about"}, method = RequestMethod.GET)
	public String doWelcome(Model model) {
		model.addAttribute("availableMatchers", reconciliationService.getMatchers().keySet());
		return "about-general";
	}

    @RequestMapping(produces="text/html", value = "/about/{configName}", method = RequestMethod.GET)
    public String doAbout(@PathVariable String configName, Model model) {
    	List<String> properties = new ArrayList<String>();
    	Map<String,String> p_matchers = new HashMap<String,String>();
    	Map<String,List<String>> p_transformers = new HashMap<String,List<String>>();

		LuceneMatcher matcher;
		try {
			matcher = reconciliationService.getMatcher(configName);
		}
		catch (MatchExecutionException e) {
			// TODO: Make a 404.
			return "about-matcher";
		}

		if (matcher != null){
			model.addAttribute("matchConfig", matcher.getConfig());
			for (Property p : matcher.getConfig().getProperties()){
				properties.add(p.getSourceColumnName());
				p_matchers.put(p.getSourceColumnName(), p.getMatcher().getClass().getCanonicalName());
				List<String> p_t = new ArrayList<String>();
				for (Transformer t : p.getSourceTransformers()){
					p_t.add(t.getClass().getCanonicalName());
				}
				p_transformers.put(p.getSourceColumnName(), p_t);
			}
			//if (totals.containsKey(configName))
			//	model.addAttribute("total", totals.get(configName));
			model.addAttribute("configName", configName);
			model.addAttribute("properties", properties);
			model.addAttribute("matchers", p_matchers);
			model.addAttribute("transformers", p_transformers);
		}
    	return "about-matcher";
    }

	/**
	 * Performs a single match query.
	 */
	@RequestMapping(value = "/match/{configName}", method = RequestMethod.GET)
	public ResponseEntity<List<Map<String,String>>> doMatch (@PathVariable String configName, @RequestParam Map<String,String> requestParams, Model model) {
		logger.info("Match query for {}?{}", configName, requestParams);

		// Build a map by looping over each property in the config, reading its value from the
		// request object, and applying any transformations specified in the config
		Map<String, String> userSuppliedRecord = new HashMap<String, String>();
		for (String key : requestParams.keySet()) {
			userSuppliedRecord.put(key, requestParams.get(key));
			if (logger.isTraceEnabled()) { logger.trace("Setting: {} to {}", key, requestParams.get(key)); }
		}

		List<Map<String, String>> matches = null;
		try {
			matches = reconciliationService.doQuery(configName, userSuppliedRecord);
			logger.debug("Found {} matches", matches.size());
			if (matches.size() < 4) {
				logger.debug("Matches for {} are {}", requestParams, matches);
			}
		}
		catch (TooManyMatchesException | MatchExecutionException e) {
			logger.error("Problem handling match", e);
			return new ResponseEntity<List<Map<String,String>>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// TODO: Reporter is needed to cause only configured properties to be returned in the JSON.

		// matches will be returned as JSON
		return new ResponseEntity<List<Map<String,String>>>(matches, HttpStatus.OK);
	}

	/**
	 * Matches the records in the uploaded file.
	 *
	 * Results are put into a temporary file (available for download) and also shown in the web page.
	 */
	@RequestMapping(value = "/filematch/{configName}", method = RequestMethod.POST)
	public String doFileMatch (@PathVariable String configName, @RequestParam("file") MultipartFile file, HttpServletResponse response, Model model) {
		// Map of matches
		// Key is the ID of supplied records
		// Entries are a List of Map<String,String>
		Map<String,List<Map<String,String>>> matches = new HashMap<String,List<Map<String,String>>>();

		// Map of supplied data (useful for display)
		List<Map<String,String>> suppliedData = new ArrayList<Map<String,String>>();

		// Temporary file for results
		File resultsFile;

		List<String> properties = new ArrayList<String>();
		if (!file.isEmpty()) {
			try {
				logger.debug("Looking for : " + configName);
				LuceneMatcher matcher = reconciliationService.getMatcher(configName);
				if (matcher != null) {
					resultsFile = File.createTempFile("match-results-", ".csv");
					OutputStreamWriter resultsFileWriter = new OutputStreamWriter(new FileOutputStream(resultsFile), "UTF-8");
					resultsFileWriter.write("queryId,matchId\n"); // TODO: attempt to use same line ending as input file

					// Save the property names:
					for (Property p : matcher.getConfig().getProperties()) {
						properties.add(p.getSourceColumnName());
					}

					CsvPreference customCsvPref = new CsvPreference.Builder('"', ',', "\n").build();
					CsvMapReader mr = new CsvMapReader(new InputStreamReader(file.getInputStream(), "UTF-8"), customCsvPref);
					final String[] header = mr.getHeader(true);
					Map<String, String> record = null;
					while ((record = mr.read(header)) != null) {
						logger.debug("Next record is {}", record);
						suppliedData.add(record);
						try {
							List<Map<String,String>> theseMatches = matcher.getMatches(record, 5);
							// Just write out some matches to std out:
							if (theseMatches != null) {
								logger.debug("Record ID {}, matched: {}", record.get("id"), theseMatches.size());
							}
							else {
								logger.debug("Record ID {}, matched: null", record.get("id"));
							}
							matches.put(record.get("id"), theseMatches);

							// Append matche results to file
							StringBuilder sb = new StringBuilder();
							for (Map<String,String> result : theseMatches) {
								if (sb.length() > 0) sb.append('|');
								sb.append(result.get("id"));
							}
							sb.insert(0, ',').insert(0, record.get("id")).append("\n");
							resultsFileWriter.write(sb.toString());
						}
						catch (TooManyMatchesException | MatchExecutionException e) {
							logger.error("Problem handling match", e);
						}
					}
					mr.close();
					resultsFileWriter.close();
					logger.debug("got file's bytes");
					model.addAttribute("resultsFile", resultsFile.getName());
					response.setHeader("X-File-Download", resultsFile.getName()); // Putting this in a header saves the unit tests from needing to parse the HTML.
				}
				model.addAttribute("suppliedData", suppliedData);
				model.addAttribute("matches", matches);
				model.addAttribute("properties", properties);
			}
			catch (Exception e) {
				logger.error("Problem reading file", e);
			}
		}
		return "file-matcher-results";
	}

	/**
	 * Downloads a match result file.
	 */
	@RequestMapping(value = "/download/{fileName}", method = RequestMethod.GET)
	public ResponseEntity<String> doDownload(@PathVariable String fileName, Model model) {
		logger.info("User attempting to download file named «{}»", fileName);

		// Check for the user trying to do something suspicious
		if (fileName.contains(File.separator)) {
			logger.error("User attempting to download file named «{}»", fileName);
			return new ResponseEntity<String>("Looks dodgy.", HttpStatus.FORBIDDEN);
		}

		// Put back the .csv, as Spring has chopped it off.
		File downloadFile = new File(tmpDir, fileName + ".csv");

		try {
			if (downloadFile.canRead()) {
				return new ResponseEntity<String>(FileUtils.readFileToString(downloadFile, "UTF-8"), HttpStatus.OK);
			}
			else {
				logger.warn("User attempted to download file «{}» but it doesn't exist", fileName);
				return new ResponseEntity<String>("This download does not exist", HttpStatus.NOT_FOUND);
			}
		}
		catch (IOException e) {
			logger.error("Exception when user attempted to download file «{}»", fileName);
			return new ResponseEntity<String>("Error retrieving download: "+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/* • Open Refine / Google Refine reconciliation service • */

	/**
	 * Retrieve reconciliation service metadata.
	 */
	@RequestMapping(value = "/reconcile/{configName}",
			method={RequestMethod.GET,RequestMethod.POST},
			produces="application/json; charset=UTF-8")
	public ResponseEntity<String> getMetadata(HttpServletRequest request, @PathVariable String configName, @RequestParam(value="callback",required=false) String callback, Model model) throws JsonGenerationException, JsonMappingException, IOException {
		logger.debug("Get Metadata for config {}, callback {}", configName, callback);

		String myUrl = request.getScheme() + "://" + request.getServerName() + (request.getServerPort() == 80 ? "" : (":" + request.getServerPort()));
		String basePath = servletContext.getContextPath() + "/reconcile/" + configName;

		Metadata metadata;
		try {
			metadata = reconciliationService.getMetadata(configName);
		}
		catch (MatchExecutionException e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		}

		if (metadata != null) {
			String metadataJson = jsonMapper.writeValueAsString(metadata).replace("LOCAL", myUrl).replace("BASE", basePath);
			// Work out if the response needs to be JSONP wrapped in a callback
			if (callback != null) {
				return new ResponseEntity<String>(callback + "(" + metadataJson + ")", HttpStatus.OK);
			}
			else {
				return new ResponseEntity<String>(metadataJson, HttpStatus.OK);
			}
		}
		return null;
	}

	/**
	 * Perform multiple reconciliation queries (no callback)
	 */
	@RequestMapping(value = "/reconcile/{configName}", method={RequestMethod.GET,RequestMethod.POST}, params={"queries"}, produces="application/json; charset=UTF-8")
	public @ResponseBody String doMultipleQueries(@PathVariable String configName, @RequestParam("queries") String queries) {
		logger.debug("In multiple query, queries:" + queries);
		return doMultipleQueries(configName, queries, null);
	}

	/**
	 * Perform multiple reconciliation queries (no callback)
	 */
	@RequestMapping(value = "/reconcile/{configName}", method={RequestMethod.GET,RequestMethod.POST}, params={"queries","callback"}, produces="application/json; charset=UTF-8")
	public @ResponseBody String doMultipleQueries(@PathVariable String configName, @RequestParam("queries") String queries, @RequestParam(value="callback",required=false) String callback) {
		logger.debug("In multi query w callback, query: {}", queries);
		String jsonres = null;
		Map<String,QueryResponse<QueryResult>> res = new HashMap<>();
		try {
			// Convert JSON to map of queries
			Map<String,Query> qs = jsonMapper.readValue(queries, new TypeReference<Map<String,Query>>() { });
			for (String key : qs.keySet()){
				Query q = qs.get(key);
				QueryResult[] qres = doQuery(q, configName);
				QueryResponse<QueryResult> response = new QueryResponse<>();
				response.setResult(qres);
				res.put(key,response);
			}
			jsonres = jsonMapper.writeValueAsString(res);
		}
		catch (Exception e) {
			logger.error("Error with multiple query call", e);
		}
		return wrapResponse(callback, jsonres);
	}

	/**
	 * Single reconciliation query, no callback.
	 */
	@RequestMapping(value = "/reconcile/{configName}", method={RequestMethod.GET,RequestMethod.POST}, params={"query"}, produces="application/json; charset=UTF-8")
	public ResponseEntity<String> doSingleQuery(@PathVariable String configName, @RequestParam("query") String query) {
		logger.debug("In single query, query:{}", query);
		return doSingleQuery(configName, query, null);
	}

	/**
	 * Single reconciliation query.
	 */
	@RequestMapping(value = "/reconcile/{configName}", method={RequestMethod.GET,RequestMethod.POST}, params={"query","callback"}, produces="application/json; charset=UTF-8")
	public ResponseEntity<String> doSingleQuery(@PathVariable String configName, @RequestParam("query") String query, @RequestParam(value="callback",required=false) String callback) {
		String jsonres = null;
		logger.debug("In single query with callback, query: {}", query);
		try {
			Query q = jsonMapper.readValue(query, Query.class);
			QueryResult[] qres = doQuery(q, configName);
			QueryResponse<QueryResult> response = new QueryResponse<>();
			response.setResult(qres);
			jsonres = jsonMapper.writeValueAsString(response);
		}
		catch (JsonMappingException | JsonGenerationException e) {
			logger.warn("Error parsing JSON query", e);
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
		logger.debug("In suggest query with callback, prefix: {}", prefix);
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
		String jsonres = null;
		logger.debug("In suggest type query with callback, query: {}", prefix);
		try {
			Type[] defaultTypes = reconciliationService.getMetadata(configName).getDefaultTypes();
			QueryResponse<Type> response = new QueryResponse<>();
			response.setResult(defaultTypes);
			jsonres = jsonMapper.writeValueAsString(response);
		}
		catch (JsonMappingException | JsonGenerationException e) {
			logger.warn("Error parsing JSON query", e);
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
		String jsonres = null;
		logger.debug("In suggest property query with callback, query: {}", prefix);
		try {
			List<Type> filteredProperties = new ArrayList<>();
			List<Property> properties = reconciliationService.getReconciliationServiceConfiguration(configName).getProperties();
			for (Property p : properties) {
				String name = p.getSourceColumnName();

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
			logger.warn("Error parsing JSON query", e);
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

	@RequestMapping(produces="text/html", value={"/help"}, method = RequestMethod.GET)
	public String doHelp(Model model) {
		return "help";
	}

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
	 * @throws MatchExecutionException 
	 * @throws TooManyMatchesException 
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
			String keyColumnName = reconciliationService.getReconciliationServiceConfiguration(configName).getProperties().get(0).getSourceColumnName();
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
