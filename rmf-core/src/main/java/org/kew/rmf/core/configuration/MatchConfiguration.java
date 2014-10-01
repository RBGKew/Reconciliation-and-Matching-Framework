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
package org.kew.rmf.core.configuration;

import java.util.ArrayList;
import java.util.List;

public class MatchConfiguration extends Configuration {

	@Override
	public String[] outputDefs() {
		List<String> queryOutputDefs = new ArrayList<>();
		List<String> authorityOutputDefs = new ArrayList<>();
		for (Property prop : this.getProperties()) {
			if (prop.isAddOriginalQueryValue()) queryOutputDefs.add(prop.getQueryColumnName());
			if (prop.isAddTransformedQueryValue()) queryOutputDefs.add(prop.getQueryColumnName() + "_transf");
			if (prop.isAddOriginalAuthorityValue()) authorityOutputDefs.add("authority_" + prop.getAuthorityColumnName());
			if (prop.isAddTransformedAuthorityValue()) authorityOutputDefs.add("authority_" + prop.getAuthorityColumnName() + "_transf");
		}
		queryOutputDefs.addAll(authorityOutputDefs);
		return queryOutputDefs.toArray(new String[queryOutputDefs.size()]);
	}
}
