package org.kew.stringmod.dedupl.script;

import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import com.google.gson.Gson;


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
