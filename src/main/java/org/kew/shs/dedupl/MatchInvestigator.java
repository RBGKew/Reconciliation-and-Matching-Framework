package org.kew.shs.dedupl;

import java.io.File;

public interface MatchInvestigator {

	/** 
	 * Pass in a file of matches and produce an output listing the fields that
	 * do not match exactly. 
	 * Not yet implemented. May be better for performance than doing this 
	 * inside the main deduplication process.
	 */
	public void run();
	
	
}