/**
 *
 * 
 */
package org.kew.shs.dedupl;

import org.kew.shs.dedupl.configuration.Configuration;

/**
 * @author Alecs Geuder
 *
 */
public interface DataHandler {
 
	public void setConfiguration(Configuration config);
	public void setDataLoader(DataLoader dataLoader);
	public void loadData() throws Exception;
	public void run() throws Exception;
	
}
