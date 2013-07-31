package org.kew.shs.dedupl.reporters;

import java.io.IOException;
import java.util.Map;

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
