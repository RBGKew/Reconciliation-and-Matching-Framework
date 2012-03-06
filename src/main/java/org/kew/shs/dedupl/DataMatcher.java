package org.kew.shs.dedupl;

import org.kew.shs.dedupl.configuration.MatchConfiguration;


/**
 * This interface defines a Deduplicator
 * @author nn00kg
 *
 */
public interface DataMatcher{

	public void run();
	public void setDataLoader(DataLoader dataLoader);
	public void setConfiguration(MatchConfiguration config);
	
}