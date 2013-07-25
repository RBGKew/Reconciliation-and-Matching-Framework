package org.kew.shs.dedupl.configuration;

import java.util.ArrayList;
import java.util.List;

import org.kew.shs.dedupl.matchers.Matcher;
import org.kew.shs.dedupl.transformers.Transformer;

/**
 * This is a simple JavaBean that holds configuration options relating 
 * to how a value is cleaned, stored and matched.
 * @author nn00kg
 *
 */
public class Property {

	private String sourceColumnName;
	private String lookupColumnName;

	private Matcher matcher;
	
	private boolean useInSelect=false;
	private boolean useInNegativeSelect=false;
	private boolean indexLength=false;
	private boolean blanksMatch=false;
	private boolean indexOriginal=false;
	private boolean indexInitial=false;
	private boolean useWildcard=false;

    private boolean addOriginalSourceValue = false;
    private boolean addTransformedSourceValue = false;
    // Matching specific as obsolete for Deduplication tasks
    private boolean addOriginalLookupValue = false;
    private boolean addTransformedLookupValue = false;
	
	private List<Transformer> sourceTransformers = new ArrayList<>();
	private List<Transformer> lookupTransformers = new ArrayList<>();

	@Override
	public String toString() {
		return "Property [name=" + sourceColumnName + "_" + lookupColumnName
				+ ", matcher=" + matcher + ", useInSelect="
			    + useInSelect + ", useInNegativeSelect=" + useInNegativeSelect
				+ ", indexLength=" + indexLength + ", blanksMatch="
				+ blanksMatch + ", indexOriginal=" + indexOriginal
				+ ", indexInitial=" + indexInitial + ", useWildcard="
				+ useWildcard + ", transformers=" + sourceTransformers + "_" + lookupTransformers + "]";
	}

	// Getters and Setters
	public Matcher getMatcher() {
		return matcher;
	}
	public void setMatcher(Matcher matcher) {
		this.matcher = matcher;
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
	public boolean isUseInNegativeSelect() {
		return useInNegativeSelect;
	}
	public void setUseInNegativeSelect(boolean useInNegativeSelect) {
		this.useInNegativeSelect = useInNegativeSelect;
	}
	public boolean isUseWildcard() {
		return useWildcard;
	}
	public void setUseWildcard(boolean useWildcard) {
		this.useWildcard = useWildcard;
	}
	public List<Transformer> getSourceTransformers() {
		return sourceTransformers;
	}
	public void setSourceTransformers(List<Transformer> sourceTransformers) {
		this.sourceTransformers = sourceTransformers;
	}
	public List<Transformer> getLookupTransformers() {
		return lookupTransformers;
	}
	public void setLookupTransformers(List<Transformer> lookupTransformers) {
		this.lookupTransformers = lookupTransformers;
	}
	public boolean isAddOriginalLookupValue() {
		return addOriginalLookupValue;
	}
	public void setAddOriginalLookupValue(boolean addOriginalLookupValue) {
		this.addOriginalLookupValue = addOriginalLookupValue;
	}
	public boolean isAddTransformedLookupValue() {
		return addTransformedLookupValue;
	}
	public void setAddTransformedLookupValue(boolean addTransformedLookupValue) {
		this.addTransformedLookupValue = addTransformedLookupValue;
	}

	public boolean isAddOriginalSourceValue() {
		return addOriginalSourceValue;
	}

	public void setAddOriginalSourceValue(boolean addOriginalSourceValue) {
		this.addOriginalSourceValue = addOriginalSourceValue;
	}

	public boolean isAddTransformedSourceValue() {
		return addTransformedSourceValue;
	}

	public void setAddTransformedSourceValue(boolean addTransformedSourceValue) {
		this.addTransformedSourceValue = addTransformedSourceValue;
	}

	public String getSourceColumnName() {
		return sourceColumnName;
	}

	public void setSourceColumnName(String sourceColumnName) {
		this.sourceColumnName = sourceColumnName;
	}

	public String getLookupColumnName() {
		return lookupColumnName;
	}

	public void setLookupColumnName(String lookupColumnName) {
		this.lookupColumnName = lookupColumnName;
	}
}