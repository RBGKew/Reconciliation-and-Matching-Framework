package org.kew.rmf.core.script;

import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.google.gson.Gson;


/**
 * This boxed javascript environment is able to evaluate a string as javascript.
 *
 * The available data to evaluate on is provided in form of a map of strings, hence
 * a typical task would be "record['columnA'] > 10 && record['columnB'] != 'jumpOverMe'"
 */
public class JavaScriptEnv {

    private ScriptEngineManager factory;

    public JavaScriptEnv() {
        this.factory = new ScriptEngineManager();
    }

    public boolean evalFilter(String s, Map<String, String> record) throws ScriptException {
        Gson gson = new Gson();
        ScriptEngine engine = this.factory.getEngineByName("JavaScript");
        String recordJson = gson.toJson(record);
        engine.put("record_json", recordJson);
        try {
            s = "var record = new Object(JSON.parse(record_json));" + s;
            return (boolean) engine.eval(s);
        } catch (NullPointerException e) {
            throw new NullPointerException("The record filter should return either true or false! -- " + e.toString());
        }
    }

}
