package org.kew.stringmod.dedupl;

import org.kew.stringmod.dedupl.configuration.Configuration;
import org.kew.stringmod.dedupl.exception.DataLoadException;

public interface DataLoader{
	public void load() throws DataLoadException;
	public void setConfig(Configuration config);
}
