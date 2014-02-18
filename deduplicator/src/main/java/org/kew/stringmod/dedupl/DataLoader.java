package org.kew.stringmod.dedupl;

import java.io.File;

import org.kew.stringmod.dedupl.configuration.Configuration;


public interface DataLoader{

	public void load() throws Exception;
	public void load(File f) throws Exception;
	public void setConfig(Configuration config);
	
}