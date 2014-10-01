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
package org.kew.rmf.reporters;

import java.io.IOException;
import java.util.Map;

/**
 * A piper is a reporter used for immediately writing out a record without
 * calling all the special bits and bobs methods; it basically represents the
 * 'continue' statement in the reporting logic, without swallowing the record
 * itself away
 */
public class Piper {

    private Reporter reporter;

    public Piper(Reporter reporter) {
        this.reporter = reporter;
    }

    public void pipe(Map<String, String> record) throws IOException {
        Reporter rep = this.getReporter();
        if (rep.isStart()) {
            rep.setWriter();
            if (rep.wantHeader) rep.writeHeader();
            rep.setStart(false);
        }
        rep.writer.write(record, rep.header);
    }

    public Reporter getReporter() {
        return reporter;
    }

}
