package org.kew.rmf.matchconf.web;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kew.rmf.matchconf.Configuration;

public class ConfigSwitch {

    @SuppressWarnings("serial")
	static final Map<String,String> TYPE_CLASS_MAP = new HashMap<String,String>() {{
        put("match", "MatchConfiguration");
        put("dedup", "DeduplicationConfiguration");
    }};

    @SuppressWarnings("serial")
	static final Map<String,String> CLASS_TYPE_MAP = new HashMap<String,String>() {{
        put("MatchConfiguration", "match");
        put("DeduplicationConfiguration", "dedup");
    }};

    public static String getTypeForUrl (String configName) {
        Configuration config = Configuration.findConfigurationsByNameEquals(configName).getSingleResult();
        return CLASS_TYPE_MAP.get(config.getClassName());
    }

    @SuppressWarnings("unchecked")
	public static List<Configuration> findConfigEntries (int firstResult, int size, String configType) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        String method = "findConfigurationEntries";
        if (configType.equals("match")) method = "findMatchConfigEntries";
        else if (configType.equals("dedup")) method = "findDedupConfigEntries";
        return (List<Configuration>) Configuration.class.getMethod(method, int.class, int.class).invoke(null, firstResult, size);
    }

    public static long countConfigs(String className) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        String method = "countConfigurations";
        if (className.equals("match")) method = "countMatchConfigs";
        else if (className.equals("dedup")) method = "countDedupConfigs";
        return (long) Configuration.class.getMethod(method).invoke(null);
    }

    @SuppressWarnings("unchecked")
	public static List<Configuration> findAllConfigs(String configType) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        String method = "findAllConfigurations";
        if (configType.equals("match")) method = "findAllMatchConfigs";
        else if (configType.equals("dedup")) method = "findAllDedupConfigs";
        return (List<Configuration>) Configuration.class.getMethod(method).invoke(null);
    }
}
