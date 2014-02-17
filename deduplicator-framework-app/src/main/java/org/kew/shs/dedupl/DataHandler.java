package org.kew.shs.dedupl;

import org.kew.shs.dedupl.configuration.Configuration;


/**
 * The DataHandler does all the work.
 * It loads the data and runs a process as defined in the provided configuration.
 *
 * @param <Config>
 */
public interface DataHandler<Config extends Configuration> {

	public void setConfig(Config config);
	public void setDataLoader(DataLoader dataLoader);
	public void loadData() throws Exception;
	public void run() throws Exception;

}
