package org.kew.shs.dedupl;

import org.kew.shs.dedupl.matchers.Matcher;
import org.kew.shs.dedupl.transformers.Transformer;

/**
 * This is a simple JavaBean that holds configuration options relating 
 * to how a value is cleaned, stored and matched.
 * @author nn00kg
 *
 */
public class Property {

	private String name;
	private int columnIndex;
	private Matcher matcher;
	
	private boolean useInSelect=false;
	private boolean indexLength=false;
	private boolean blanksMatch=false;
	private boolean indexOriginal=false;
	private boolean indexInitial=false;
	
	private Transformer transformer;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getColumnIndex() {
		return columnIndex;
	}
	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}
	public Matcher getMatcher() {
		return matcher;
	}
	public void setMatcher(Matcher matcher) {
		this.matcher = matcher;
	}
	public Transformer getTransformer() {
		return transformer;
	}
	public void setTransformer(Transformer transformer) {
		this.transformer = transformer;
	}
	public boolean isUseInSelect() {
		return useInSelect;
	}
	public void setUseInSelect(boolean useInSelect) {
		this.useInSelect = useInSelect;
	}
	public void setIndexLength(boolean indexLength) {
		this.indexLength = indexLength;
	}
	public boolean isIndexLength() {
		return indexLength;
	}
	public boolean isBlanksMatch() {
		return blanksMatch;
	}
	public void setBlanksMatch(boolean blanksMatch) {
		this.blanksMatch = blanksMatch;
	}
	public boolean isIndexOriginal() {
		return indexOriginal;
	}
	public void setIndexOriginal(boolean indexOriginal) {
		this.indexOriginal = indexOriginal;
	}
	public boolean isIndexInitial() {
		return indexInitial;
	}
	public void setIndexInitial(boolean indexInitial) {
		this.indexInitial = indexInitial;
	}
	@Override
	public String toString() {
		return "Property [name=" + name + ", columnIndex=" + columnIndex
				+ ", matcher=" + matcher + ", useInSelect=" + useInSelect
				+ ", indexLength=" + indexLength + ", blanksMatch="
				+ blanksMatch + ", indexOriginal=" + indexOriginal
				+ ", indexInitial=" + indexInitial + ", transformer="
				+ transformer + "]";
	}
	
}