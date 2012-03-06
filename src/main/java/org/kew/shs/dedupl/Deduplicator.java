package org.kew.shs.dedupl;

import org.kew.shs.dedupl.configuration.DeduplicationConfiguration;;


/**
 * This interface defines a Deduplicator
 * @author nn00kg
 *
 */
public interface Deduplicator{

	public void run();
	public void setDataLoader(DataLoader dataLoader);
	public void setConfiguration(DeduplicationConfiguration config);
	public void setInvestigator(Investigator investigator);
}