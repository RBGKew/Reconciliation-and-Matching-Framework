package org.kew.shs.dedupl.transformers;

public abstract class RegexDefCollection {

	protected String EX_MARKER = " ex ";
	protected String EX_MARKER_REGEX = "( ex | Ex )";
	protected String IN_MARKER = " in ";
	protected String IN_MARKER_REGEX = "( in | In )";

    protected String ALPHANUMDIAC = "[\\wáéíóúÁÉÍÓÚâêîôÂÊÎÔãõÃĂÕçÇ]"; // to include diacritics in alphanumerics

}
