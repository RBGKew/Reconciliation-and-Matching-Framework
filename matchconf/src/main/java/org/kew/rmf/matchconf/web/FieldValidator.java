package org.kew.rmf.matchconf.web;

import org.apache.commons.lang3.StringUtils;

public class FieldValidator {

    /**
     * We use many strings as part of the REST-ish url structure;
     * These only want to contain ASCII letters and/or '-', '_'
     * @param s
     * @return
     */
    public static boolean validSlug(String s) {
        return !StringUtils.isBlank(s) && s.matches("[\\w-_]*");
    }

}
