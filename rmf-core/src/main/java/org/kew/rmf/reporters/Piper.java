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
