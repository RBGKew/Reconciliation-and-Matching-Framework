package org.kew.stringmod.dedupl.configuration;

import java.util.ArrayList;
import java.util.List;

import org.kew.rmf.transformers.Transformer;
import org.kew.stringmod.dedupl.matchers.Matcher;

/**
 * This is a simple JavaBean that holds configuration options relating
 * to how a value is cleaned, stored and matched.
 *
 * The mapping works via <strong>named</strong> columns.
 */
public class Property {

	private String queryColumnName;
	private String authorityColumnName;

	private Matcher matcher;

	private boolean useInSelect=false;
	private boolean useInNegativeSelect=false;
	private boolean indexLength=false;
	private boolean blanksMatch=false;
	private boolean indexInitial=false;
	private boolean useWildcard=false;

	private boolean addOriginalQueryValue = false;
	private boolean addTransformedQueryValue = false;
	// Matching specific as obsolete for Deduplication tasks
	private boolean addOriginalAuthorityValue = false;
	private boolean addTransformedAuthorityValue = false;

	private List<Transformer> queryTransformers = new ArrayList<>();
	private List<Transformer> authorityTransformers = new ArrayList<>();

	@Override
	public String toString() {
		return "Property [name=" + queryColumnName + "_" + authorityColumnName
				+ ", matcher=" + matcher + ", useInSelect="
				+ useInSelect + ", useInNegativeSelect=" + useInNegativeSelect
				+ ", indexLength=" + indexLength + ", blanksMatch="
				+ blanksMatch + ", indexInitial=" + indexInitial + ", useWildcard="
				+ useWildcard + ", transformers=" + queryTransformers + "_" + authorityTransformers + "]";
	}

	// Getters and Setters
	public Matcher getMatcher() {
		return matcher;
	}
	/**
	 * The {@link Matcher} to use against the transformed fields.
	 */
	public void setMatcher(Matcher matcher) {
		this.matcher = matcher;
	}

	public boolean isUseInSelect() {
		return useInSelect;
	}
	/**
	 * Whether this property should be used in the query to retrieve potential matches.
	 */
	public void setUseInSelect(boolean useInSelect) {
		this.useInSelect = useInSelect;
	}

	public void setIndexLength(boolean indexLength) {
		this.indexLength = indexLength;
	}
	/**
	 * Index the length of the record, needed for Lucene Levenshtein matching
	 * @return
	 */
	public boolean isIndexLength() {
		return indexLength;
	}

	public boolean isBlanksMatch() {
		return blanksMatch;
	}
	/**
	 * Whether a blank on either side (authority or query) is a match.
	 */
	public void setBlanksMatch(boolean blanksMatch) {
		this.blanksMatch = blanksMatch;
	}

	public boolean isIndexInitial() {
		return indexInitial;
	}
	/**
	 * Index the first letter, used to limit records retrieved as potential matches.
	 */
	public void setIndexInitial(boolean indexInitial) {
		this.indexInitial = indexInitial;
	}

	public boolean isUseInNegativeSelect() {
		return useInNegativeSelect;
	}
	/**
	 * Exclude records from query to retrieve potential matches.
	 */
	public void setUseInNegativeSelect(boolean useInNegativeSelect) {
		this.useInNegativeSelect = useInNegativeSelect;
	}

	public boolean isUseWildcard() {
		return useWildcard;
	}
	/**
	 * Allow approximate matching when retrieving potential matches.
	 */
	public void setUseWildcard(boolean useWildcard) {
		this.useWildcard = useWildcard;
	}

	public List<Transformer> getQueryTransformers() {
		return queryTransformers;
	}
	/**
	 * The {@link Transformer}s used to transform query records before indexing or matching.
	 */
	public void setQueryTransformers(List<Transformer> queryTransformers) {
		this.queryTransformers = queryTransformers;
	}

	public List<Transformer> getAuthorityTransformers() {
		return authorityTransformers;
	}
	/**
	 * The {@link Transformer}s used to transform authority records before indexing or matching.
	 */
	public void setAuthorityTransformers(List<Transformer> authorityTransformers) {
		this.authorityTransformers = authorityTransformers;
	}

	public boolean isAddOriginalAuthorityValue() {
		return addOriginalAuthorityValue;
	}
	/**
	 * Whether to add the untransformed authority value to result records.
	 */
	public void setAddOriginalAuthorityValue(boolean addOriginalAuthorityValue) {
		this.addOriginalAuthorityValue = addOriginalAuthorityValue;
	}

	public boolean isAddTransformedAuthorityValue() {
		return addTransformedAuthorityValue;
	}
	/**
	 * Whether to add the transformed authority value to result records.
	 */
	public void setAddTransformedAuthorityValue(boolean addTransformedAuthorityValue) {
		this.addTransformedAuthorityValue = addTransformedAuthorityValue;
	}

	public boolean isAddOriginalQueryValue() {
		return addOriginalQueryValue;
	}
	/**
	 * Whether to add the untransformed query value to result records.
	 */
	public void setAddOriginalQueryValue(boolean addOriginalQueryValue) {
		this.addOriginalQueryValue = addOriginalQueryValue;
	}

	public boolean isAddTransformedQueryValue() {
		return addTransformedQueryValue;
	}
	/**
	 * Whether to add the transformed query value to result records.
	 */
	public void setAddTransformedQueryValue(boolean addTransformedQueryValue) {
		this.addTransformedQueryValue = addTransformedQueryValue;
	}

	public String getQueryColumnName() {
		return queryColumnName;
	}
	/**
	 * The name of the query column this Property refers to.
	 */
	public void setQueryColumnName(String queryColumnName) {
		this.queryColumnName = queryColumnName;
	}

	public String getAuthorityColumnName() {
		return authorityColumnName;
	}
	/**
	 * The name of the authority column this Property refers to.
	 */
	public void setAuthorityColumnName(String authorityColumnName) {
		this.authorityColumnName = authorityColumnName;
	}
}
