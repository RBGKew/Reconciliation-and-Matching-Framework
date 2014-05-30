package org.kew.stringmod.dedupl.matchers;



/**
 * This matcher tests for equality between the two inputs (exact matches).
 * @author nn00kg
 *
 */
public class OcrCanonicaliserMatcher implements Matcher {

	public static int COST = 0;

	@Override
	public int getCost() {
		return COST;
	}
	
	private static final String[][] ocr_pairs = {
		{"a","c"}
		,{"a","d"}
		,{"a","e"}
		,{"a","f"}
		,{"an","m"}
		,{"a","o"}
		,{"a","s"}
		,{"a","z"}
		,{"b","g"}
		,{"b","h"}
		,{"b","p"}
		,{"c","e"}
		,{"c","f"}
		,{"ch","oli"}
		,{"c","o"}
		,{"d","l"}
		,{"e","o"}
		,{"e","r"}
		,{"ff","f"}
		,{"fi","fr"}
		,{"fii","fi"}
		,{"f","l"}
		,{"fl","f"}
		,{"g","h"}
		,{"g","p"}
		,{"g","q"}
		,{"h","i"}
		,{"h","li"}
		,{"h","ll"}
		,{"id","k"}
		,{"i","l"}
		,{"il","u"}
		,{"in","lm"}
		,{"in","m"}
		,{"i","r"}
		,{"i","t"}
		,{"li","k"}
		,{"ll","k"}
		,{"ln","n"}
		,{"l","t"}
		,{"lt","k"}
		,{"mc","rne"}
		,{"m","n"}
		,{"mn","rm"}
		,{"m","o"}
		,{"m","rn"}
		,{"m","ru"}
		,{"m","tn"}
		,{"m","tu"}
		,{"m","u"}
		,{"ni","m"}
		,{"nm","rm"}
		,{"n","o"}
		,{"n","r"}
		,{"n","ra"}
		,{"n","s"}
		,{"n","u"}
		,{"rn","m"}
		,{"ro","m"}
		,{"r","u"}
		,{"s","z"}
		,{"t","f"}
		,{"tr","u"}
		,{"t","u"}
		,{"v","y"}
		,{"x","z"}
		,{"y","u"}
		};
	private static final String CANONICAL = "#";

	@Override
	public boolean matches(String s1, String s2) {
		boolean matches = false;
		if (s1 == null && s2 == null)
			matches = true;
		else{
			try{
				matches = s1.equals(s2);
				if (!matches){
					for (int i = 0; i < ocr_pairs.length; i++) {
						String[] pair = ocr_pairs[i];
						matches = s1.replaceAll(pair[0], CANONICAL).matches(s2.replaceAll(pair[1], CANONICAL));
						if (matches)
							break;
					}
				}
			}
			catch (Exception e) {
				;
			}
		}
		return matches;
	}

	@Override
	public boolean isExact() {
		return false;
	}

	@Override
	public String getExecutionReport() {
		return null;
	}
	
	public static void main(String[] args) {
		OcrCanonicaliserMatcher m = new OcrCanonicaliserMatcher();
		System.out.println(m.matches("hirsuta", "liirsuta"));
	}
}
