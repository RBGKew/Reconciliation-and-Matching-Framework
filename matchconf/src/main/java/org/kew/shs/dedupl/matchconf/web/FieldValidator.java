package org.kew.shs.dedupl.matchconf.web;


public class FieldValidator {

    /**
     * We use many strings as part of the REST-ish url structure;
     * These only want to contain ASCII letters and/or '-', '_'
     * @param s
     * @return
     */
    public static boolean validSlug(String s) {
        return s.matches("[\\w-_]*");
    }

}
