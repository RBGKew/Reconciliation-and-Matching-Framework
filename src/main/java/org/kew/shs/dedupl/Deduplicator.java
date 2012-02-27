package org.kew.shs.dedupl;


/**
 * This interface defines a Deduplicator
 * @author nn00kg
 *
 */
public interface Deduplicator{

	public void run();
	public void setConfiguration(Configuration config);
	
}