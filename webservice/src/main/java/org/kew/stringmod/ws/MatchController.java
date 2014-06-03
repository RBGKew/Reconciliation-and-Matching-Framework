package org.kew.stringmod.ws;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.kew.reconciliation.refine.domain.metadata.Metadata;
import org.kew.reconciliation.refine.domain.metadata.MetadataView;
import org.kew.reconciliation.refine.domain.query.Query;
import org.kew.reconciliation.refine.domain.response.QueryResponse;
import org.kew.reconciliation.refine.domain.response.QueryResult;
import org.kew.stringmod.dedupl.configuration.Property;
import org.kew.stringmod.dedupl.exception.MatchExecutionException;
import org.kew.stringmod.dedupl.exception.TooManyMatchesException;
import org.kew.stringmod.dedupl.lucene.LuceneMatcher;
import org.kew.stringmod.lib.transformers.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
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

	@Value("#{'${configFiles}'.split(',')}")
	private List<String> configFiles;

	private Map<String, LuceneMatcher> matchers;
	private Map<String, Integer> totals;
	
	private boolean initialised=false;

	/* Added for recon service */
	@Autowired
	protected ObjectMapper jsonMapper;
	private String[] types;
	
    @PostConstruct
    public void init() {
    	logger.debug("initiaising match controller");
    	if (!initialised){
	       // Load up the matchers from the specified files
	    	matchers = new HashMap<String, LuceneMatcher>();
	    	totals = new HashMap<String, Integer>();
	    	if (configFiles != null){
		    	for (String configFile : configFiles){
		    		logger.debug("processing config: " + configFile);
		    		ConfigurableApplicationContext context = new GenericXmlApplicationContext(configFile);
		    		LuceneMatcher matcher = (LuceneMatcher) context.getBean("engine");
		    		try{
			    		matcher.loadData(); 
			    		logger.debug("loaded data");
			    		String configName = matcher.getConfig().getName(); 
			    		matchers.put(configName, matcher);
			    		totals.put(configName, matcher.getIndexReader().numDocs());
			    		logger.debug("Stored matcher with name: " + configName);
		    		}
		    		catch(Exception e){
		    			logger.error("Problem initialising handler w. config", e);
		    		}
		    	}
	    	}
	    	initialised=true;
    	}
    }

    @RequestMapping(produces="text/html", value = "/about", method = RequestMethod.GET)
    public String doWelcome(Model model) {
    	if (matchers != null)
    		model.addAttribute("availableMatchers", matchers.keySet());
    	return "about-general";
    }

    @RequestMapping(produces="text/html", value = "/about/{configName}", method = RequestMethod.GET)
    public String doAbout(@PathVariable String configName, Model model) {
    	List<String> properties = new ArrayList<String>();
    	Map<String,String> p_matchers = new HashMap<String,String>();
    	Map<String,List<String>> p_transformers = new HashMap<String,List<String>>();
    	if (matchers != null){
    		logger.debug("Looking for : " + configName);
    		LuceneMatcher matcher = matchers.get(configName);
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
	    		if (totals.containsKey(configName))
	    			model.addAttribute("total", totals.get(configName));
	    		model.addAttribute("configName", configName);
	    		model.addAttribute("properties", properties);
	    		model.addAttribute("matchers", p_matchers);
	    		model.addAttribute("transformers", p_transformers);
    		}
    	}
    	return "about-matcher";
    }
    
    @RequestMapping(value = "/match/{configName}", method = RequestMethod.GET)
    public synchronized ResponseEntity<List<Map<String,String>>> doMatch (@PathVariable String configName
    														, @RequestParam Map<String,String> requestParams
    														, Model model) {
    	logger.info("Match query for {}?{}", configName, requestParams);
    	List<Map<String,String>> matches = null;
    	// Assuming that multiple configurations may be accessed from a single webapp, 
    	// look for the one with the specified name (keyed to this in a map as explained above)
    	if (matchers != null){
    		logger.debug("Looking for : " + configName);
    		LuceneMatcher matcher = matchers.get(configName);
	    	if (matcher != null){
		    	// Build a map by looping over each property in the config, reading its value from the 
		    	// request object, and applying any transformations specified in the config
	    		Map<String, String> userSuppliedRecord = new HashMap<String, String>();
	    		for(String key : requestParams.keySet()){
	    			userSuppliedRecord.put(key, requestParams.get(key));
	    			logger.debug(key + ":" + requestParams.get(key));
	    		}
		    	// We are now at the same point as if we had read the map from a file
		    	// Pass this map to the new method in the LuceneMatcher - 
		    	// getMatches(Map<String,String> record) to get a DocList of matches: 
	    		try{
	    			matches = matcher.getMatches(userSuppliedRecord,10);
	    			logger.debug("Found some matches: {}", matches.size());
	    			if (matches.size() < 4) {
	    				logger.debug("Matches for {} are {}", requestParams, matches);
	    			}
	    		}
	    		catch (TooManyMatchesException | MatchExecutionException e) {
	    			logger.error("problem handling match", e);
	    			return new ResponseEntity<List<Map<String,String>>>(HttpStatus.INTERNAL_SERVER_ERROR);
	    		}
	    	}
    	}
    	// matches will be returned as JSON
    	return new ResponseEntity<List<Map<String,String>>>(matches, HttpStatus.OK);
    }

    @RequestMapping(value = "/filematch/{configName}", method = RequestMethod.POST)
    public String doFileMatch (@PathVariable String configName
    							, @RequestParam("file") MultipartFile file
    							, Model model) {
    	// Map of matches
    	// Key is the ID of supplied records
    	// Entries are a List of Map<String,String>
    	Map<String,List<Map<String,String>>> matches = new HashMap<String,List<Map<String,String>>>();
    	// Map of supplied data (useful for display)
    	List<Map<String,String>> suppliedData = new ArrayList<Map<String,String>>();
        List<String> properties = new ArrayList<String>();
    	if (!file.isEmpty()) {
        	try{
            	if (matchers != null){
            		logger.debug("Looking for : " + configName);
            		LuceneMatcher matcher = matchers.get(configName);
        	    	if (matcher != null){
        	    		// Save the property names:
        	    		for (Property p : matcher.getConfig().getProperties())
        	    			properties.add(p.getSourceColumnName());
        	    		CsvPreference customCsvPref = new CsvPreference.Builder('"', ',', "\n").build();
        	    		CsvMapReader mr = new CsvMapReader(new InputStreamReader(file.getInputStream()), customCsvPref);
        	    		final String[] header = mr.getHeader(true);
        	    		Map<String, String> record = null;
        	    	    while ((record = mr.read(header)) != null){
        	    	    	logger.debug("Next record is {}", record);
        	    	    	suppliedData.add(record);
        	    	    	try{
        	    	    		List<Map<String,String>> theseMatches = matcher.getMatches(record, 5);
	        	    			// Just write out some matches to std out:
        	    	    		if (theseMatches != null) {
        	    	    			logger.debug("Record ID {}, matched: {}", record.get("id"), theseMatches.size());
        	    	    		}
        	    	    		else {
        	    	    			logger.debug("Record ID {}, matched: null", record.get("id"));
        	    	    		}
	        	    			matches.put(record.get("id"), theseMatches);
        	    	    	}
        	    	    	catch(Exception e){
        	    	    		// swallow inner exception
        	    	    		logger.error("Exception processing record", e);
        	    	    	}
        	    	    }
        	    	    mr.close();
        	    		logger.debug("got file's bytes");
        	    	}
        	    	model.addAttribute("suppliedData", suppliedData);
        	    	model.addAttribute("matches", matches);
        	    	model.addAttribute("properties", properties);
            	}
        	}
        	catch(Exception e){
        		logger.error("Problem reading file", e);
        	}
            // store the bytes somewhere
           //return "redirect:uploadSuccess";
       } 
       return "file-matcher-results";
    }    
    
    
	public List<String> getConfigFiles() {
		return configFiles;
	}

	public void setConfigFiles(List<String> configFiles) {
		this.configFiles = configFiles;
	}

	//
	// Following stuff added for GR recon service
	//
    @RequestMapping(value = "/reconcile/{configName}"
    			, method={RequestMethod.GET,RequestMethod.POST}
    			, produces="application/json; charset=UTF-8")
    public @ResponseBody String getMetadata(@PathVariable String configName
    										, @RequestParam(value="callback",required=false) String callback
    										, Model model) throws JsonGenerationException, JsonMappingException, IOException{
    	logger.debug("In get metadata");
    	String metadata = jsonMapper.writeValueAsString(getMetadata(configName));
		// Work out if the response needs to be JSONP wrapped in a callback		
		if (callback != null)
				return callback + "(" + metadata + ")";
		else
			return metadata;
	}

    @RequestMapping(value = "/reconcile/{configName}"
	, method={RequestMethod.GET,RequestMethod.POST}
    , params={"queries"}
	, produces="application/json; charset=UTF-8")
    public @ResponseBody String doMultipleQueries(@PathVariable String configName
    		, @RequestParam("queries") String queries) {
    	logger.debug("In multiple query, queries:" + queries);
		return doMultipleQueries(configName, queries, null);
	}
	

    @RequestMapping(value = "/reconcile/{configName}"
	, method={RequestMethod.GET,RequestMethod.POST}
    , params={"queries","callback"}
	, produces="application/json; charset=UTF-8")
    public @ResponseBody String doMultipleQueries(@PathVariable String configName
    						, @RequestParam("queries") String queries
							, @RequestParam(value="callback",required=false) String callback) {
    	logger.debug("In multi query w callback, query: {}", queries);
    	String jsonres = null;
		Map<String,QueryResponse> res = new HashMap<String,QueryResponse>();
		try{
			// Convert JSON to map of queries
			Map<String,Query> qs = jsonMapper.readValue(queries, new TypeReference<Map<String,Query>>() { });
			for (String key : qs.keySet()){
				Query q = qs.get(key);
				QueryResult[] qres = doQuery(q, configName);
				QueryResponse response = new QueryResponse();
				response.setResult(qres);
				res.put(key,response);
			}
			jsonres = jsonMapper.writeValueAsString(res);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return wrapResponse(callback, jsonres);
	}
		   
    @RequestMapping(value = "/reconcile/{configName}"
    	, method={RequestMethod.GET,RequestMethod.POST}
		, params={"query"}
		,produces="application/json; charset=UTF-8")
	public ResponseEntity<String> doSingleQuery(@PathVariable String configName, @RequestParam("query") String query) {
    	logger.debug("In single query, query:{}", query);
		return doSingleQuery(configName, query, null);
	}
		
    @RequestMapping(value = "/reconcile/{configName}"
    	, method={RequestMethod.GET,RequestMethod.POST}
		, params={"query","callback"}
		,produces="application/json; charset=UTF-8")
	public ResponseEntity<String> doSingleQuery(@PathVariable String configName, @RequestParam("query") String query
							, @RequestParam(value="callback",required=false) String callback) {
		String jsonres = null;
		logger.debug("In single query w callback, query: {}", query);
		try{
			Query q = jsonMapper.readValue(query, Query.class);
			QueryResult[] qres = doQuery(q, configName);
			QueryResponse response = new QueryResponse();
			response.setResult(qres);
			jsonres = jsonMapper.writeValueAsString(response);
		}
		catch(Exception e){
			logger.error("", e);
			return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String>(wrapResponse(callback, jsonres), HttpStatus.OK);
	}

	private String wrapResponse(String callback, String jsonres){
		if (callback != null)
			return callback + "(" + jsonres + ")";
		else
			return jsonres;
	}

	public String[] getTypes() {
		return types;
	}

	public void setTypes(String[] types) {
		this.types = types;
	}	

	private Metadata getMetadata(String configName){
		Metadata m = new Metadata();
		
		// Set up the metadata object with whatever is set in the specified config:
		// For now just push in the same config no matter what:
		m.setName(configName + " name reconciliation");
		m.setIdentifierSpace("http://www.ipni.org");
		m.setSchemaSpace("http://www.ipni.org");
		MetadataView mv = new MetadataView();
		mv.setUrl("http://www.ipni.org/ipni/idPlantNameSearch.do?id={{id}}");
		m.setView(mv);
		return m;
	}
	
	private QueryResult[] doQuery(Query q, String configName){
		List<QueryResult> qr = new ArrayList<QueryResult>();
		//
		List<Map<String,String>> matches = null;
    	// Assuming that multiple configurations may be accessed from a single webapp, 
    	// look for the one with the specified name (keyed to this in a map as explained above)
    	if (matchers != null){
    		logger.debug("Looking for : {}", configName);
    		LuceneMatcher matcher = matchers.get(configName);
	    	if (matcher != null){
		    	// Build a map by looping over each property in the config, reading its value from the 
		    	// request object, and applying any transformations specified in the config
	    		Map<String, String> userSuppliedRecord = new HashMap<String, String>();
	    		for (org.kew.reconciliation.refine.domain.query.Property p : q.getProperties()){
	    			logger.debug("Setting: {} to {}", p.getPid(), p.getV());
	    			userSuppliedRecord.put(p.getPid(), p.getV());
	    		}
	    		
	    		try{
	    			matches = matcher.getMatches(userSuppliedRecord, 5);
	    			// Just write out some matches to std out:
	    			logger.debug("GR Found some matches: {}", matches.size());
	    			if (matches.size() < 4) {
	    				logger.debug("GR Matches for {} are {}", q, matches);
	    			}
	    		}
	    		catch(Exception e){
	    			logger.error("problem handling match", e);
	    		}
	    	}
    	}
    	for (Map<String,String> match : matches){
    		QueryResult res = new QueryResult();
    		res.setId(match.get("id"));
    		res.setMatch(true);
    		res.setScore(100);
    		res.setName(match.get("genus") + " " + match.get("species") + " " + match.get("authors"));
    		String[] types = {"name"};
    		res.setType(types);
    		qr.add(res);
    	}
		//
		return qr.toArray(new QueryResult[0]);
	}
}
