package org.kew.shs.dedupl;

import java.io.File;

import org.kew.shs.dedupl.configuration.Configuration;


/** 
 * This interface defines a DataLoader
 */
public interface DataLoader{

	public void load();
	public void load(File f);
	public void setConfiguration(Configuration configuration);
	
}