package org.kew.stringmod.dedupl.matchers;

/**
 * This interface defines the behaviour expected of Matchers.
 * Some matches are expensive to calculate, so matchers have a cost:
 * zero - low, higher int values = higher cost.
 * When multiple matchers are defined, it is expected that they will be 
 * evaluated in order of cost (lowest first).
 * 
 * @author nn00kg
 */
public interface Matcher {

	public boolean matches(String s1, String s2) throws MatchException;
	public boolean isExact();
	public int getCost();
	public String getExecutionReport();
}
