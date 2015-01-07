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
package org.kew.rmf.reconciliation.queryextractor;

import java.util.ArrayList;
import java.util.List;

import org.kew.rmf.refine.domain.query.Property;
import org.perf4j.LoggingStopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts a specimen citation into a set of properties.
 */
public class SpecimenCitationToPropertiesConverter implements QueryStringToPropertiesExtractor {
	private static final Logger logger = LoggerFactory.getLogger(SpecimenCitationToPropertiesConverter.class);

	// Zambia: c. 3 km west of Kalabo, foot of escarpment at edge of swamp bordering river, fl. 13.xi.1959, Drummond & Cookson 6442 (E; K; LISC; SRGH).

	@Override
	public Property[] extractProperties(String queryString) {
		// Log the query if it takes more than 250ms.
		LoggingStopWatch speedCheck = new Slf4JStopWatch("SpecimenCitationParsing:"+queryString, logger, Slf4JStopWatch.WARN_LEVEL).setTimeThreshold(250);

		speedCheck.stop();

		return makeProperties(queryString);
	}

	private Property[] makeProperties(String recordedBy) {
		List<org.kew.rmf.refine.domain.query.Property> properties = new ArrayList<>();

		if (recordedBy != null) {
			org.kew.rmf.refine.domain.query.Property p = new org.kew.rmf.refine.domain.query.Property();
			p.setP("recorded_by");
			p.setPid("recorded_by");
			p.setV(recordedBy);
			properties.add(p);
		}

		return properties.toArray(new Property[properties.size()]);
	}
}
