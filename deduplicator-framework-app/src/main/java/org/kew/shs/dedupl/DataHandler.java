/**
 *
 * 
 */
package org.kew.shs.dedupl;

import org.kew.shs.dedupl.configuration.Configuration;


public interface DataHandler<Config extends Configuration> {
 
	public void setConfig(Config config);
	public void setDataLoader(DataLoader dataLoader);
	public void loadData() throws Exception;
	public void run() throws Exception;
	
}
