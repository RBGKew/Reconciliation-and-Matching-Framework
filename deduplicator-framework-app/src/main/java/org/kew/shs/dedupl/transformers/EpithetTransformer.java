package org.kew.shs.dedupl.transformers;

import org.kew.shs.dedupl.util.LibraryRegister;

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
			  if (s.endsWith("ana") && !s.endsWith("iana"))
				  s = s.replaceFirst("ana$", "iana");
			  s = s.replaceFirst("aef", "if");
			  s = s.replaceFirst("colus$", "cola");
			  s = s.replaceFirst("us$", "a");
			  s = s.replaceFirst("um$", "a");
			  s = s.replaceFirst("on$", "a");
			  s = s.replace("iae$", "i");
			  s = s.replace("ei$", "i");
			  s = s.replace("ae$", "i");
			  s = s.replace("ii$", "i");
			  s = s.replace("ioi$", "oi");
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