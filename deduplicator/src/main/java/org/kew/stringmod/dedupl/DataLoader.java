package org.kew.stringmod.dedupl;

import org.kew.stringmod.dedupl.configuration.Configuration;

public interface DataLoader{
	public void load() throws Exception;
	public void setConfig(Configuration config);
}
