package org.kew.stringmod.lib.transformers;

import org.kew.stringmod.utils.LibraryRegister;

/**
 * This transformer canonicalises epithets
 * @author nn00kg
 *
 */
@LibraryRegister(category="transformers")
public class EpithetTransformer implements Transformer {

	public String transform(String s) {
		  if (s != null){
			  s = s.replaceAll("[^A-Za-z]", "");
			  s = s.replaceFirst("(?<=[^i])ana$", "iana");
			  s = s.replaceFirst("aef", "if");
			  s = s.replaceFirst("colus$", "cola");
			  s = s.replaceFirst("us$", "a");
			  s = s.replaceFirst("um$", "a");
			  s = s.replaceFirst("on$", "a");
			  s = s.replaceFirst("iae$", "i");
			  s = s.replaceFirst("ei$", "i");
			  s = s.replaceFirst("ae$", "i");
			  s = s.replaceFirst("ii$", "i");
			  s = s.replaceFirst("ioi$", "oi");
			  s = s.replace("j", "i");
			  s = s.replace("y", "i");
			  s = s.replace("-", "");
			  s = s.replace("'", "");
			  /*
			  if (value.endsWith("anus") && !value.endsWith("ianus"))
				  value = value.replaceFirst("anus$", "ianus");
			  if (value.endsWith("anum") && !value.endsWith("ianum"))
				  value = value.replaceFirst("anum$", "ianum");
			  if (value.endsWith("arum") && !value.endsWith("iarum"))
				  value = value.replaceFirst("arum$", "iarum");
			  if (value.endsWith("orum") && !value.endsWith("iorum"))
				  value = value.replaceFirst("orum$", "iorum");
				*/
		  }
	    return s;
	}

}