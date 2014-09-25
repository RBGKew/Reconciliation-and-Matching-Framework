package org.kew.rmf.core;

import org.kew.rmf.core.configuration.Configuration;
import org.kew.rmf.core.exception.DataLoadException;

public interface DataLoader{
	public void load() throws DataLoadException;
	public void setConfig(Configuration config);
}
