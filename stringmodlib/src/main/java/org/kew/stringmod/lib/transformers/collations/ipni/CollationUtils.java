package org.kew.stringmod.lib.transformers.collations.ipni;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.kew.stringmod.lib.transformers.collations.CollationStructureTransformer;

public class CollationUtils {

	public static int SERIES_INDEX = 0;
	public static int VOL_INDEX = 1;
	public static int ISSUE_INDEX = 2;
	public static int PAGE_INDEX = 3;
	public static int TAB_OR_FIG_INDEX = 4;
	public static int YEAR_INDEX = 5;
	public static int RULE_INDEX = 6;

	private static String[] NOTES_PATTERNS={", a.", ", a. a.", ", a a.", ", a a a.", ", a 'a'"};

	// This pair of methods provides a way to match collation patterns which have notes, such as ', in obs.' 
	// at the end, without always explicitly stating these in each test below.  
	// It has not been used in all the code in the parseCollation method but would make this much more succinct if done so. 

	//private static boolean patternMatchesWithNotes(String suppliedPattern, String testPattern){
	//	return patternMatchesWithNotes(suppliedPattern, testPattern, false);
	//}
	private static boolean patternMatchesWithNotes(String suppliedPattern, String testPattern, boolean normaliseSpace){
		boolean matches = false;
		for (String suffix :NOTES_PATTERNS){
			if (normaliseSpace){
				matches = suppliedPattern.replaceAll(" ", "").equals((testPattern+suffix).replaceAll(" ", ""));
			}
			else{
				matches = suppliedPattern.equals(testPattern+suffix);
			}
			if (matches) break;	
		}
		// if the submitted pattern matches the test pattern with the addition of notes flags, then return true:
		return matches;
	}
	
	private static String[] splitCollation(String collation){
		return CollationStructureTransformer.splitCollation(collation);
	}
	
	public static String assessCollationStructure(String collation){
		return CollationStructureTransformer.assessCollationStructure(collation);
	}

	public static boolean parsableCollation(String collation){
		return !Arrays.toString(parseCollation(collation)).equals("[, , , , , , ]");
	}
	
