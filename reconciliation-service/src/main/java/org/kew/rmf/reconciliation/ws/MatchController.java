/*
 * Reconciliation and Matching Framework
 * Copyright © 2014 Royal Botanic Gardens, Kew
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kew.rmf.reconciliation.ws;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.kew.rmf.core.configuration.Property;
import org.kew.rmf.core.exception.MatchExecutionException;
import org.kew.rmf.core.exception.TooManyMatchesException;
import org.kew.rmf.core.lucene.LuceneMatcher;
import org.kew.rmf.reconciliation.service.ReconciliationService;
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
import org.springframework.web.multipart.MultipartFile;
import org.supercsv.io.CsvMapReader;
import org.supercsv.prefs.CsvPreference;

/**
 * Implements a custom HTTP query interface to the reconciliation configurations, providing JSON responses
 * and file upload processing.
 */
@Controller
public class MatchController {
	private static Logger logger = LoggerFactory.getLogger(MatchController.class);
	private static String tmpDir = System.getProperty("java.io.tmpdir");

	@Autowired
	private ReconciliationService reconciliationService;

	/**
	 * Performs a single match query.
	 */
	@RequestMapping(value = "/match/{configName}", method = RequestMethod.GET)
	public ResponseEntity<List<Map<String,String>>> doMatch (@PathVariable String configName, @RequestParam Map<String,String> requestParams, Model model) {
		logger.info("{}: Match query {}", configName, requestParams);

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
			logger.error(configName + ": Problem handling match", e);
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
		logger.info("{}: File match query {}", configName, file);

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
						properties.add(p.getQueryColumnName());
					}

					CsvPreference customCsvPref = new CsvPreference.Builder('"', ',', "\n").build();
					CsvMapReader mr = new CsvMapReader(new InputStreamReader(file.getInputStream(), "UTF-8"), customCsvPref);
					final String[] header = mr.getHeader(true);
					Map<String, String> record = null;
					while ((record = mr.read(header)) != null) {
						logger.debug("Next record is {}", record);
						suppliedData.add(record);
						try {
							List<Map<String,String>> theseMatches = matcher.getMatches(record);
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
}
