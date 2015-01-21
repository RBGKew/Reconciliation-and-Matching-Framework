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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kew.rmf.core.exception.MatchExecutionException;
import org.kew.rmf.core.exception.TooManyMatchesException;
import org.kew.rmf.reconciliation.exception.UnknownReconciliationServiceException;
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

/**
 * Implements a custom HTTP query interface to the reconciliation configurations, providing JSON responses.
 */
@Controller
public class MatchController {
	private static Logger logger = LoggerFactory.getLogger(MatchController.class);

	@Autowired
	private ReconciliationService reconciliationService;

	@Autowired
	private BaseController baseController;

	/**
	 * Performs a single match query.
	 */
	@RequestMapping(value = "/match/{configName}", method = RequestMethod.GET)
	public ResponseEntity<List<Map<String,String>>> doMatch (@PathVariable String configName, @RequestParam Map<String,String> requestParams, Model model) throws UnknownReconciliationServiceException {
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
			return new ResponseEntity<List<Map<String,String>>>(null, baseController.getResponseHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// TODO: Reporter is needed to cause only configured properties to be returned in the JSON.

		// matches will be returned as JSON
		return new ResponseEntity<List<Map<String,String>>>(matches, baseController.getResponseHeaders(), HttpStatus.OK);
	}
}
