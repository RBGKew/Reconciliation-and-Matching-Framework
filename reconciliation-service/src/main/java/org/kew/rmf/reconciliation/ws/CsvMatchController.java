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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.kew.rmf.core.configuration.Configuration;
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
 * Implements a CSV query interface to the reconciliation configurations, providing file upload processing.
 */
@Controller
public class CsvMatchController {
	private static Logger logger = LoggerFactory.getLogger(CsvMatchController.class);
	private static String tmpDir = System.getProperty("java.io.tmpdir");

	@Autowired
	private ReconciliationService reconciliationService;

	@Autowired
	private BaseController baseController;

	/**
	 * Matches the records in the uploaded file.
	 *
	 * Results are put into a temporary file (available for download) and also shown in the web page.
	 */
	@RequestMapping(value = "/filematch/{configName}", method = RequestMethod.POST)
	public String doFileMatch (@PathVariable String configName, @RequestParam("file") MultipartFile file, @RequestParam("charset") String charset, HttpServletResponse response, Model model) {
		logger.info("{}: File match query {}, {}", configName, file, charset);

		if (file.isEmpty()) {
			logger.warn("File is empty");
			baseController.menuAndBreadcrumbs("/filematch/"+configName, model);
			model.addAttribute("error", "CSV file is empty.");
			return "file-matcher-error";
		}

		logger.debug("Looking for: {}", configName);
		LuceneMatcher matcher;
		try {
			matcher = reconciliationService.getMatcher(configName);
		}
		catch (MatchExecutionException e) {
			logger.error("Problem retrieving matcher "+configName, e);
			baseController.menuAndBreadcrumbs("/filematch/"+configName, model);
			model.addAttribute("error", "Matcher "+configName+" not found or not available.");
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return "file-matcher-error";
		}
		if (matcher == null) {
			baseController.menuAndBreadcrumbs("/filematch/"+configName, model);
			model.addAttribute("error", "Matcher "+configName+" not found or not available.");
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return "file-matcher-error";
		}

		// Map of matches
		// Key is the ID of supplied records
		// Entries are a List of Map<String,String>
		Map<String,List<Map<String,String>>> matches = new HashMap<String,List<Map<String,String>>>();

		// Map of supplied data (useful for display)
		List<Map<String,String>> suppliedData = new ArrayList<Map<String,String>>();

		// Temporary file for results
		File resultsFile;
		OutputStreamWriter resultsFileWriter;

		List<String> unusedCsvFields = new ArrayList<>();

		// Property field names
		List<String> properties = new ArrayList<String>();
		for (Property p : matcher.getConfig().getProperties()) {
			properties.add(p.getQueryColumnName());
		}

		try {
			// Open file and read first line to determine line ending
			BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), Charset.forName(charset)));
			String lineEnding;
			String userLineEnding;
			boolean crlf = useCrLfEndings(fileReader);
			lineEnding = crlf ? "\r\n" : "\n";
			userLineEnding = crlf ? "Windows (CR+LF)" : "Linux/Mac (LF)";

			// Read CSV from file
			CsvPreference customCsvPref = new CsvPreference.Builder('"', ',', lineEnding).build(); // lineEnding is only used for writing anyway.
			CsvMapReader mr = new CsvMapReader(fileReader, customCsvPref);
			final String[] header = mr.getHeader(true);

			// Check CSV file has appropriate headers
			// Need "id" and at least one from properties
			if (!validateCsvFields(header, properties, unusedCsvFields)) {
				mr.close();
				fileReader.close();

				logger.warn("Uploaded CSV doesn't contain id column.");
				baseController.menuAndBreadcrumbs("/filematch/"+configName, model);
				model.addAttribute("error", "CSV file doesn't contain 'id' column, or is corrupt.");
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				return "file-matcher-error";
			}

			// Open results file and write header
			resultsFile = File.createTempFile("match-results-", ".csv");
			resultsFileWriter = new OutputStreamWriter(new FileOutputStream(resultsFile), "UTF-8");
			resultsFileWriter.write("queryId,matchId");
			resultsFileWriter.write(lineEnding);

			// Process each line of the input CSV, append results to output file
			Map<String, String> record = null;
			while ((record = mr.read(header)) != null) {
				logger.debug("Next record is {}", record);
				suppliedData.add(record);
				try {
					List<Map<String,String>> theseMatches = matcher.getMatches(record);
					if (theseMatches != null) {
						logger.debug("Record ID {}, matched: {}", record.get(Configuration.ID_FIELD_NAME), theseMatches.size());
					}
					else {
						logger.debug("Record ID {}, matched: null", record.get(Configuration.ID_FIELD_NAME));
					}
					matches.put(record.get(Configuration.ID_FIELD_NAME), theseMatches);

					// Append match results to file
					StringBuilder sb = new StringBuilder();
					for (Map<String,String> result : theseMatches) {
						if (sb.length() > 0) sb.append('|');
						sb.append(result.get(Configuration.ID_FIELD_NAME));
					}
					sb.insert(0, ',').insert(0, record.get(Configuration.ID_FIELD_NAME)).append(lineEnding);
					resultsFileWriter.write(sb.toString());
				}
				catch (TooManyMatchesException | MatchExecutionException e) {
					logger.error("Problem handling match", e);
				}
			}

			// Close file etc
			mr.close();
			resultsFileWriter.close();

			model.addAttribute("resultsFile", resultsFile.getName());
			response.setHeader("X-File-Download", resultsFile.getName()); // Putting this in a header saves the unit tests from needing to parse the HTML.

			model.addAttribute("suppliedData", suppliedData);
			model.addAttribute("matches", matches);
			model.addAttribute("properties", properties);
			model.addAttribute("unusedCsvFields", unusedCsvFields);
			model.addAttribute("userLineEnding", userLineEnding);
			model.addAttribute("charset", charset);
		}
		catch (Exception e) {
			logger.error("Problem reading CSV file", e);
			baseController.menuAndBreadcrumbs("/filematch/"+configName, model);
			model.addAttribute("error", "Problem reading CSV file: "+e.getMessage());
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			return "file-matcher-error";
		}

		baseController.menuAndBreadcrumbs("/filematch/"+configName, model);
		return "file-matcher-results";
	}

	/**
	 * Returns true if the input file's line endings are likely to be Windows-style (\r\n).
	 */
	private boolean useCrLfEndings(Reader inputStream) {
		if (inputStream.markSupported()) {
			try {
				inputStream.mark(4096);
				int i = 0;
				while(i < 4096) {
					int c = inputStream.read();

					if (c == '\n') {
						inputStream.reset();
						return false;
					}

					if (c == '\r') {
						inputStream.reset();
						return true;
					}
				}

				logger.warn("Detecting line endings not possible, first line is very long");
				return false;
			}
			catch (IOException e) {
				logger.warn("Exception determining CSV input file line ending", e);
				return false;
			}
		}
		else {
			logger.warn("Detecting line endings not possible, mark not supported");
			return false;
		}
	}

	/**
	 * Checks for the presence of an "id" field, and returns all strings in fields but not in properties.
	 */
	private boolean validateCsvFields(String[] fields, List<String> properties, List<String> unusedFields) {
		boolean containsId = false;
		for (String f : fields) {
			if (Configuration.ID_FIELD_NAME.equals(f)) {
				containsId = true;
			}
			else {
				if (!properties.contains(f)) {
					unusedFields.add(f);
				}
			}
		}

		return containsId;
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