	public static String[] parseCollation(String collation){
		
		String pattern = assessCollationStructure(collation);
		String series="";
		String volume="";
		String issue="";
		String page="";
		String tab_or_fig_or_no="";
		String year="";
		String rule="";

		boolean parsed = false;
		
		// Just pages, maybe followed by in obs etc
		if (pattern.equals("d")
				|| pattern.equals("d.")
				|| pattern.equals("d, a.")				
				|| pattern.equals("d, a a.")
				|| pattern.equals("d, a. a.")
				|| pattern.equals("d, a a a.")
				|| pattern.equals("d, a 'a'")
				|| pattern.equals("d (-d)")
				|| pattern.equals("yyyy")
				|| pattern.equals("yyyy.")
				|| pattern.equals("yyyy, a.")
				|| pattern.equals("yyyy, a a.")
				|| pattern.equals("yyyy, a. a.")
				|| pattern.equals("yyyy, a a a.")
				|| pattern.equals("yyyy, a 'a'")
				|| pattern.equals("r")
				|| pattern.equals("r.")
				|| pattern.equals("r, a.")
				|| pattern.equals("r, a a.")
				|| pattern.equals("r, a. a.")
				|| pattern.equals("r, a a a.")
				|| pattern.equals("r, a 'a'")){
			//Example: "88"
			//Series: ""
			series = "";
			//Volume: ""
			volume = "";
			//Issue: ""
			issue = "";
			//Page: "88"
			page = splitCollation(collation)[0];
			//Tab/Fig/No: ""
			tab_or_fig_or_no = "";
			//Year: ""
			year = "";
			rule = "1";
			parsed = true;
		}
		// Things that are vol: page, possibly followed by in obs / nom nov etc
		if (!parsed && (pattern.replaceAll(" ","").equals("d:d")
				||pattern.replaceAll(" ","").equals("d:d.")
				||pattern.replaceAll(" ","").equals("d:d,")
				||pattern.replaceAll(" ","").equals("d:d,a")
				||pattern.replaceAll(" ","").equals("d:d.a.")
				||pattern.replaceAll(" ","").equals("d:d.aa.")
				||pattern.replaceAll(" ","").equals("d:d.a.a.")
				||pattern.replaceAll(" ","").equals("d:d.aaa.")
				||pattern.replaceAll(" ","").equals("d:d.a'a'")
				||pattern.replaceAll(" ","").equals("d:d,a.")
				||pattern.replaceAll(" ","").equals("d:d,aa.")
				||pattern.replaceAll(" ","").equals("d:d,a.a.")
				||pattern.replaceAll(" ","").equals("d:d,aaa.")
				||pattern.replaceAll(" ","").equals("d:d,a'a'")
				||pattern.replaceAll(" ","").equals("d:yyyy")
				||pattern.replaceAll(" ","").equals("d:yyyy.")
				||pattern.replaceAll(" ","").equals("d:yyyy,")
				||pattern.replaceAll(" ","").equals("d:yyyy,a")
				||pattern.replaceAll(" ","").equals("d:yyyy.a.")
				||pattern.replaceAll(" ","").equals("d:yyyy.aa.")
				||pattern.replaceAll(" ","").equals("d:yyyy.a.a.")
				||pattern.replaceAll(" ","").equals("d:yyyy.aaa.")
				||pattern.replaceAll(" ","").equals("d:yyyy.a'a'")
				||pattern.replaceAll(" ","").equals("d:yyyy,a.")
				||pattern.replaceAll(" ","").equals("d:yyyy,aa.")
				||pattern.replaceAll(" ","").equals("d:yyyy,a.a.")
				||pattern.replaceAll(" ","").equals("d:yyyy,aaa.")
				||pattern.replaceAll(" ","").equals("d:yyyy,a'a'")
				//
				||pattern.replaceAll(" ","").equals("yyyy-d:d")
				// As above, but with roman numeral volume:
				||pattern.replaceAll(" ","").equals("r:d")
				||pattern.replaceAll(" ","").equals("r:d.")
				||pattern.replaceAll(" ","").equals("r:d,")
				||pattern.replaceAll(" ","").equals("r:d,a")
				||pattern.replaceAll(" ","").equals("r:d.a.")
				||pattern.replaceAll(" ","").equals("r:d.aa.")
				||pattern.replaceAll(" ","").equals("r:d.a.a.")
				||pattern.replaceAll(" ","").equals("r:d.aaa.")
				||pattern.replaceAll(" ","").equals("r:d.a'a'")
				||pattern.replaceAll(" ","").equals("r:d,a.")
				||pattern.replaceAll(" ","").equals("r:d,aa.")
				||pattern.replaceAll(" ","").equals("r:d,a.a.")
				||pattern.replaceAll(" ","").equals("r:d,aaa.")
				||pattern.replaceAll(" ","").equals("r:d,a'a'")
				||pattern.replaceAll(" ","").equals("r:yyyy")
				||pattern.replaceAll(" ","").equals("r:yyyy.")
				||pattern.replaceAll(" ","").equals("r:yyyy,")
				||pattern.replaceAll(" ","").equals("r:yyyy,a")
				||pattern.replaceAll(" ","").equals("r:yyyy.a.")
				||pattern.replaceAll(" ","").equals("r:yyyy.aa.")
				||pattern.replaceAll(" ","").equals("r:yyyy.a.a.")
				||pattern.replaceAll(" ","").equals("r:yyyy.aaa.")
				||pattern.replaceAll(" ","").equals("r:yyyy.a'a'")
				||pattern.replaceAll(" ","").equals("r:yyyy,a.")
				||pattern.replaceAll(" ","").equals("r:yyyy,aa.")
				||pattern.replaceAll(" ","").equals("r:yyyy,a.a.")
				||pattern.replaceAll(" ","").equals("r:yyyy,aaa.")
				||pattern.replaceAll(" ","").equals("r:yyyy,a'a'")
				// As above but with 4 digit volume
				||pattern.replaceAll(" ","").equals("yyyy:d")
				||pattern.replaceAll(" ","").equals("yyyy:d.")
				||pattern.replaceAll(" ","").equals("yyyy:d,")
				||pattern.replaceAll(" ","").equals("yyyy:d,a")
				||pattern.replaceAll(" ","").equals("yyyy:d.a.")
				||pattern.replaceAll(" ","").equals("yyyy:d.aa.")
				||pattern.replaceAll(" ","").equals("yyyy:d.a.a.")
				||pattern.replaceAll(" ","").equals("yyyy:d.aaa.")
				||pattern.replaceAll(" ","").equals("yyyy:d.a'a'")
				||pattern.replaceAll(" ","").equals("yyyy:d,a.")
				||pattern.replaceAll(" ","").equals("yyyy:d,aa.")
				||pattern.replaceAll(" ","").equals("yyyy:d,a.a.")
				||pattern.replaceAll(" ","").equals("yyyy:d,aaa.")
				||pattern.replaceAll(" ","").equals("yyyy:d,a'a'")
				||pattern.replaceAll(" ","").equals("yyyy:yyyy")
				||pattern.replaceAll(" ","").equals("yyyy:yyyy.")
				||pattern.replaceAll(" ","").equals("yyyy:yyyy,")
				||pattern.replaceAll(" ","").equals("yyyy:yyyy,a")
				||pattern.replaceAll(" ","").equals("yyyy:yyyy.a.")
				||pattern.replaceAll(" ","").equals("yyyy:yyyy.aa.")
				||pattern.replaceAll(" ","").equals("yyyy:yyyy.a.a.")
				||pattern.replaceAll(" ","").equals("yyyy:yyyy.aaa.")
				||pattern.replaceAll(" ","").equals("yyyy:yyyy.a'a'")
				||pattern.replaceAll(" ","").equals("yyyy:yyyy,a.")
				||pattern.replaceAll(" ","").equals("yyyy:yyyy,aa.")
				||pattern.replaceAll(" ","").equals("yyyy:yyyy,a.a.")
				||pattern.replaceAll(" ","").equals("yyyy:yyyy,aaa.")
				||pattern.replaceAll(" ","").equals("yyyy:yyyy,a'a'")				
				)){
			//Example: "1: 1"
			String[] c = splitCollation(collation.replaceAll(" ", ""));
			//Series: ""
			series = "";
			//Volume: "1"
			volume = c[0];
			//Issue: ""
			issue = "";
			//Page: "1"
			page = c[1];
			//Tab/Fig/No: ""
			tab_or_fig_or_no = "";
			//Year: ""
			year = "";
			rule = "3";
			parsed = true;
		}
		// Things that are vol (issue): page, possibly followed by "in obs." / "nom. nov." / "sphalm." / "figs." / "without basionym ref." etc		
		if (!parsed && 
				(pattern.replaceAll(" ","").equals("d(d):d")
				||pattern.replaceAll(" ","").equals("d(d):d,")
				||pattern.replaceAll(" ","").equals("d(d):d.")
				||pattern.replaceAll(" ","").equals("d(d):daa.")
				||pattern.replaceAll(" ","").equals("d(d):d,aa.")
				||pattern.replaceAll(" ","").equals("d(d):d.aa.")
				||pattern.replaceAll(" ","").equals("d(d):d,a.a.")
				||pattern.replaceAll(" ","").equals("d(d):d.a.a.")
				||pattern.replaceAll(" ","").equals("d(d):d,a.")
				||pattern.replaceAll(" ","").equals("d(d):d.a.")
				||pattern.replaceAll(" ","").equals("d(d):daaa.")
				||pattern.replaceAll(" ","").equals("d(d):d,aaa.")
				||pattern.replaceAll(" ","").equals("d(d):d.aaa.")
				||pattern.replaceAll(" ","").equals("d(d):yyyy")
				||pattern.replaceAll(" ","").equals("d(d):yyyy,")
				||pattern.replaceAll(" ","").equals("d(d):yyyy.")
				||pattern.replaceAll(" ","").equals("d(d):yyyy,aa.")
				||pattern.replaceAll(" ","").equals("d(d):yyyy.aa.")
				||pattern.replaceAll(" ","").equals("d(d):yyyy,a.a.")
				||pattern.replaceAll(" ","").equals("d(d):yyyy.a.a.")
				||pattern.replaceAll(" ","").equals("d(d):yyyy,a.")
				||pattern.replaceAll(" ","").equals("d(d):yyyy.a.")
				||pattern.replaceAll(" ","").equals("d(d):yyyy,aaa.")
				||pattern.replaceAll(" ","").equals("d(d):yyyy.aaa.")
				// Prev handledunder rule 7
				||pattern.replaceAll(" ","").equals("d[d]:d")
				||pattern.replaceAll(" ","").equals("d[d]:yyyy")
				// Prev handled under rule #39 
				||pattern.replaceAll(" ","").equals("d(d):d(-d)")
				||pattern.replaceAll(" ","").equals("d(d):d(-d)a")
				||pattern.replaceAll(" ","").equals("d(d):d(-d),a")
				||pattern.replaceAll(" ","").equals("d(d):d(-d).a")
				||pattern.replaceAll(" ","").equals("d(d):d(-d)aaa.")
				||pattern.replaceAll(" ","").equals("d(d):d(-d),aaa.")
				||pattern.replaceAll(" ","").equals("d(d):d(-d).aaa.")
				||pattern.replaceAll(" ","").equals("d(d):yyyy(-d)")
				||pattern.replaceAll(" ","").equals("d(d):yyyy(-d)a")
				||pattern.replaceAll(" ","").equals("d(d):yyyy(-d),a")
				||pattern.replaceAll(" ","").equals("d(d):yyyy(-d).a")
				||pattern.replaceAll(" ","").equals("d(d):yyyy(-d)aaa.")
				||pattern.replaceAll(" ","").equals("d(d):yyyy(-d),aaa.")
				||pattern.replaceAll(" ","").equals("d(d):yyyy(-d).aaa.")
				// Roman numeral volume:
				||pattern.replaceAll(" ","").equals("r(d):d")
				||pattern.replaceAll(" ","").equals("r(d):d(-d)")
				||pattern.replaceAll(" ","").equals("r(d):d(-d)a")
				||pattern.replaceAll(" ","").equals("r(d):d(-d),a")
				||pattern.replaceAll(" ","").equals("r(d):d(-d).a")
				||pattern.replaceAll(" ","").equals("r(d):d(-d)aaa.")
				||pattern.replaceAll(" ","").equals("r(d):d(-d),aaa.")
				||pattern.replaceAll(" ","").equals("r(d):d(-d).aaa.")
				||pattern.replaceAll(" ","").equals("r(d):yyyy(-d)")
				||pattern.replaceAll(" ","").equals("r(d):yyyy(-d)a")
				||pattern.replaceAll(" ","").equals("r(d):yyyy(-d),a")
				||pattern.replaceAll(" ","").equals("r(d):yyyy(-d).a")
				||pattern.replaceAll(" ","").equals("r(d):yyyy(-d)aaa.")
				||pattern.replaceAll(" ","").equals("r(d):yyyy(-d),aaa.")
				||pattern.replaceAll(" ","").equals("r(d):yyyy(-d).aaa.")				
				// Prev handled under rule #57.7 
				||pattern.replaceAll(" ","").equals("yyyy(d):d")
				||pattern.replaceAll(" ","").equals("yyyy(d):d,")
				||pattern.replaceAll(" ","").equals("yyyy(d):d.")
				||pattern.replaceAll(" ","").equals("yyyy(d):d,aa.")
				||pattern.replaceAll(" ","").equals("yyyy(d):d.aa.")
				||pattern.replaceAll(" ","").equals("yyyy(d):d,a.a.")
				||pattern.replaceAll(" ","").equals("yyyy(d):d.a.a.")
				||pattern.replaceAll(" ","").equals("yyyy(d):da.")
				||pattern.replaceAll(" ","").equals("yyyy(d):d,a.")
				||pattern.replaceAll(" ","").equals("yyyy(d):d.a.")
				||pattern.replaceAll(" ","").equals("yyyy(d):d,aaa.")
				||pattern.replaceAll(" ","").equals("yyyy(d):d.aaa.")
				||pattern.replaceAll(" ","").equals("yyyy(d):d(-d)")
				||pattern.replaceAll(" ","").equals("yyyy(d):d(-d)a")
				||pattern.replaceAll(" ","").equals("yyyy(d):d(-d),a")
				||pattern.replaceAll(" ","").equals("yyyy(d):d(-d),aa.")
				||pattern.replaceAll(" ","").equals("yyyy(d):d(-d).a")
				||pattern.replaceAll(" ","").equals("yyyy(d):d(-d)aaa.")
				||pattern.replaceAll(" ","").equals("yyyy(d):d(-d),aaa.")
				||pattern.replaceAll(" ","").equals("yyyy(d):d(-d).aaa.")
				||pattern.replaceAll(" ","").equals("yyyy(d):yyyy")
				||pattern.replaceAll(" ","").equals("yyyy(d):yyyy,a.a.")
				||pattern.replaceAll(" ","").equals("yyyy(d):yyyy,aa.")
				||pattern.replaceAll(" ","").equals("yyyy(d):yyyy,aaa.")
				||pattern.replaceAll(" ","").equals("yyyy(d):yyyy(-d)")
				||pattern.replaceAll(" ","").equals("yyyy(d):yyyy(-d)a")
				||pattern.replaceAll(" ","").equals("yyyy(d):yyyy(-d),a")
				||pattern.replaceAll(" ","").equals("yyyy(d):yyyy(-d).a")
				||pattern.replaceAll(" ","").equals("yyyy(d):yyyy(-d)aaa.")
				||pattern.replaceAll(" ","").equals("yyyy(d):yyyy(-d),aaa.")
				||pattern.replaceAll(" ","").equals("yyyy(d):yyyy(-d).aaa."))){
			
			//Example: "1(1): 1" or "1(1):1" 
			String[] c = splitCollation(collation.replaceAll(" ", ""));
			//Series: ""
			series = "";
			//Volume: "1"
			volume = c[0];
			//Issue: "1"
			issue = c[1];
			//Page: "1"
			page = c[2];
			//Tab/Fig/No: ""
			tab_or_fig_or_no = "";
			//Year: ""
			year = "";
			rule = "4";
			parsed = true;
		}
		// vol(issue): page (year)
		if (!parsed && (pattern.replaceAll(" ","").equals("d(d):d(yyyy)")
				|| pattern.replaceAll(" ","").equals("d(d):d(yyyy).")
				|| pattern.replaceAll(" ","").equals("d(d):d(yyyy),")
				|| pattern.replaceAll(" ","").equals("d(d):d(yyyy):")
				|| pattern.replaceAll(" ","").equals("d(d):d(yyyy);")
				|| pattern.replaceAll(" ","").equals("d(d):yyyy(yyyy)")
				|| pattern.replaceAll(" ","").equals("d(d):yyyy(yyyy).")
				|| pattern.replaceAll(" ","").equals("d(d):yyyy(yyyy),")
				|| pattern.replaceAll(" ","").equals("d(d):yyyy(yyyy):")
				|| pattern.replaceAll(" ","").equals("d(d):yyyy(yyyy);")
				// as above w. "sphalm." or "fig."
				|| pattern.replaceAll(" ","").equals("d(d):d(yyyy)a.")
				|| pattern.replaceAll(" ","").equals("d(d):d(yyyy).a.")
				|| pattern.replaceAll(" ","").equals("d(d):d(yyyy),a.")
				|| pattern.replaceAll(" ","").equals("d(d):d(yyyy):a.")
				|| pattern.replaceAll(" ","").equals("d(d):d(yyyy);a.")
				|| pattern.replaceAll(" ","").equals("d(d):yyyy(yyyy)a.")
				|| pattern.replaceAll(" ","").equals("d(d):yyyy(yyyy).a.")
				|| pattern.replaceAll(" ","").equals("d(d):yyyy(yyyy),a.")
				|| pattern.replaceAll(" ","").equals("d(d):yyyy(yyyy):a.")
				|| pattern.replaceAll(" ","").equals("d(d):yyyy(yyyy);a.")
				// as above w. "in obs."
				|| pattern.replaceAll(" ","").equals("d(d):d(yyyy)aa.")
				|| pattern.replaceAll(" ","").equals("d(d):d(yyyy).aa.")
				|| pattern.replaceAll(" ","").equals("d(d):d(yyyy),aa.")
				|| pattern.replaceAll(" ","").equals("d(d):d(yyyy):aa.")
				|| pattern.replaceAll(" ","").equals("d(d):d(yyyy);aa.")
				|| pattern.replaceAll(" ","").equals("d(d):yyyy(yyyy)aa.")
				|| pattern.replaceAll(" ","").equals("d(d):yyyy(yyyy).aa.")
				|| pattern.replaceAll(" ","").equals("d(d):yyyy(yyyy),aa.")
				|| pattern.replaceAll(" ","").equals("d(d):yyyy(yyyy):aa.")
				|| pattern.replaceAll(" ","").equals("d(d):yyyy(yyyy);aa.")
				// as above w. "nom. nov."
				|| pattern.replaceAll(" ","").equals("d(d):d(yyyy)a.a.")
				|| pattern.replaceAll(" ","").equals("d(d):d(yyyy).a.a.")
				|| pattern.replaceAll(" ","").equals("d(d):d(yyyy),a.a.")
				|| pattern.replaceAll(" ","").equals("d(d):d(yyyy):a.a.")
				|| pattern.replaceAll(" ","").equals("d(d):d(yyyy);a.a.")
				|| pattern.replaceAll(" ","").equals("d(d):yyyy(yyyy)a.a.")
				|| pattern.replaceAll(" ","").equals("d(d):yyyy(yyyy).a.a.")
				|| pattern.replaceAll(" ","").equals("d(d):yyyy(yyyy),a.a.")
				|| pattern.replaceAll(" ","").equals("d(d):yyyy(yyyy):a.a.")
				|| pattern.replaceAll(" ","").equals("d(d):yyyy(yyyy);a.a.")
				// as above w. "without basionym ref."
				|| pattern.replaceAll(" ","").equals("d(d):d(yyyy)aaa.")
				|| pattern.replaceAll(" ","").equals("d(d):d(yyyy).aaa.")
				|| pattern.replaceAll(" ","").equals("d(d):d(yyyy),aaa.")
				|| pattern.replaceAll(" ","").equals("d(d):d(yyyy):aaa.")
				|| pattern.replaceAll(" ","").equals("d(d):d(yyyy);aaa.")
				|| pattern.replaceAll(" ","").equals("d(d):yyyy(yyyy)aaa.")
				|| pattern.replaceAll(" ","").equals("d(d):yyyy(yyyy).aaa.")
				|| pattern.replaceAll(" ","").equals("d(d):yyyy(yyyy),aaa.")
				|| pattern.replaceAll(" ","").equals("d(d):yyyy(yyyy):aaa.")
				|| pattern.replaceAll(" ","").equals("d(d):yyyy(yyyy);aaa.")
				
				|| pattern.replaceAll(" ","").equals("yyyy(d):d(yyyy)")
				|| pattern.replaceAll(" ","").equals("yyyy(d):d(yyyy).")
				|| pattern.replaceAll(" ","").equals("yyyy(d):d(yyyy),")
				|| pattern.replaceAll(" ","").equals("yyyy(d):d(yyyy):")
				|| pattern.replaceAll(" ","").equals("yyyy(d):d(yyyy);")
				|| pattern.replaceAll(" ","").equals("yyyy(d):yyyy(yyyy)")
				|| pattern.replaceAll(" ","").equals("yyyy(d):yyyy(yyyy).")
				|| pattern.replaceAll(" ","").equals("yyyy(d):yyyy(yyyy),")
				|| pattern.replaceAll(" ","").equals("yyyy(d):yyyy(yyyy):")
				|| pattern.replaceAll(" ","").equals("yyyy(d):yyyy(yyyy);")
				// as above w. "sphalm." or "fig."    yyyy
				|| pattern.replaceAll(" ","").equals("yyyy(d):d(yyyy)a.")
				|| pattern.replaceAll(" ","").equals("yyyy(d):d(yyyy).a.")
				|| pattern.replaceAll(" ","").equals("yyyy(d):d(yyyy),a.")
				|| pattern.replaceAll(" ","").equals("yyyy(d):d(yyyy):a.")
				|| pattern.replaceAll(" ","").equals("yyyy(d):d(yyyy);a.")
				|| pattern.replaceAll(" ","").equals("yyyy(d):yyyy(yyyy)a.")
				|| pattern.replaceAll(" ","").equals("yyyy(d):yyyy(yyyy).a.")
				|| pattern.replaceAll(" ","").equals("yyyy(d):yyyy(yyyy),a.")
				|| pattern.replaceAll(" ","").equals("yyyy(d):yyyy(yyyy):a.")
				|| pattern.replaceAll(" ","").equals("yyyy(d):yyyy(yyyy);a.")
				// as above w. "in obs."              yyyy
				|| pattern.replaceAll(" ","").equals("yyyy(d):d(yyyy)aa.")
				|| pattern.replaceAll(" ","").equals("yyyy(d):d(yyyy).aa.")
				|| pattern.replaceAll(" ","").equals("yyyy(d):d(yyyy),aa.")
				|| pattern.replaceAll(" ","").equals("yyyy(d):d(yyyy):aa.")
				|| pattern.replaceAll(" ","").equals("yyyy(d):d(yyyy);aa.")
				|| pattern.replaceAll(" ","").equals("yyyy(d):yyyy(yyyy)aa.")
				|| pattern.replaceAll(" ","").equals("yyyy(d):yyyy(yyyy).aa.")
				|| pattern.replaceAll(" ","").equals("yyyy(d):yyyy(yyyy),aa.")
				|| pattern.replaceAll(" ","").equals("yyyy(d):yyyy(yyyy):aa.")
				|| pattern.replaceAll(" ","").equals("yyyy(d):yyyy(yyyy);aa.")
				// as above w. "nom. nov."            yyyy
				|| pattern.replaceAll(" ","").equals("yyyy(d):d(yyyy)a.a.")
				|| pattern.replaceAll(" ","").equals("yyyy(d):d(yyyy).a.a.")
				|| pattern.replaceAll(" ","").equals("yyyy(d):d(yyyy),a.a.")
				|| pattern.replaceAll(" ","").equals("yyyy(d):d(yyyy):a.a.")
				|| pattern.replaceAll(" ","").equals("yyyy(d):d(yyyy);a.a.")
				|| pattern.replaceAll(" ","").equals("yyyy(d):yyyy(yyyy)a.a.")
				|| pattern.replaceAll(" ","").equals("yyyy(d):yyyy(yyyy).a.a.")
				|| pattern.replaceAll(" ","").equals("yyyy(d):yyyy(yyyy),a.a.")
				|| pattern.replaceAll(" ","").equals("yyyy(d):yyyy(yyyy):a.a.")
				|| pattern.replaceAll(" ","").equals("yyyy(d):yyyy(yyyy);a.a.")
				// as above w. "without basionym ref."yyyy
				|| pattern.replaceAll(" ","").equals("yyyy(d):d(yyyy)aaa.")
				|| pattern.replaceAll(" ","").equals("yyyy(d):d(yyyy).aaa.")
				|| pattern.replaceAll(" ","").equals("yyyy(d):d(yyyy),aaa.")
				|| pattern.replaceAll(" ","").equals("yyyy(d):d(yyyy):aaa.")
				|| pattern.replaceAll(" ","").equals("yyyy(d):d(yyyy);aaa.")
				|| pattern.replaceAll(" ","").equals("yyyy(d):yyyy(yyyy)aaa.")
				|| pattern.replaceAll(" ","").equals("yyyy(d):yyyy(yyyy).aaa.")
				|| pattern.replaceAll(" ","").equals("yyyy(d):yyyy(yyyy),aaa.")
				|| pattern.replaceAll(" ","").equals("yyyy(d):yyyy(yyyy):aaa.")
				|| pattern.replaceAll(" ","").equals("yyyy(d):yyyy(yyyy);aaa.")
				)){
			//Example: "1(1): 1 (1988)"
			String[] c = splitCollation(collation.replaceAll(" ",""));
			//Series: ""
			series = "";
			//Volume: "1"
			volume = c[0];
			//Issue: "1"
			issue = c[1];
			//Page: "1"
			page = c[2];
			//Tab/Fig/No: ""
			tab_or_fig_or_no = "";
			//Year: "1988"
			year = c[3];
			rule = "5";
			parsed = true;
		}
		// vol: page (year)
		if (	!parsed &&   
				(pattern.replaceAll(" ","").equals("d:d(yyyy)")
				|| pattern.replaceAll(" ","").equals("d:d(yyyy).")
				|| pattern.replaceAll(" ","").equals("d:d(yyyy),")
				|| pattern.replaceAll(" ","").equals("d:d(yyyy):")
				|| pattern.replaceAll(" ","").equals("d:d(yyyy);")
				|| pattern.replaceAll(" ","").equals("d:yyyy(yyyy)")
				|| pattern.replaceAll(" ","").equals("d:yyyy(yyyy).")
				|| pattern.replaceAll(" ","").equals("d:yyyy(yyyy),")
				|| pattern.replaceAll(" ","").equals("d:yyyy(yyyy):")
				|| pattern.replaceAll(" ","").equals("d:yyyy(yyyy);")
				// as above w. "sphalm." or "fig."
				|| pattern.replaceAll(" ","").equals("d:d(yyyy)a.")
				|| pattern.replaceAll(" ","").equals("d:d(yyyy).a.")
				|| pattern.replaceAll(" ","").equals("d:d(yyyy),a.")
				|| pattern.replaceAll(" ","").equals("d:d(yyyy):a.")
				|| pattern.replaceAll(" ","").equals("d:d(yyyy);a.")
				|| pattern.replaceAll(" ","").equals("d:yyyy(yyyy)a.")
				|| pattern.replaceAll(" ","").equals("d:yyyy(yyyy).a.")
				|| pattern.replaceAll(" ","").equals("d:yyyy(yyyy),a.")
				|| pattern.replaceAll(" ","").equals("d:yyyy(yyyy):a.")
				|| pattern.replaceAll(" ","").equals("d:yyyy(yyyy);a.")
				// as above w. "in obs."
				|| pattern.replaceAll(" ","").equals("d:d(yyyy)aa.")
				|| pattern.replaceAll(" ","").equals("d:d(yyyy).aa.")
				|| pattern.replaceAll(" ","").equals("d:d(yyyy),aa.")
				|| pattern.replaceAll(" ","").equals("d:d(yyyy):aa.")
				|| pattern.replaceAll(" ","").equals("d:d(yyyy);aa.")
				|| pattern.replaceAll(" ","").equals("d:yyyy(yyyy)aa.")
				|| pattern.replaceAll(" ","").equals("d:yyyy(yyyy).aa.")
				|| pattern.replaceAll(" ","").equals("d:yyyy(yyyy),aa.")
				|| pattern.replaceAll(" ","").equals("d:yyyy(yyyy):aa.")
				|| pattern.replaceAll(" ","").equals("d:yyyy(yyyy);aa.")
				// as above w. "nom. nov."
				|| pattern.replaceAll(" ","").equals("d:d(yyyy)a.a.")
				|| pattern.replaceAll(" ","").equals("d:d(yyyy).a.a.")
				|| pattern.replaceAll(" ","").equals("d:d(yyyy),a.a.")
				|| pattern.replaceAll(" ","").equals("d:d(yyyy):a.a.")
				|| pattern.replaceAll(" ","").equals("d:d(yyyy);a.a.")
				|| pattern.replaceAll(" ","").equals("d:yyyy(yyyy)a.a.")
				|| pattern.replaceAll(" ","").equals("d:yyyy(yyyy).a.a.")
				|| pattern.replaceAll(" ","").equals("d:yyyy(yyyy),a.a.")
				|| pattern.replaceAll(" ","").equals("d:yyyy(yyyy):a.a.")
				|| pattern.replaceAll(" ","").equals("d:yyyy(yyyy);a.a.")
				// as above w. "without basionym ref."
				|| pattern.replaceAll(" ","").equals("d:d(yyyy)aaa.")
				|| pattern.replaceAll(" ","").equals("d:d(yyyy).aaa.")
				|| pattern.replaceAll(" ","").equals("d:d(yyyy),aaa.")
				|| pattern.replaceAll(" ","").equals("d:d(yyyy):aaa.")
				|| pattern.replaceAll(" ","").equals("d:d(yyyy);aaa.")
				|| pattern.replaceAll(" ","").equals("d:yyyy(yyyy)aaa.")
				|| pattern.replaceAll(" ","").equals("d:yyyy(yyyy).aaa.")
				|| pattern.replaceAll(" ","").equals("d:yyyy(yyyy),aaa.")
				|| pattern.replaceAll(" ","").equals("d:yyyy(yyyy):aaa.")
				|| pattern.replaceAll(" ","").equals("d:yyyy(yyyy);aaa.")
				// as above w. roman numeral volume
				|| pattern.replaceAll(" ","").equals("r:d(yyyy)")
				|| pattern.replaceAll(" ","").equals("r:d(yyyy)a.a.")
				|| pattern.replaceAll(" ","").equals("r:d(yyyy).a.a.")
				|| pattern.replaceAll(" ","").equals("r:d(yyyy),a.a.")
				|| pattern.replaceAll(" ","").equals("r:d(yyyy):a.a.")
				|| pattern.replaceAll(" ","").equals("r:d(yyyy);a.a.")
				|| pattern.replaceAll(" ","").equals("r:yyyy(yyyy)a.a.")
				|| pattern.replaceAll(" ","").equals("r:yyyy(yyyy).a.a.")
				|| pattern.replaceAll(" ","").equals("r:yyyy(yyyy),a.a.")
				|| pattern.replaceAll(" ","").equals("r:yyyy(yyyy):a.a.")
				|| pattern.replaceAll(" ","").equals("r:yyyy(yyyy);a.a.")
				// as above w. "without basionym ref."r
				|| pattern.replaceAll(" ","").equals("r:d(yyyy)aaa.")
				|| pattern.replaceAll(" ","").equals("r:d(yyyy).aaa.")
				|| pattern.replaceAll(" ","").equals("r:d(yyyy),aaa.")
				|| pattern.replaceAll(" ","").equals("r:d(yyyy):aaa.")
				|| pattern.replaceAll(" ","").equals("r:d(yyyy);aaa.")
				|| pattern.replaceAll(" ","").equals("r:yyyy(yyyy)aaa.")
				|| pattern.replaceAll(" ","").equals("r:yyyy(yyyy).aaa.")
				|| pattern.replaceAll(" ","").equals("r:yyyy(yyyy),aaa.")
				|| pattern.replaceAll(" ","").equals("r:yyyy(yyyy):aaa.")
				|| pattern.replaceAll(" ","").equals("r:yyyy(yyyy);aaa.")
				// as above w. roman numeral volume, page separated by .
				|| pattern.replaceAll(" ","").equals("r.d(yyyy)")
				|| pattern.replaceAll(" ","").equals("r.d(yyyy).")
				|| pattern.replaceAll(" ","").equals("r.d(yyyy),a.")
				|| pattern.replaceAll(" ","").equals("r.d(yyyy)a.a.")
				|| pattern.replaceAll(" ","").equals("r.d(yyyy).a.a.")
				|| pattern.replaceAll(" ","").equals("r.d(yyyy),a.a.")
				|| pattern.replaceAll(" ","").equals("r.d(yyyy):a.a.")
				|| pattern.replaceAll(" ","").equals("r.d(yyyy);a.a.")
				|| pattern.replaceAll(" ","").equals("r.yyyy(yyyy)a.a.")
				|| pattern.replaceAll(" ","").equals("r.yyyy(yyyy).a.a.")
				|| pattern.replaceAll(" ","").equals("r.yyyy(yyyy),a.a.")
				|| pattern.replaceAll(" ","").equals("r.yyyy(yyyy):a.a.")
				|| pattern.replaceAll(" ","").equals("r.yyyy(yyyy);a.a.")
				// as above w. "without basionym ref."r
				|| pattern.replaceAll(" ","").equals("r.d(yyyy)aaa.")
				|| pattern.replaceAll(" ","").equals("r.d(yyyy).aaa.")
				|| pattern.replaceAll(" ","").equals("r.d(yyyy),aaa.")
				|| pattern.replaceAll(" ","").equals("r.d(yyyy):aaa.")
				|| pattern.replaceAll(" ","").equals("r.d(yyyy);aaa.")
				|| pattern.replaceAll(" ","").equals("r.yyyy(yyyy)aaa.")
				|| pattern.replaceAll(" ","").equals("r.yyyy(yyyy).aaa.")
				|| pattern.replaceAll(" ","").equals("r.yyyy(yyyy),aaa.")
				|| pattern.replaceAll(" ","").equals("r.yyyy(yyyy):aaa.")
				|| pattern.replaceAll(" ","").equals("r.yyyy(yyyy);aaa.")				
				)){
			//Example: "1: 1 (1988)"
			String[] c = splitCollation(collation.replaceAll(" ",""));
			//Series: ""
			series = "";
			//Volume: "1"
			volume = c[0];
			//Issue: "1"
			issue = "";
			//Page: "1"
			page = c[1];
			//Tab/Fig/No: ""
			tab_or_fig_or_no = "";
			//Year: "1988"
			year = c[2];
			rule = "5.1";
			parsed = true;
		}

		
		if (!parsed && 
				(
						(pattern.equals("r. d") || patternMatchesWithNotes(pattern, "r. d", true)) 
						|| 
						(pattern.equals("r. d.") || patternMatchesWithNotes(pattern, "r. d.", true))
						)
					){
			//Example: "x. 89."
			//Series: ""
			series = "";
			//Volume: "x"
			volume = splitCollation(collation)[0];
			//Issue: ""
			issue = "";
			//Page: "89"
			page = splitCollation(collation)[1];
			//Tab/Fig/No: ""
			tab_or_fig_or_no = "";
			//Year: ""
			year = "";
			rule = "6";
			parsed = true;
		}
		if (!parsed && (pattern.replaceAll(" ","").equals("d,a.d:d"))){
			String[] c = splitCollation(collation.replaceAll(" ",""));
			//"5, pt. 1: 123"
			volume = c[0]; // 5
			issue = c[2]; // 1
			page = c[3]; // 123
			rule = "9";
			parsed = true;
		}
		if (!parsed && (pattern.replaceAll(" ","").equals("yyyy,r.d.")
				|| pattern.replaceAll(" ","").equals("yyyy,r.d"))){
			String[] c = splitCollation(collation.replaceAll(" ",""));
			//Example: "1901, xv. 58"
			volume = c[1]; // xv
			page = c[2]; // 58
			year = c[0]; // 1901
			rule = "15";
			parsed = true;
		}
		if (!parsed && (pattern.replaceAll(" ","").equals("(yyyy)r.d.")
				|| pattern.replaceAll(" ","").equals("(yyyy),r.d.")
				|| pattern.replaceAll(" ","").equals("(yyyy)r.d")
				|| pattern.replaceAll(" ","").equals("(yyyy),r.d"))){
			String[] c = splitCollation(collation.replaceAll(" ",""));
			//Example: "(1901), xv. 58
			volume = c[2]; // xv
			page = c[3]; // 58
			year = c[1]; // 1901
			rule = "17";
			parsed = true;
		}
		if (!parsed && (pattern.replaceAll(" ","").equals("yyyy,d.")
				|| pattern.replaceAll(" ","").equals("yyyy,d"))){
			String[] c = splitCollation(collation.replaceAll(" ",""));
			//Example: "1901, 58
			page = c[1]; // 58
			year = c[0]; // 1901
			rule = "20";
			parsed = true;
		}
		if (!parsed && (pattern.replaceAll(" ","").equals("d:d(-d)")
				|| pattern.replaceAll(" ","").equals("d:d(-d)."))){
			String[] c=splitCollation(collation.replaceAll(" ",""));
			// Example: "12: 34(-7)"
			volume=c[0]; // 12
			page=c[1]; // 34
			parsed=true;
			rule = "22";
		}
		if (!parsed && (pattern.replaceAll(" ","").equals("d.d.yyyy")
				||pattern.replaceAll(" ","").equals("d.d.yyyy."))){
			String[] c=splitCollation(collation.replaceAll(" ",""));
			volume=c[0];
			page=c[1];
			year=c[2];
			parsed=true;
			rule = "24";
		}
		if (!parsed && (pattern.replaceAll(" ","").equals("d(d,a.d):d"))){
		    String[] c=splitCollation(collation.replaceAll(" ",""));
		    volume=c[0];
		    issue=c[1]+", "+c[2]+". "+c[3];
		    page=c[4];
		    parsed=true;
		    rule = "25";
		  }
		  if (!parsed && (pattern.replaceAll(" ","").equals("d:d,a.d")
				  ||pattern.replaceAll(" ","").equals("d:d(a.d)")
				  || pattern.replaceAll(" ","").equals("d:yyyy,a.d")
				  ||pattern.replaceAll(" ","").equals("yyyy:d(a.d)")
				  || pattern.replaceAll(" ","").equals("r.d,a.d")
				  || pattern.replaceAll(" ","").equals("r.yyyy,a.d")
				  || pattern.replaceAll(" ","").equals("r.d.a.d.")
				  || pattern.replaceAll(" ","").equals("r.d.a.d")
				  || pattern.replaceAll(" ","").equals("r.yyyy.a.d")
				  || pattern.replaceAll(" ","").equals("r.yyyy.a.d."))){
		    String[] c=splitCollation(collation.replaceAll(" ",""));
		    volume=c[0];
		    page=c[1];
		    tab_or_fig_or_no=c[2]+". "+c[3];
		    parsed=true;
		    rule = "28";
		  }
		  if (!parsed && (pattern.replaceAll(" ","").equals("d:d,a.d,d")
				  ||pattern.replaceAll(" ","").equals("d:d(a.d,d)")
				  || pattern.replaceAll(" ","").equals("d:yyyy,a.d,d")
				  ||pattern.replaceAll(" ","").equals("yyyy:d(a.d,d)")
				  || pattern.replaceAll(" ","").equals("r.d,a.d,d")
				  || pattern.replaceAll(" ","").equals("r.yyyy,a.d,d")
				  || pattern.replaceAll(" ","").equals("r.d.a.d,d.")
				  || pattern.replaceAll(" ","").equals("r.d.a.d,d")
				  || pattern.replaceAll(" ","").equals("r.yyyy.a.d,d")
				  || pattern.replaceAll(" ","").equals("r.yyyy.a.d,d."))){
		    String[] c=splitCollation(collation.replaceAll(" ",""));
		    volume=c[0];
		    page=c[1];
		    tab_or_fig_or_no=c[2]+". "+c[3]+", "+c[4];
		    parsed=true;
		    rule = "28.1";
		  }		 
		  if (!parsed && (pattern.replaceAll(" ","").equals("d:d(-d;a.d)"))){
			    String[] c=splitCollation(collation.replaceAll(" ",""));
			    volume=c[0];
			    page=c[1]+c[2];
			    tab_or_fig_or_no=c[3]+". "+c[4];
			    parsed=true;
			    rule = "28.2";			  
		  }
		  if (!parsed && (pattern.replaceAll(" ","").equals("r.(yyyy)d.")
				  ||pattern.replaceAll(" ","").equals("r.(yyyy)d")
				  ||pattern.replaceAll(" ","").equals("r(yyyy)d"))){
			String[] c=splitCollation(collation.replaceAll(" ",""));
		    volume=c[0];
		    year=c[1];
		    page=c[2];
		    parsed=true;
		    rule = "29";
		  }
		  if (!parsed && pattern.replaceAll(" ","").equals("yyyy,d(yyyy)")){
			String[] c=splitCollation(collation.replaceAll(" ",""));
		    volume=c[0];
		    page=c[1];
		    year=c[2];
		    parsed=true;
		    rule = "30";
		  }
		  if (!parsed && pattern.replaceAll(" ","").equals("d:d(-d),a")){
			String[] c=splitCollation(collation.replaceAll(" ",""));
		    volume=c[0];
		    page=c[1];
		    parsed=true;
		    rule = "32";
		  }
		  if (!parsed && pattern.replaceAll(" ","").equals("d:d,d")){
			String[] c=splitCollation(collation.replaceAll(" ",""));
		    volume=c[0];
		    page=c[1];
		    parsed=true;
		    rule = "33";
		  }
		  
		  if (!parsed && pattern.replaceAll(" ","").equals("d(d):d(yyyy),a.a.:")){
			String[] c=splitCollation(collation.replaceAll(" ",""));
		    volume=c[0];
		    issue=c[1];
		    page=c[2];
		    year=c[3];
		    parsed=true;
		    rule = "34";
		  }
		  if (!parsed && pattern.replaceAll(" ","").equals("d(d):d(-d;a.d)")){
			String[] c=splitCollation(collation.replaceAll(" ",""));
			// Example: 1(1): 217 (-219; fig. 11)
		    volume=c[0]; // 1
		    issue=c[1]; // 1
		    page=c[2]; // 217
		    tab_or_fig_or_no=c[4]+". "+c[5]; // fig. 11
		    parsed=true;
		    rule = "35";
		  }
		  if (!parsed && pattern.replaceAll(" ","").equals("d:d(yyyy-d)")){
			String[] c=splitCollation(collation.replaceAll(" ",""));
			// Example: 30: 101 (1859-61)
		    volume=c[0];
		    page=c[1];
		    year=c[2].split("\\-")[0];
		    parsed=true;
		    rule = "36";
		  }
		  // Rule #37 - see rule #29
		  if (!parsed && pattern.replaceAll(" ","").equals("d,a.d:d,a.d,a.d")){
			String[] c=splitCollation(collation.replaceAll(" ",""));
			// Example: 1, pt. 2: 112, tab. 5, fig. 2
		    volume=c[0]; // 1 
		    issue=c[2]; // 2
		    page=c[3]; // 112
		    tab_or_fig_or_no=c[4]+". "+c[5]+", "+c[6]+". " + c[7]; // tab. 5, fig. 2 
		    parsed=true;
		    rule = "38";
		  }
		  if (!parsed && pattern.replaceAll(" ","").equals("d:d,a.d,a.d")){
			String[] c=splitCollation(collation.replaceAll(" ",""));
		    volume=c[0];
		    page=c[1];
		    tab_or_fig_or_no=c[2]+". "+c[3]+", "+c[4]+". "+c[5];
		    parsed=true;
		    rule = "38.1";
		  }
		  
		  // Rule 39 handled under rule #4
		  
		  if (!parsed && (pattern.replaceAll(" ","").equals("r.d(yyyy),aa.")
				  || pattern.replaceAll(" ","").equals("r.d(yyyy)aa."))){
			String[] c=splitCollation(collation.replaceAll(" ",""));
			// Example: C. 433 (1895), in obs.
		    volume=c[0];
		    page=c[1];
		    year=c[2];
		    parsed=true;
		    rule = "40";
		  }
		  if (!parsed && (pattern.replaceAll(" ","").equals("r.a.d,d"))){
			String[] c=splitCollation(collation.replaceAll(" ",""));
			volume=c[0];
			issue=c[2];
			page=c[3];
		    parsed=true;
		    rule = "41";
		  }
		  if (!parsed && (
				  (pattern.replaceAll(" ","").equals("r.a.d,d(yyyy)")
						  ||pattern.replaceAll(" ","").equals("r.a.d,d(yyyy).")))){
			String[] c=splitCollation(collation.replaceAll(" ",""));
			volume=c[0];
			issue=c[2];
			page=c[3];
			page=c[4];
		    parsed=true;
		    rule = "41.1";
		  }

		  // Rule #42 - see rule #5
		  
		  // Rule #43 - see rule #24

		  // Rule #44 - see rule #3
		  // See rule #24

		  if (!parsed && (pattern.replaceAll(" ","").equals("d(a.):d"))){
			String[] c=splitCollation(collation.replaceAll(" ",""));
			// Example: 24(Misc.): 71
		    volume=c[0]+"("+c[1]+".)"; // 24(Misc.)
		    page=c[2]; // 71
		    parsed=true;
		    rule = "45";
		  }
		  if (!parsed && (pattern.replaceAll(" ","").equals("d(yyyy).")
				  || pattern.replaceAll(" ","").equals("d(yyyy)")
				  || pattern.replaceAll(" ","").equals("d.yyyy."))){
			String[] c=splitCollation(collation.replaceAll(" ",""));
			// Example: 7 (1932).
		    page=c[0]; // 7
		    year=c[1]; // 1932
		    parsed=true;
		    rule = "46";
		  }
		  
		  // Rule #47 - see rule #5
		  
		  if (!parsed && (pattern.replaceAll(" ","").equals("d(d):d,a.d")
				  ||pattern.replaceAll(" ","").equals("d(d):d(a.d)"))){
			String[] c=splitCollation(collation.replaceAll(" ",""));
		    volume=c[0];
		    issue=c[1];
		    page=c[2];
		    tab_or_fig_or_no=c[3]+". "+c[4];
		    parsed=true;
		    rule = "48";
		  }
		  
		  // Rule #49 - see rule 3

		  // Rule #50 - see rule #46
		  
		  if (!parsed && (pattern.replaceAll(" ","").equals("d,a.d:d,a.d"))){
			String[] c=splitCollation(collation.replaceAll(" ",""));
		    volume=c[0];
		    issue=c[2];
		    page=c[3];
		    tab_or_fig_or_no=c[4]+". "+c[5];
		    parsed=true;
		    rule = "51";
		  }
		  
		  if (!parsed && (pattern.replaceAll(" ","").equals("(yyyy)d.")
				  || pattern.replaceAll(" ","").equals("(yyyy)d"))){
			String[] c=splitCollation(collation.replaceAll(" ",""));
			// Example: (1756) 249.
		    year=c[1]; // 1756
		    page=c[2]; // 249
		    parsed=true;
		    rule = "52";
		  }
		  
		  // Rule #53 - see rule #52

		  // Rule #54 - see rule #4
		  
		  if (!parsed && (pattern.replaceAll(" ","").equals("a.d,d:d" ) || pattern.replaceAll(" ","").equals("a.d,d:yyyy"))){
			  String[] c=splitCollation(collation.replaceAll(" ",""));
			  if (c[0].equalsIgnoreCase("ser")){
			      series=c[1];
			      volume=c[2];
			      page=c[3];
			      parsed=true;
			      rule = "55";
			  }
		  }
		  if (!parsed && (pattern.replaceAll(" ","").equals("a.d,d(d):d" ) || pattern.replaceAll(" ","").equals("a.d,d(d):yyyy"))){
			  String[] c=splitCollation(collation.replaceAll(" ",""));
			  if (c[0].equalsIgnoreCase("ser")){
			      series=c[1];
			      volume=c[2];
			      issue=c[3];
			      page=c[4];
			      parsed=true;
			      rule = "56";
			  }
		  }
		  if (!parsed && (pattern.replaceAll(" ","").equals("a.d"))){
			  String[] c=splitCollation(collation.replaceAll(" ",""));
			  // Example: Acerac. 22
			  page = c[1];
			  parsed = true;
			  rule = "56.1";
		  }
		  if (!parsed && (pattern.replaceAll(" ","").equals("r.r.(yyyy)d"))){
			  String[] c=splitCollation(collation.replaceAll(" ",""));
			  if(c[0].toUpperCase().equals(c[0])){
				  series=c[0];
				  volume=c[1];
				  year=c[2];
				  page=c[3];
				  parsed = true;
				  rule = "56.111";				  
			  }
			  else{
				  volume=c[0];
				  issue=c[1];
				  year=c[2];
				  page=c[3];
				  parsed = true;
				  rule = "56.112";				  				  
			  }
		  }
		  
		  if (!parsed && (pattern.replaceAll(" ","").equals("a.d,d(yyyy)"))){
			  String[] c=splitCollation(collation.replaceAll(" ",""));
			  // Example: No. 154, 65 (1963)
			  if (c[0].equals("No")||c[0].equals("Fasc")||c[0].equals("Pt")){
				  volume=c[1];
				  page=c[2];
				  year=c[3];
				  rule="56.3";
				  parsed=true;
			  }
		  }
		  
		  if (!parsed && (pattern.replaceAll(" ","").equals("a.r,r.d(yyyy).")
				  || pattern.replaceAll(" ","").equals("a.r,r.d(yyyy)"))){
			  // Example: Ser. I, ix. 16 (1911).
			  String[] c=splitCollation(collation.replaceAll(" ",""));
			  series = c[1];
			  volume = c[2];
			  page = c[3];
			  year = c[4];
			  parsed = true;
			  rule = "56.4";
		  }

		  if (!parsed && (pattern.replaceAll(" ","").equals("r.r.d(yyyy)"))){
			  String[] c=splitCollation(collation.replaceAll(" ",""));
			  volume=c[0];
			  issue=c[1];
			  page=c[2];
			  year=c[3];
			  rule="56.13";
			  parsed=true;
		  }
		  if (!parsed && (pattern.replaceAll(" ","").equals("d:a.d")
				  || pattern.replaceAll(" ","").equals("d:a.yyyy"))){
			  // 4: t. 334
			  String[] c=splitCollation(collation.replaceAll(" ",""));
			  volume=c[0];
			  tab_or_fig_or_no=c[1]+". "+c[2];
			  parsed = true;
			  rule = "56.15";						  			  
		  }

		  if (!parsed && (pattern.replaceAll(" ","").equals("d.yyyy"))){		  
			  // 2. 1857
			  String[] c=splitCollation(collation.replaceAll(" ",""));
			  page=c[0];
			  year=c[1];
			  parsed = true;
			  rule = "56.8";
		  }

		  if (!parsed && (pattern.replaceAll(" ","").equals("r.(yyyy)r.d")
				  ||pattern.replaceAll(" ","").equals("r.(yyyy)r.d."))){
			  // Example: xxx. (1857) II. 67.
			  String[] c=splitCollation(collation.replaceAll(" ",""));
			  volume=c[0];
			  year=c[1];
			  issue=c[2];
			  page=c[3];
			  parsed = true;
			  rule = "56.9";						  
		  }

		  
		  if (!parsed && (pattern.replaceAll(" ","").equals("r.d(yyyy)d"))){
			String[] c=splitCollation(collation.replaceAll(" ",""));
			if(c[1].matches(".*[A-Za-z].*")){
				  series=c[0];
				  volume=c[1];
				  year=c[2];
				  page=c[3];
				  rule="62.5";
				  parsed=true;				
			}
			else{
				volume=c[0];
				issue=c[1];
				year=c[2];
				page=c[3];
				rule = "62.4";
				parsed=true;
			}
		  }
		  if (pattern.replaceAll(" ","").equals("d,a.d")
				  ||pattern.replaceAll(" ","").equals("d.a.d")
				  ||pattern.replaceAll(" ","").equals("d.a.d.")){
			  String[] c=splitCollation(collation.replaceAll(" ",""));
			  page=c[0];
			  tab_or_fig_or_no=c[1]+". "+c[2];
			  rule = "63.1";
			  parsed=true;
		  }
		  //17, Heft 74: 36
		  if (pattern.equals("d, a d: d")){
			  String[] c=splitCollation(collation);
			  volume=c[0];
			  issue=c[1] + " " + c[2];
			  page=c[3];
			  rule = "63.5";
			  parsed=true;
		  }

		  //94: sub t. 5720
		  if (pattern.equals("d: a a. d")||pattern.equals("d: a a. yyyy")){
			  String[] c=splitCollation(collation);
			  volume=c[0];
			  tab_or_fig_or_no=c[1]+" " + c[2] + ". "+c[3];
			  rule = "65.15";
			  parsed=true;
		  }


		  if (pattern.replaceAll(" ","").equals("a.d:d")){
			  String[] c=splitCollation(collation.replaceAll(" ",""));
			  volume=c[0]+". "+c[1];
			  page=c[2];
			  rule = "65.2";
			  parsed=true;
		  }
		  if (!parsed && (pattern.replaceAll(" ","").equals("d(d.d):d")
				  ||pattern.replaceAll(" ","").equals("d(d,d):d")
				  ||pattern.replaceAll(" ","").equals("d(d:d):d")
				  ||pattern.replaceAll(" ","").equals("d(d/d):d"))){
			  String[] c=splitCollation(collation.replaceAll(" ",""));
			  volume=c[0];
			  issue=collation.replaceAll(" ","").split("[\\(\\)]")[1];
			  page=c[3];
			  rule = "65.4";
			  parsed=true;
		  }
		  if (pattern.replaceAll(" ","").equals("d(a.d):d")
				  || pattern.replaceAll(" ","").equals("d(d,a.):d")
				  || pattern.replaceAll(" ","").equals("d(d,a.a.):d")){
			  String[] c=splitCollation(collation.replaceAll(" ",""));
			  issue=collation.split("[\\(\\)]")[1];
			  volume=c[0];
			  page=collation.replaceAll(" ","").split(":")[1];
			  rule = "65.5";
			  parsed=true;
		  }
		  if (pattern.replaceAll(" ","").equals("r.a.d")){
			  String[] c=splitCollation(collation.replaceAll(" ",""));
			  if (c[1].equals("t")
					  || c[1].equals("fig")
					  || c[1].equals("n")){
				  volume=c[0];
				  tab_or_fig_or_no=c[1]+". "+c[2];
				  rule = "65.1";
				  parsed=true;				  
			  }
			  else{
				  volume=c[0]+"."+c[1];
				  page=c[2];
				  rule = "65.1";
				  parsed=true;				  				  
			  }
		  }
		  if (pattern.replaceAll(" ","").equals("a.r.r.d.")){
			  // Example: Ser. II. i. 1074.
			  String[] c=splitCollation(collation.replaceAll(" ",""));
			  series=c[1]; // II
			  volume=c[2]; // i
			  page=c[3]; // 1074
			  parsed = true;
			  rule = "56.5";
		  }
		  if (pattern.replaceAll(" ","").equals("a.-a.d(yyyy)")){
			  String[] c=splitCollation(collation.replaceAll(" ",""));
			  series=c[0]+"."+c[1]+".";
			  page=c[2];
			  year=c[3];
			  parsed = true;
			  rule = "56.7";
		  }
		  if (pattern.replaceAll(" ","").equals("r.r.d")){
			  String[] c=splitCollation(collation.replaceAll(" ",""));
			  volume=c[0];
			  issue=c[1];
			  page=c[2];
			  parsed = true;
			  rule = "56.12";			
		  }
		  if (pattern.replaceAll(" ","").equals("yyyy,r.d(yyyy).")){
		  		// Example: 1851, iii. 160 (1855).
			  String[] c=splitCollation(collation.replaceAll(" ",""));
			  volume=c[1];
			  page=c[2];
			  year=c[3];
			  parsed = true;
			  rule = "56.14";						  
		  }
		  if (pattern.replaceAll(" ","").equals("d:a.d")
				  || pattern.replaceAll(" ","").equals("d:a.yyyy")){
			  // 4: t. 334
			  String[] c=splitCollation(collation.replaceAll(" ",""));
			  volume=c[0];
			  tab_or_fig_or_no=c[1]+". "+c[2];
			  parsed = true;
			  rule = "56.15";						  			  
		  }
		  if (pattern.equals("(a yyyy) d")){
			  // Example: (Aout 1819) 98 
			  String[] c=splitCollation(collation);
			  year=c[2];
			  page=c[3];
			  parsed = true;
			  rule = "56.17";						  			  			  			  
		  }
		  
		  if (pattern.replaceAll(" ","").equals("a.d,d")){
			  String[] c=splitCollation(collation.replaceAll(" ",""));
			  if (c[0].equals("ed")){
				  series=c[0]+". "+c[1];
				  page=c[2];
				  rule="56.21";
				  parsed=true;
			  }
			  else if (c[0].equals("no")){
				  volume=c[1];
				  page=c[2];
				  rule="56.22";
				  parsed=true;				  
			  }
			  else if (c[0].equals("t")){
				  tab_or_fig_or_no=c[0]+". "+c[1]+", "+c[2];
				  rule="56.23";
				  parsed=true;				  				  
			  }			  
		  }
		  if (pattern.replaceAll(" ","").equals("r.d.d.yyyy")){
			  String[] c=splitCollation(collation.replaceAll(" ",""));
			  series=c[0];
			  volume=c[1];
			  page=c[2];
			  year=c[3];
			  rule="62";
			  parsed=true;
		  }
		  //r. d, d (yyyy).
		  if (pattern.replaceAll(" ","").equals("r.d,d(yyyy).")){
			  String[] c=splitCollation(collation.replaceAll(" ",""));
			  volume=c[0];
			  page=c[1] + ", " + c[2];
			  year=c[3];
			  rule="62.3";
			  parsed=true;
		  }
		  // a., d(d): d
		  if (pattern.replaceAll(" ","").equals("a.,d(d):d")){
			  String[] c=splitCollation(collation.replaceAll(" ",""));
			  volume=c[0] + "., " + c[1];
			  issue=c[2];
			  page=c[3];
			  rule="65.10";
			  parsed=true;
		  }
		  //yyyy, a. r. r. d.
		  if (pattern.replaceAll(" ","").equals("yyyy,a.r.r.d.")){
			  String[] c=splitCollation(collation.replaceAll(" ",""));
			  year=c[0];
			  series=c[2];
			  volume=c[3];
			  page=c[4];
			  rule="67";
			  parsed=true;
		  }
		  //d; a. a. r. d
		  if (pattern.replaceAll(" ","").equals("d;a.a.r.d")){
			  String[] c=splitCollation(collation.replaceAll(" ",""));
			  page=c[0];
			  rule="70";
			  parsed=true;
		  }
		  //a.-a.-a. d (yyyy)
		  if (pattern.replaceAll(" ","").equals("a.-a.-a.d(yyyy)")){
			  String[] c=splitCollation(collation.replaceAll(" ",""));
			  series=c[0] + "." + c[1] + "." + c[2] + ".";
			  page=c[3];
			  year=c[4];
			  rule="71";
			  parsed=true;
		  }
		  //a. r. d (yyyy)
		  if (pattern.replaceAll(" ","").equals("a.r.d(yyyy)")){
			  String[] c=splitCollation(collation.replaceAll(" ",""));
			  if (c[0].equalsIgnoreCase("Suppl")){
				  volume="Suppl. " + c[1];
				  page=c[2];
				  year=c[3];
				  rule="75.2";
				  parsed = true;
			  }
			  else{
				  volume=c[1];
				  page=c[2];
				  year=c[3];
				  rule="75.1";
				  parsed = true;				  
			  }
		  }
		  //d(a): d
		  if (pattern.replaceAll(" ","").equals("d(a):d")||pattern.replaceAll(" ","").equals("d(a):yyyy")){
			  String[] c=splitCollation(collation.replaceAll(" ",""));
			  volume=c[0]+"("+c[1]+")";
			  page=c[2];
			  rule="65.17";
			  parsed=true;
		  }
		  //a. r. r. d (yyyy).
		  if (pattern.replaceAll(" ","").equals("a.r.r.d(yyyy).")){
			  String[] c=splitCollation(collation.replaceAll(" ",""));
			  if (c[0].equalsIgnoreCase("Ser")){
				  series=c[1];
				  volume=c[2];
				  page=c[3];
				  year=c[4];
				  rule="77";
				  parsed=true;				  
			  }
		  }
		  if (pattern.replaceAll(" ","").equals("d(d):a.d")||pattern.replaceAll(" ","").equals("d(d):a.yyyy")){
			  String[] c=splitCollation(collation.replaceAll(" ",""));
			  volume=c[0];
			  issue=c[1];
			  tab_or_fig_or_no=c[2] + ". " + c[3];
			  rule="78";
			  parsed=true;
		  }
		  //yyyy, r. d (yyyy)
		  if(pattern.replaceAll(" ","").equals("yyyy,r.d(yyyy)")){
			  String[] c=splitCollation(collation.replaceAll(" ",""));
			  volume=c[1];
			  page=c[2];
			  year=c[3];
			  parsed=true;
			  rule="65.21";
		  }
		  //r a. d (yyyy)
		  if(pattern.equals("r a. d (yyyy)")){
			  String[] c=splitCollation(collation);
			  volume=c[0] + " " + c[1];
			  page=c[2];
			  year=c[3];
			  parsed=true;
			  rule="80";
		  }
		  // a. d, d, a. d
		  if(pattern.replaceAll(" ","").equals("a.d,d,a.d")){
			  String[] c=splitCollation(collation.replaceAll(" ",""));
			  if (c[0].equalsIgnoreCase("Ser")){
				  series=c[1];
				  volume=c[2];
				  tab_or_fig_or_no=c[3] + ". " + c[4];
				  rule="81";
				  parsed=true;				  
			  }
		  }
		  //a. r, r. (yyyy) d
		  if(pattern.replaceAll(" ","").equals("a.r,r.(yyyy)d")){
			  String[] c=splitCollation(collation.replaceAll(" ",""));
			  if (c[0].equalsIgnoreCase("Ser")){
				  series=c[1];
				  volume=c[2];
				  year=c[3];
				  page=c[4];
				  rule="82";
				  parsed=true;				  
			  }
		  }
		  //r. (yyyy-d) d.
		  if(pattern.replaceAll(" ","").equals("r.(yyyy-d)d.")){
			  String[] c=splitCollation(collation.replaceAll(" ",""));
			  volume=c[0];
			  year=c[1].split("\\-")[0]+","+c[1].split("\\-")[0].substring(0,2)+c[1].split("\\-")[1];
			  page=c[2];
			  parsed=true;
			  rule="83";
		  }
		  //r-r. (yyyy) d
		  if(pattern.replaceAll(" ","").equals("r-r.(yyyy)d")){
			  String[] c=splitCollation(collation.replaceAll(" ",""));
			  volume=c[0];
			  year=c[1];
			  page=c[2];
			  parsed=true;
			  rule="84";
		  }
		  //r.d.a.d(yyyy)
		  if(pattern.replaceAll(" ","").equals("r.d.a.d(yyyy)")){
			  String[] c=splitCollation(collation.replaceAll(" ",""));
			  volume=c[0];
			  page=c[1];
			  tab_or_fig_or_no=c[2] + ". " + c[3];
			  year=c[4];
			  parsed=true;
			  rule="62.6";
		  }
		  //r. d(a d): d
		  if(pattern.equals("r. d(a d): d")){
			  String[] c=splitCollation(collation);
			  series=c[0];
			  volume=c[1];
			  issue=c[2] + " " + c[3];
			  page=c[4];
			  parsed=true;
			  rule="62.8";
		  }
		  //r. d (yyyy); a. a a. a a.
		  if(pattern.equals("r. d (yyyy); a. a a. a a.")){
			  String[] c=splitCollation(collation);
			  volume=c[0];
			  page=c[1];
			  year=c[2];
			  parsed=true;
			  rule="62.9";
		  }
		  //r. d, d (yyyy)
		  if(pattern.replaceAll(" ", "").equals("r.d,d(yyyy)")){
			  String[] c=splitCollation(collation.replaceAll(" ", ""));
			  volume=c[0];
			  page=c[1] + ", " + c[2];
			  year=c[3];
			  parsed=true;
			  rule="62.10";
		  }
		  //r. d. a. d (yyyy).
		  if(pattern.replaceAll(" ", "").equals("r.d.a.d(yyyy).")){
			  String[] c=splitCollation(collation.replaceAll(" ", ""));
			  volume=c[0];
			  page=c[1];
			  tab_or_fig_or_no=c[2] + ". " + c[3];
			  year=c[4];
			  parsed=true;
			  rule="62.11";
		  }
		  //r. d, d
		  if(pattern.replaceAll(" ", "").equals("r.d,d")){
			  String[] c=splitCollation(collation.replaceAll(" ", ""));
			  volume=c[0];
			  page=c[1] + ", " + c[2];
			  parsed=true;
			  rule="62.12";
		  }
		  //d(d):d,d
		  if(pattern.replaceAll(" ", "").equals("d(d):d,d")){
			  String[] c=splitCollation(collation.replaceAll(" ", ""));
			  volume=c[0];
			  issue=c[1];
			  page=c[2]+", "+c[3];
			  parsed=true;
			  rule="62.12";
		  }
		  //d,a.d,r:d
		  if(pattern.replaceAll(" ", "").equals("d,a.d,r:d")){
			  String[] c=splitCollation(collation.replaceAll(" ", ""));
			  series=c[0];
			  volume=c[1]+". "+c[2]+", "+c[3];
			  page=c[4];
			  parsed=true;
			  rule="63.3";
		  }
		  
		  // Final catches if not yet parsed:
		  if (!parsed){
		    // do some final catches - LOOK AT THIS
		    if (pattern.startsWith("d: d")){
		      String[] c=collation.split("[^a-z0-9]+");
		      volume=c[0];
		      page=c[1];
		      parsed=true;
		      rule = "58";
		    }
		    if (pattern.startsWith("d(d): d")){
		      String[] c=collation.split("[^a-z0-9]+");
		      volume=c[0];
		      issue=c[1];
		      page=c[2];
		      parsed=true;
		      rule = "59";
		    }
		    if (pattern.startsWith("r(d): d")){
		      String[] c=collation.split("[^a-z0-9]+");
		      volume=c[0];
		      issue=c[1];
		      page=c[2];
		      parsed=true;
		      rule = "60";
		    }
		    if (pattern.startsWith("r: d")){
		      String[] c=collation.split("[^a-z0-9]+");
		      volume=c[0];
		      page=c[1];
		      parsed=true;
		      rule = "61";
		    }
		    if (pattern.startsWith("d (yyyy)")){
		    	page=collation.split(" ")[0];
		    	year=collation.split("\\(")[1].split("\\)")[0];
		    	parsed=true;
		    	rule = "64";
		    }
		  }
//		  // Final attempt - if we don't have a page at this point:
//		  if (!parsed){
//			  String[] locators = {": d", ": yyyy"};
//			  for (String locator : locators){
//				  if (c_patt.contains(locator)){
//					  if (c_patt.endsWith(locator)){
//						  page =  collation.split(": ")[collation.split(": ").length-1];
//					  }
//					  else{
//						  Pattern pattern = Pattern.compile(": [0-9]");
//						  Matcher matcher = pattern.matcher(collation);
//						  if(matcher.find()){
//							  String rem = collation.substring(matcher.start()+2);
//							  page = rem.split("[^0-9]")[0];
//						  }
//					  }
//					  rule = "65";
//				  }
//			  }
//		  }
		  
		  // Put the components into a list for return
		  String[] c_parsed={series,volume,issue,page,tab_or_fig_or_no,year,rule};
		  return c_parsed;
	}

