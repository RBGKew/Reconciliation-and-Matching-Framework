package org.kew.rmf.core;

import org.kew.rmf.core.configuration.Configuration;


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
