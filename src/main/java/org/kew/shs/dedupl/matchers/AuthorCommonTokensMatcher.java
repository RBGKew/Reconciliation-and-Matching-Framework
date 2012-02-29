package org.kew.shs.dedupl.matchers;

import com.googlecode.ehcache.annotations.Cacheable;

/**
 * This matcher tests for common tokens in two authorship strings
 * @author nn00kg
 *
 */
public class AuthorCommonTokensMatcher extends CommonTokensMatcher{

	public static int COST = 1;
	
	private static String IN_MARKER = " in ";
	private static String EX_MARKER = " ex ";
	
	public int getCost() {
		return COST;
	}

	@Cacheable(cacheName="actMatchCache")
	public boolean matches(String s1, String s2) {
		boolean matches = false;
		if (s1 == null && s2 == null)
			matches = true;
		else{
			matches = super.matches(clean(s1),clean(s2));
		}
		return matches;
	}

	private String clean(String s){
		return cleanEx(cleanIn(s));
	}
	
	private String cleanEx(String s){
		String cleaned = s;
		if (s != null){
			if (s.indexOf(EX_MARKER) != -1){
				cleaned = s.replaceAll(".*" + EX_MARKER, "");
			}
		}
		return cleaned;
	}
	
	private String cleanIn(String s){
		String cleaned = s;
		if (s != null){
			if (s.indexOf(IN_MARKER) != -1){
				cleaned = s.replaceAll(IN_MARKER+ ".*$", "");
			}
		}
		return cleaned;		
	}
	
	public boolean isExact() {
		return true;
	}

	public String getExecutionReport() {
		return null;
	}

}