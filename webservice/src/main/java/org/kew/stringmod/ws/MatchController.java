package org.kew.stringmod.ws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.kew.stringmod.dedupl.configuration.Property;
import org.kew.stringmod.dedupl.lucene.LuceneDataLoader;
import org.kew.stringmod.dedupl.lucene.LuceneMatcher;
import org.kew.stringmod.lib.transformers.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MatchController {
	
	private static Logger logger = LoggerFactory.getLogger(LuceneDataLoader.class);

	@Autowired
	private @Resource(name="configFiles") List<String> configFiles;

	private Map<String, LuceneMatcher> matchers;
	private Map<String, Integer> totals;
	
	private boolean initialised=false;
    
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
	    			List<String> p_t = new ArrayList<>();
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
    public @ResponseBody List<Map<String,String>> doMatch (@PathVariable String configName, HttpServletRequest request, Model model) { 
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
	    		@SuppressWarnings("unchecked")
				Map<String,String[]> params = (Map<String,String[]>) request.getParameterMap();
	    		for(String key : params.keySet()){
	    			userSuppliedRecord.put(key, params.get(key)[0]);
	    			logger.debug(key + ":" + params.get(key)[0]);
	    		}
		    	// We�re now at the same point as if we had read the map from a file 
		    	// Pass this map to the new method in the LuceneMatcher - 
		    	// getMatches(Map<String,String> record) to get a DocList of matches: 
	    		try{
	    			matches = matcher.getMatches(userSuppliedRecord, 0);
	    			// Just write out some matches to std out:
	    			logger.debug("Found some matches: " + matches.size());
	    		}
	    		catch(Exception e){
	    			logger.error("problem handling match", e);
	    		}
	    	}
    	}
    	// matches will be returned as JSON
    	return matches;
    }
    
	public List<String> getConfigFiles() {
		return configFiles;
	}

	public void setConfigFiles(List<String> configFiles) {
		this.configFiles = configFiles;
	}

}