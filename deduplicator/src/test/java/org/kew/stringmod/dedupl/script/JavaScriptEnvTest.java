package org.kew.stringmod.dedupl.script;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptException;

import org.junit.Test;
import org.kew.stringmod.dedupl.script.JavaScriptEnv;

public class JavaScriptEnvTest {

    @Test
    public void testBooleanBehaviourSimple() throws ScriptException {
        JavaScriptEnv jsEnv = new JavaScriptEnv();
        Map<String, String> record = new HashMap<>();
        assertTrue(jsEnv.evalFilter("true", record));
        assertFalse(jsEnv.evalFilter("false", record));
    }

    @Test(expected = ScriptException.class)
    public void testWrongStuffReturned() throws ScriptException {
        JavaScriptEnv jsEnv = new JavaScriptEnv();
        Map<String, String> record = new HashMap<>();
        jsEnv.evalFilter("something else", record);
    }

    @Test(expected = NullPointerException.class)
    public void testAlsoFailIfEmpty() throws ScriptException {
        JavaScriptEnv jsEnv = new JavaScriptEnv();
        Map<String, String> record = new HashMap<>();
        jsEnv.evalFilter("", record);
    }

    @Test
    public void testRealJavaScriptExampleSimple() throws ScriptException {
        JavaScriptEnv jsEnv = new JavaScriptEnv();
        Map<String, String> record = new HashMap<>();
        assertTrue(jsEnv.evalFilter("true === true", record));
        assertFalse(jsEnv.evalFilter("true === false", record));
    }

    @Test
    public void testRealJavaScriptExampleWithRecord() throws ScriptException {
        JavaScriptEnv jsEnv = new JavaScriptEnv();
        Map<String, String> record = new HashMap<>();
        record.put("fieldA", "value1");
        record.put("fieldB", "value2");
        record.put("fieldC", "value1");
        assertTrue(jsEnv.evalFilter("record.fieldA == 'value1'", record));
        assertTrue(jsEnv.evalFilter("record.fieldA == record['fieldC']", record));
        assertFalse(jsEnv.evalFilter("record.fieldA == record.fieldB", record));
    }

}
