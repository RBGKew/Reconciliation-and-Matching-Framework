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
package org.kew.rmf.core.script;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptException;

import org.junit.Test;

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
