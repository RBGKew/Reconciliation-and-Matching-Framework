package org.kew.shs.dedupl;


public interface Investigator {

	/** 
	 * Pass in a file of matches and produce an output listing the fields that
	 * do not match exactly. 
	 */
	public void run();
	
	
}