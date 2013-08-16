package org.kew.shs.dedupl.transformers;

/**
 * This interface defines the behaviour expected of Transformers
 * @author nn00kg
 *
 */
public interface Transformer {
	
	public String transform(String s) throws Exception;

}