	public static void main(String[] args) {
		// Tab separated file of id and collation
		String inputfile = args[0];
		String outputfile = args[1];

		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputfile),"utf8"));
				FileWriter fw = new FileWriter(outputfile); BufferedWriter bw = new BufferedWriter(fw);) {
	    	int count = 0;

			String line = null;
	        while ((line = br.readLine()) != null) {
	        	if ((count++ % 10000) == 0){
	        		System.out.println(count);
	        	}
	        	String[] elems = line.split("\t");
	        	String id = elems[0];
	        	if (elems.length > 1){
		        	String collation = elems[1];
		        	
		        	String structure = new CollationStructureTransformer().transform(collation);
		        	
		        	String[] parsed = CollationUtils.parseCollation(collation);
		        	
		        	bw.write(id 
		        			+ "\t" + collation
		        			+ "\t" + structure
		        			+ "\t" + parsed[SERIES_INDEX]
		        			+ "\t" + parsed[VOL_INDEX]
		        			+ "\t" + parsed[ISSUE_INDEX]
		        			+ "\t" + parsed[PAGE_INDEX]
		        			+ "\t" + parsed[TAB_OR_FIG_INDEX]
		        			+ "\t" + parsed[YEAR_INDEX]
		        			+ "\t" + CollationUtils.parsableCollation(collation)
		        			+ "\t" + parsed[RULE_INDEX]
		        			+ "\n");
	        	}
	        	else{
	        		bw.write(id + "\n");
	        	}
	        }
	        bw.flush();
	        bw.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

}