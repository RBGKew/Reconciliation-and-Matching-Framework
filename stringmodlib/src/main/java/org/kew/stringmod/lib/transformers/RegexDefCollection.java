package org.kew.stringmod.lib.transformers;


public abstract class RegexDefCollection {

	protected String EX_MARKER = " ex ";
	protected String EX_MARKER_REGEX = "( [Ee][Xx] )";
	protected String IN_MARKER = " in ";
	protected String IN_MARKER_REGEX =  "( [Ii][Nn] )";

	// to catch (more than one) occurrences of bracket-pairs incl. their content
	protected String ROUND_BRACKETS_AND_CONTENT = "\\([^\\(\\)]*?\\)";
	protected String SQUARE_BRACKETS_AND_CONTENT = "\\[[^\\[\\]]*?\\]";
	protected String CURLY_BRACKETS_AND_CONTENT = "\\{[^\\{\\}]*?\\}";

    protected String ALPHANUMDIAC = "[a-zA-ZáéíóúÁÉÍÓÚâêîôÂÊÎÔãõÃĂÕçÇ]"; // to include diacritics in alphanumerics

}
