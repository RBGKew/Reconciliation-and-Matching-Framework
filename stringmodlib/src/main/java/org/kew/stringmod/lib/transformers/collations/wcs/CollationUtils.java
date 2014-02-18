package org.kew.stringmod.lib.transformers.collations.wcs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.kew.stringmod.lib.transformers.RomanNumeralTransformer;
import org.kew.stringmod.lib.transformers.collations.CollationStructureTransformer;

public class CollationUtils {

	public static int SERIES_INDEX = 0;
	public static int VOL_INDEX = 1;
	public static int ISSUE_INDEX = 2;
	public static int PAGE_INDEX = 3;
	public static int TAB_OR_FIG_INDEX = 4;
	public static int YEAR_INDEX = 5;
	public static int RULE_INDEX = 6;

	private static String convertRoman(String r){
		return new RomanNumeralTransformer().transform(r);
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

		if (pattern.equals("d: d") 
				|| pattern.equals("d: r")
				|| pattern.equals("yyyy: d") 
				|| pattern.equals("d: yyyy") 
				|| pattern.equals("yyyy: yyyy")){
			//Example: "12: 89"
			//Series: ""
			series = "";
			//Volume: "12"
			volume = splitCollation(collation)[0];
			//Issue: ""
			issue = "";
			//Page: "89"
			page = splitCollation(collation)[1];
			//Tab/Fig/No: ""
			tab_or_fig_or_no = "";
			//Year: ""
			year = "";
			rule = "1";
		}
		if (pattern.equals(", d: d") || pattern.equals(", yyyy: d") || pattern.equals(", d: yyyy") || pattern.equals(", yyyy: yyyy")){
			//Example: ", 12: 89"
			//Series: ""
			series = "";
			//Volume: "12"
			volume = convertRoman(splitCollation(collation)[1]);
			//Issue: ""
			issue = "";
			//Page: "89"
			page = convertRoman(splitCollation(collation)[2]);
			//Tab/Fig/No: ""
			tab_or_fig_or_no = "";
			//Year: ""
			year = "";
			rule = "1.1";
		}		
		if (pattern.equals(": d") || pattern.equals(": yyyy") || pattern.equals(": r")){
			//Example: ": 121"
			//Series: ""
			series = "";
			//Volume: ""
			volume = "";
			//Issue: ""
			issue = "";
			//Page: "121"
			page = collation.split(" ")[1];
			//Tab/Fig/No: ""
			tab_or_fig_or_no = "";
			//Year: ""
			year = "";
			rule = "2";
		}
		if (pattern.equals("d(d): d") || pattern.equals("d(d): yyyy") || pattern.equals("yyyy(d): d")){
			//Example: "12(2): 61"
			//Series: ""
			series = "";
			//Volume: "12"
			volume = splitCollation(collation)[0];
			//Issue: "2"
			issue = splitCollation(collation)[1];
			//Page: "61"
			page = splitCollation(collation)[2];
			//Tab/Fig/No: ""
			tab_or_fig_or_no = "";
			//Year: ""
			year = "";
			rule = "3";
		}
		if (pattern.equals("d: d (yyyy)")){
			//Example: "12: 99 (1898)"
			//Series: ""
			series = "";
			//Volume: "12"
			volume = splitCollation(collation)[0];
			//Issue: ""
			issue = "";
			//Page: "99"
			page = splitCollation(collation)[1];
			//Tab/Fig/No: ""
			tab_or_fig_or_no = "";
			//Year: "1898"
			year = splitCollation(collation)[2];
			rule = "3";
		}
		if (pattern.equals(", r, d: d")){
			//Example: ", III, 102: 414"
			//Series: "III"
			series = splitCollation(collation)[1];
			//Volume: "102"
			volume = splitCollation(collation)[2];
			//Issue: ""
			issue = "";
			//Page: "4141"
			page = splitCollation(collation)[3];
			//Tab/Fig/No: ""
			tab_or_fig_or_no = "";
			//Year: ""
			year = "";
			rule = "65.1";
		}
		if (pattern.equals(", a. d: d")){
			//Example: ", ed. 3: 273"
			// if "a" = "ed" OR "Nachtr" OR "Suppl" OR "Beib" OR "Beih" OR "Reimpr"
			if (splitCollation(collation)[1].equals("ed")
					|| splitCollation(collation)[1].equals("Nachtr")
					|| splitCollation(collation)[1].equals("Suppl")
					|| splitCollation(collation)[1].equals("Beib")
					|| splitCollation(collation)[1].equals("Beih")
					|| splitCollation(collation)[1].equals("Reimpr"))
			//Series: "ed. 3"
			series = splitCollation(collation)[1] + ". " + splitCollation(collation)[2];
			//Volume: ""
			volume = "";
			//Issue: ""
			issue = "";
			//Page: "2731"
			page = splitCollation(collation)[3];
			//Tab/Fig/No: ""
			tab_or_fig_or_no = "";
			//Year: ""
			year = "";
			rule = "65.2";
		}
		if (pattern.equals(", a.a., d: d")){
			//Example: ", a.s., 3: 508"
			// if "a" = "a.s." OR "n.f." OR "n.s."
			if ((splitCollation(collation)[1]+"."+splitCollation(collation)[2]+".").equals("a.s.")
					|| (splitCollation(collation)[1]+"."+splitCollation(collation)[2]+".").equals("n.f.")
					|| (splitCollation(collation)[1]+"."+splitCollation(collation)[2]+".").equals("n.s."))
				series = splitCollation(collation)[1]+"."+splitCollation(collation)[2]+".";
			//Volume: "3"
			volume = splitCollation(collation)[3];
			//Issue: ""
			issue = "";
			//Page: "508"
			page = splitCollation(collation)[4];
			//Tab/Fig/No: ""
			tab_or_fig_or_no = "";
			//Year: ""
			year = "";
			rule = "65.3";
		}
		if (pattern.equals(", a.a., d(d): d")){
			//Example: ", a.s., 3(2): 508"
			// if "a" = "a.s." OR "n.f." OR "n.s."
			if ((splitCollation(collation)[1]+"."+splitCollation(collation)[2]+".").equals("a.s.")
					|| (splitCollation(collation)[1]+"."+splitCollation(collation)[2]+".").equals("n.f.")
					|| (splitCollation(collation)[1]+"."+splitCollation(collation)[2]+".").equals("n.s."))
				series = splitCollation(collation)[1]+"."+splitCollation(collation)[2]+".";
			//Volume: "3"
			volume = splitCollation(collation)[3];
			//Issue: ""
			issue = splitCollation(collation)[4];
			//Page: "508"
			page = splitCollation(collation)[5];
			//Tab/Fig/No: ""
			tab_or_fig_or_no = "";
			//Year: ""
			year = "";
			rule = "65.12";
		}		
		if (pattern.equals(", a. d, d: d")){
			String[] c=splitCollation(collation.replaceAll(" ", ""));
			if (c[1].equals("ed")){
				//Example: ", ed. 9, 1: 197"
				series = c[1]+". "+c[2];
				//Volume: "3"
				volume = c[3];
				//Issue: ""
				issue = "";
				//Page: "508"
				page = c[4];
				//Tab/Fig/No: ""
				tab_or_fig_or_no = "";
				//Year: ""
				year = "";
				rule = "65.4";
			}
			else{
				series=c[2];
				volume=c[3];
				page=c[4];
				rule="65.4a";
			}
		}
		if (pattern.equals(", a. d, d(d): d")){
			String[] c=splitCollation(collation.replaceAll(" ", ""));
			if (c[1].equals("ed")){
				//Example: ", ed. 9, 1(1): 197"
				series = c[1]+". "+c[2];
				//Volume: "3"
				volume = c[3];
				//Issue: ""
				issue = c[4];
				//Page: "508"
				page = c[5];
				//Tab/Fig/No: ""
				tab_or_fig_or_no = "";
				//Year: ""
				year = "";
				rule = "65.49";
			}
			else{
				series=c[2];
				volume=c[3];
				issue=c[4];
				page=c[5];
				rule="65.49a";
			}
		}
		
		if (pattern.equals(", r, d(d): d")){
			// Example: ", II, 1(1): 28"
			//Series: ""
			series = splitCollation(collation)[1];
			//Volume: "C"
			volume = splitCollation(collation)[2];
			//Issue: ""
			issue = splitCollation(collation)[3];
			//Page: "433"
			page = splitCollation(collation)[4];
			//Tab/Fig/No: ""
			tab_or_fig_or_no = "";
			//Year: ""
			year = "";
			rule = "65.5";			
		}
		// 1(Conif.): 42	d(a.): d		1 (Conif.)		42			TRUE	65.6
		if (pattern.equals("d(a.): d")){
			// Example: "1(Conif.): 42"
			//Series: ""
			series = "";
			//Volume: "1(Conif.)"
			volume = collation.split(":")[0];
			//Issue: ""
			issue = "";
			//Page: "42"
			page = splitCollation(collation)[2];
			//Tab/Fig/No: ""
			tab_or_fig_or_no = "";
			//Year: ""
			year = "";
			rule = "65.6";			
		}
		// , Aloac.: 41	, a.: d				41			true	65.8
		if (pattern.equals(", a.: d")){
			// Example: ", Aloac.: 41"
			//Series: ""
			series = "";
			//Volume: ""
			volume = "";
			//Issue: ""
			issue = "";
			//Page: "41"
			page = splitCollation(collation)[2];
			//Tab/Fig/No: ""
			tab_or_fig_or_no = "";
			//Year: ""
			year = "";
			rule = "65.8";			
		}
		// 65.9 - see rule 3
		
		if (pattern.equals(": a. d") && splitCollation(collation)[1].equals("t")){
			//Example: : t. 10
			//Series:
			series="";
			//Volume:
			volume="";
			//Issue:
			issue="";
			//Page:
			page="";
			//Tab/Fig/Pl/Nos: t. 10
			tab_or_fig_or_no="t. " + splitCollation(collation)[2];
			//Year:
			year="";
			//Rule: 67
			rule="67";
		}
		if ((pattern.equals("d: a. d") || pattern.equals("d: a. yyyy") )&& splitCollation(collation)[1].equals("t")){
			//Example: 13: t. 1291
			//Series:
			series="";
			//Volume: 13
			volume=splitCollation(collation)[0];
			//Issue:
			issue="";
			//Page:
			page="";
			//Tab/Fig/Pl/Nos: t. 1291
			tab_or_fig_or_no="t. " + splitCollation(collation)[2];
			//Year:
			year="";
			//Rule: 7
			rule="7";
		}
		if (pattern.equals("d(a. d): d")){
			//Example:  19(Suppl. 1): 131
			//Series:
			series="";
			//Volume: 19(Suppl. 1)
			volume=collation.split(":")[0];
			//Issue:
			issue="";
			//Page: 131
			page=splitCollation(collation)[3];
			//Tab/Fig/Pl/Nos:
			tab_or_fig_or_no="";
			//Year:
			year="";
			//Rule: 65.13
			rule="65.13";
		}
		if (pattern.equals("d(d, a.): d")){
			//Example: 118(1291, Suppl.): 61
			//Series:
			series="";
			//Volume: 118
			volume=splitCollation(collation)[0];
			//Issue: 1291, Suppl.
			issue=splitCollation(collation)[1] + ", " + splitCollation(collation)[2] + ".";
			//Page: 61
			page=splitCollation(collation)[3];
			//Tab/Fig/Pl/Nos:
			tab_or_fig_or_no="";
			//Year:
			year="";
			//Rule: 65.14
			rule="65.14";
		}
		if (pattern.equals("d: d, d")){
			//Example: 2: 128, 331
			//Series:
			series="";
			//Volume: 2
			volume=splitCollation(collation)[0];
			//Issue:
			issue="";
			//Page: 128, 331
			page=splitCollation(collation)[1] + ", " + splitCollation(collation)[2];
			//Tab/Fig/Pl/Nos:
			tab_or_fig_or_no="";
			//Year:
			year="";
			//Rule: 65.15
			rule="65.15";
		}
		if (pattern.equals("d(d) a: d") && splitCollation(collation)[2].equals("cppo")){
			//Example: 93(1097) cppo: 12
			//Series:
			series="";
			//Volume: 93
			volume=splitCollation(collation)[0];
			//Issue: 1097 cppo
			issue=splitCollation(collation)[1] + " " + splitCollation(collation)[2];
			//Page: 12
			page=splitCollation(collation)[3];
			//Tab/Fig/Pl/Nos:
			tab_or_fig_or_no="";
			//Year:
			year="";
			//Rule: 65.17
			rule="65.17";
		}
		if (pattern.equals(", d(d): d")){
			//Example: , 54(5): 88
			//Series:
			series="";
			//Volume: 54
			volume=splitCollation(collation)[1];
			//Issue: 5
			issue=splitCollation(collation)[2];
			//Page: 88
			page=splitCollation(collation)[3];
			//Tab/Fig/Pl/Nos:
			tab_or_fig_or_no="";
			//Year:
			year="";
			//Rule: 65.18
			rule="65.18";
		}
		if (pattern.equals(", r: d")){
			//Example: , C: 96
			//Series: C
			series=splitCollation(collation)[1];
			//Volume:
			volume="";
			//Issue:
			issue="";
			//Page: 96
			page=splitCollation(collation)[2];;
			//Tab/Fig/Pl/Nos:
			tab_or_fig_or_no="";
			//Year:
			year="";
			//Rule: 65.19
			rule="65.19";
		}
		if (pattern.equals("d(d a.): d")){
			//Example: 40(1 Anh.): 88
			//Series:
			series="";
			//Volume: 40
			volume=splitCollation(collation)[0];
			//Issue: 1 Anh.
			issue=splitCollation(collation)[1] + " " + splitCollation(collation)[2] + ".";
			//Page: 88
			page=splitCollation(collation)[3];
			//Tab/Fig/Pl/Nos:
			tab_or_fig_or_no="";
			//Year:
			year="";
			//Rule: 65.22
			rule="65.22";
		}
		if (pattern.equals(", a.a., a.a., d: d")){
			//Example: , n.s., f.m., 1: 107
			//Series: n.s., f.m.
			series=splitCollation(collation)[1]+"."+splitCollation(collation)[2]+"., "+splitCollation(collation)[3]+"."+splitCollation(collation)[4]+".";
			//Volume: 1
			volume=splitCollation(collation)[5];
			//Issue:
			issue="";
			//Page: 107
			page=splitCollation(collation)[6];
			//Tab/Fig/Pl/Nos:
			tab_or_fig_or_no="";
			//Year:
			year="";
			//Rule: 65.23
			rule="65.23";
		}
		if (pattern.equals(", a d: d")){
			//Example: , Texte 3: 1149
			//Series:
			series="";
			//Volume: Texte 3
			volume=splitCollation(collation)[1] + " " + splitCollation(collation)[2];
			//Issue:
			issue="";
			//Page: 1149
			page=splitCollation(collation)[3];
			//Tab/Fig/Pl/Nos:
			tab_or_fig_or_no="";
			//Year:
			year="";
			//Rule: 65.24
			rule="65.24";
		}
		if (pattern.equals("d: d, a. d")){
			//Example: 9: 388, t. 116
			//Series:
			series="";
			//Volume: 9
			volume=splitCollation(collation)[0];
			//Issue:
			issue="";
			//Page: 388
			page=splitCollation(collation)[1];
			//Tab/Fig/Pl/Nos: t. 116
			tab_or_fig_or_no=splitCollation(collation)[2]+". "+splitCollation(collation)[3];
			//Year:
			year="";
			//Rule: 65.25
			rule="65.25";
		}
		if (pattern.equals(": d, d")){
			//Example: : 487, 701
			//Series:
			series="";
			//Volume:
			volume="";
			//Issue:
			issue="";
			//Page: 487, 701
			page=splitCollation(collation)[1]+", "+splitCollation(collation)[2];
			//Tab/Fig/Pl/Nos:
			tab_or_fig_or_no="";
			//Year:
			year="";
			//Rule: 65.26
			rule="65.26";
		}
		if (pattern.equals("yyyy(a.): d")){
			//Example: 1857(App.): 4
			//Series:
			series="";
			//Volume: 1857(App.)
			volume=collation.split(": ")[0];
			//Issue:
			issue="";
			//Page: 4
			page=collation.split(": ")[1];
			//Tab/Fig/Pl/Nos:
			tab_or_fig_or_no="";
			//Year:
			year="";
			//Rule: 65.29
			rule="65.29";
		}
		if (pattern.equals(", a. d, d(d): d")){
			if (splitCollation(collation)[1].equals("ed")){
				//Example: , ed. 3, 1(11-12): 677
				//Series: ed. 3
				series=splitCollation(collation)[1]+". "+splitCollation(collation)[2];
				//Volume: 1
				volume=splitCollation(collation)[3];
				//Issue: 11-12
				issue=splitCollation(collation)[4];
				//Page: 677
				page=splitCollation(collation)[5];
				//Tab/Fig/Pl/Nos:
				tab_or_fig_or_no="";
				//Year:
				year="";
				//Rule: 65.30
				rule="65.30";
			}
			else{
				//Example: , ser. 3, 1(11-12): 677
				//Series: 3
				series=splitCollation(collation)[2];
				//Volume: 1
				volume=splitCollation(collation)[3];
				//Issue: 11-12
				issue=splitCollation(collation)[4];
				//Page: 677
				page=splitCollation(collation)[5];
				//Tab/Fig/Pl/Nos:
				tab_or_fig_or_no="";
				//Year:
				year="";
				//Rule: 65.30
				rule="65.30a";				
			}
		}
		if (pattern.equals("yyyy-d: d")){
			//Example: 2012-39: 35
			//Series:
			series="";
			//Volume: 2012-39
			volume=collation.split(": ")[0];
			//Issue:
			issue="";
			//Page: 35
			page=collation.split(": ")[1];
			//Tab/Fig/Pl/Nos:
			tab_or_fig_or_no="";
			//Year:
			year="";
			//Rule: 65.31
			rule="65.31";
		}
		if (pattern.equals(", a. a.: d") && (splitCollation(collation)[1]+". "+splitCollation(collation)[2]).equals("Spec. No")){
			//Example: , Spec. No.: 566
			//Series:
			series="";
			//Volume:
			volume="";
			//Issue:
			issue="";
			//Page:
			page="";
			//Tab/Fig/Pl/Nos: Spec. No. 566
			tab_or_fig_or_no="Spec. No. " + splitCollation(collation)[3];
			//Year:
			year="";
			//Rule: 65.32
			rule="65.32";
		}
		if (pattern.equals(", a. a.: d") && !(splitCollation(collation)[1]+". "+splitCollation(collation)[2]).equals("Spec. No")){
			//Example: , ed. rev.: 1439
			//Series: ed. rev.
			series=splitCollation(collation)[1]+". "+splitCollation(collation)[2]+".";
			//Volume:
			volume="";
			//Issue:
			issue="";
			//Page: 1439
			page=splitCollation(collation)[3];
			//Tab/Fig/Pl/Nos:
			tab_or_fig_or_no="";
			//Year:
			year="";
			//Rule: 65.33
			rule="65.33";
		}
		if (pattern.equals(", r, d r a d: d")){
			//Example: , IV, 50 II B 21: 242
			//Series: 4
			series=splitCollation(collation)[1];
			//Volume: 50 II B 21
			volume=splitCollation(collation)[2]+" "+splitCollation(collation)[3]+" "+splitCollation(collation)[4]+" "+splitCollation(collation)[5];
			//Issue:
			issue="";
			//Page: 242
			page=splitCollation(collation)[6];
			//Tab/Fig/Pl/Nos:
			tab_or_fig_or_no="";
			//Year:
			year="";
			//Rule: 65.34
			rule="65.34";
		}
		if (pattern.equals(", a. d(d): d")){
			//Example: , Suppl. 115(1276): 18
			//Series:
			series="";
			//Volume: Suppl. 115
			volume=splitCollation(collation)[1]+". "+splitCollation(collation)[2];
			//Issue: 1276
			issue=splitCollation(collation)[3];
			//Page: 18
			page=splitCollation(collation)[4];
			//Tab/Fig/Pl/Nos:
			tab_or_fig_or_no="";
			//Year:
			year="";
			//Rule: 65.35
			rule="65.35";
		}
		if (pattern.equals(", a.a., yyyy(d): d")){
			//Example: , n.s., 1883(2): 328
			//Series: n.s.
			series=splitCollation(collation)[1]+"."+splitCollation(collation)[2]+".";
			//Volume: 1883
			volume=splitCollation(collation)[3];
			//Issue: 2
			issue=splitCollation(collation)[4];
			//Page: 328
			page=splitCollation(collation)[5];
			//Tab/Fig/Pl/Nos:
			tab_or_fig_or_no="";
			//Year:
			year="";
			//Rule: 65.36
			rule="65.36";
		}
		if (pattern.equals(", r, d: a. d")){
			//Example: , V, 7: t. 44
			//Series: 5
			series=splitCollation(collation)[1];
			//Volume: 7
			volume=splitCollation(collation)[2];
			//Issue:
			issue="";
			//Page:
			page="";
			//Tab/Fig/Pl/Nos: t. 44
			tab_or_fig_or_no=splitCollation(collation)[3]+". "+splitCollation(collation)[4];
			//Year:
			year="";
			//Rule: 70
			rule="70";
		}
		if (pattern.equals("d(d,  a.): d")){
			//Example: 120(1299,  Suppl.): 49
			//Series:
			series="";
			//Volume: 120
			volume=splitCollation(collation)[0];
			//Issue: 1299, Suppl.
			issue=splitCollation(collation)[1]+", "+splitCollation(collation)[2]+".";
			//Page: 49
			page=splitCollation(collation)[3];
			//Tab/Fig/Pl/Nos:
			tab_or_fig_or_no="";
			//Year:
			year="";
			//Rule: 65.37
			rule="65.37";
		}
		if (pattern.equals("yyyy-yyyy: d")){
			//Example: 1927-1929: 9
			//Series:
			series="";
			//Volume: 1927-1929
			volume=collation.split(": ")[0];
			//Issue:
			issue="";
			//Page: 9
			page=collation.split(": ")[1];
			//Tab/Fig/Pl/Nos:
			tab_or_fig_or_no="";
			//Year:
			year="";
			//Rule: 65.38
			rule="65.38";
		}
		if (pattern.equals("d(d; d): d")){
			//Example: 24(3; 10): 23
			//Series:
			series="";
			//Volume: 24
			volume=splitCollation(collation)[0];
			//Issue: 3; 10
			issue=splitCollation(collation)[1]+"; "+splitCollation(collation)[2];
			//Page: 23
			page=splitCollation(collation)[3];
			//Tab/Fig/Pl/Nos:
			tab_or_fig_or_no="";
			//Year:
			year="";
			//Rule: 65.39
			rule="65.39";
		}
		if (pattern.equals("d(a. a.): d")){
			//Example: 9(Suppl. Bot.): 33
			//Series:
			series="";
			//Volume: 9(Suppl. Bot.)
			volume=collation.split(": ")[0];
			//Issue:
			issue="";
			//Page: 33
			page=collation.split(": ")[1];
			//Tab/Fig/Pl/Nos:
			tab_or_fig_or_no="";
			//Year:
			year="";
			//Rule: 65.40
			rule="65.40";
		}
		if (pattern.equals("d: a.\u00ba d")
				|| pattern.equals("d: a.\u00ba da")
				|| pattern.equals("d: a.\u00ba yyyy")
				||pattern.equals("yyyy: a.\u00ba d")
				||pattern.equals("yyyy: a.\u00ba da")
				||pattern.equals("yyyy: a.\u00ba yyyy")){
			//Example: 17: n.o 4
			//Series:
			series="";
			//Volume: 17
			volume=splitCollation(collation)[0];
			//Issue:
			issue="";
			//Page: 
			page="";
			//Tab/Fig/Pl/Nos: n.o 4
			tab_or_fig_or_no="n.\u00ba " + splitCollation(collation)[2];
			//Year:
			year="";
			//Rule: 73
			rule="73";
		}
		if (pattern.equals(": a.\u00ba d")
				|| pattern.equals(": a.\u00ba yyyy")
				|| pattern.equals(": a.\u00ba da")
				|| pattern.equals(": a.\u00ba yyyya")){
			//Example: ": n.o 4"
			//Series:
			series="";
			//Volume: 
			volume="";
			//Issue:
			issue="";
			//Page: 
			page="";
			//Tab/Fig/Pl/Nos: n.o 4
			tab_or_fig_or_no="n.\u00ba " + splitCollation(collation)[2];
			//Year:
			year="";
			//Rule: 73
			rule="73.1";
		}
		
		
		if (pattern.equals(", r, yyyy(d): d")){
			//Example: , III, 1893(1): 413
			//Series: 3
			series=splitCollation(collation)[1];
			//Volume: 1893
			volume=splitCollation(collation)[2];
			//Issue: 1
			issue=splitCollation(collation)[3];
			//Page: 413
			page=splitCollation(collation)[4];
			//Tab/Fig/Pl/Nos:
			tab_or_fig_or_no="";
			//Year:
			year="";
			//Rule: 65.41
			rule="65.41";
		}
		if (pattern.equals("d: a.a.")){
			//Example: 339: s.p.
			//Series:
			series="";
			//Volume: 339
			volume=splitCollation(collation)[0];
			//Issue:
			issue="";
			//Page: s.p.
			page=splitCollation(collation)[1]+"."+splitCollation(collation)[2]+".";
			//Tab/Fig/Pl/Nos:
			tab_or_fig_or_no="";
			//Year:
			year="";
			//Rule: 74
			rule="74";
		}
		if (pattern.equals("d: a. d, a. d")){
			//Example: 47: t. 2113, p. 5
			//Series:
			series="";
			//Volume: 47
			volume=splitCollation(collation)[0];
			//Issue:
			issue="";
			//Page: t. 2113, p. 5
			// CHECK!!
			page=collation.split(": ")[1];
			//Tab/Fig/Pl/Nos:
			tab_or_fig_or_no="";
			//Year:
			year="";
			//Rule: 75
			rule="75";
		}
		if (pattern.equals(", a. a. a.: d")){
			//Example: , Prodr. Fl. Cap.: 5
			//Series: Prodr. Fl. Cap.
			series=splitCollation(collation)[1]+". "+splitCollation(collation)[2]+". "+splitCollation(collation)[3]+".";
			//Volume:
			volume="";
			//Issue:
			issue="";
			//Page: 5
			page=splitCollation(collation)[4];
			//Tab/Fig/Pl/Nos:
			tab_or_fig_or_no="";
			//Year:
			year="";
			//Rule: 65.42
			rule="65.42";
		}
		if (pattern.equals("d(d: d): d")){
			//Example: 9(225: 1): 267
			//Series:
			series="";
			//Volume: 9
			volume=splitCollation(collation)[0];
			//Issue: 255:1
			issue=splitCollation(collation)[1]+":"+splitCollation(collation)[2];
			//Page: 267
			page=splitCollation(collation)[3];
			//Tab/Fig/Pl/Nos:
			tab_or_fig_or_no="";
			//Year:
			year="";
			//Rule: 65.44
			rule="65.44";
		}
		if (pattern.equals(", a: d")){
			//Example: , Atlas: 13
			//Series: Atlas
			series=splitCollation(collation)[1];
			//Volume:
			volume="";
			//Issue:
			issue="";
			//Page: 13
			page=splitCollation(collation)[2];
			//Tab/Fig/Pl/Nos:
			tab_or_fig_or_no="";
			//Year:
			year="";
			//Rule: 65.45
			rule="65.45";
		}
		if (pattern.equals("d(d): a. d")){
			//Example: 1(2): t. 20
			//Series:
			series="";
			//Volume: 1
			volume=splitCollation(collation)[0];
			//Issue: 2
			issue=splitCollation(collation)[1];
			//Page:
			page="";
			//Tab/Fig/Pl/Nos: t. 20
			tab_or_fig_or_no=splitCollation(collation)[2]+". "+splitCollation(collation)[3];
			//Year:
			year="";
			//Rule: 77
			rule="77";
		}
		if (pattern.equals(", a.a., yyyy: d")){
			//Example: , n.s., 1875: 162
			//Series: n.s.
			series=splitCollation(collation)[1]+"."+splitCollation(collation)[2]+".";
			//Volume: 1875
			volume=splitCollation(collation)[3];
			//Issue:
			issue="";
			//Page: 162
			page=splitCollation(collation)[4];
			//Tab/Fig/Pl/Nos:
			tab_or_fig_or_no="";
			//Year:
			year="";
			//Rule: 65.46
			rule="65.46";
		}
		if (pattern.equals(", r, yyyy: d")){
			//Example: , III, 1893: 629
			//Series: 3
			series=splitCollation(collation)[1];
			//Volume: 1893
			volume=splitCollation(collation)[2];
			//Issue:
			issue="";
			//Page: 629
			page=splitCollation(collation)[3];
			//Tab/Fig/Pl/Nos:
			tab_or_fig_or_no="";
			//Year:
			year="";
			//Rule: 65.47
			rule="65.47";
		}
		if (pattern.equals("d(a): d")){
			//Example: 15(Extra): 408
			//Series:
			series="";
			//Volume: 15(Extra)
			volume=collation.split(": ")[0];
			//Issue:
			issue="";
			//Page: 408
			page=collation.split(": ")[1];
			//Tab/Fig/Pl/Nos:
			tab_or_fig_or_no="";
			//Year:
			year="";
			//Rule: 65.50
			rule="65.50";
		}
		if (pattern.equals(", r, d r: d")){
			//Example: , IV, 243 II: 95
			//Series: 4
			series=splitCollation(collation)[1];
			//Volume: 243 II
			volume=splitCollation(collation)[2]+" "+splitCollation(collation)[3];
			//Issue:
			issue="";
			//Page: 95
			page=splitCollation(collation)[4];
			//Tab/Fig/Pl/Nos:
			tab_or_fig_or_no="";
			//Year:
			year="";
			//Rule: 65.51
			rule="65.51";
		}
		if (pattern.equals("d:")){
			//Example: 7:
			//Series:
			series="";
			//Volume: 7
			volume=splitCollation(collation)[0];
			//Issue:
			issue="";
			//Page:
			page="";
			//Tab/Fig/Pl/Nos:
			tab_or_fig_or_no="";
			//Year:
			year="";
			//Rule: 65.53
			rule="65.53";
		}
		
		String [] values = {series, volume, issue, page, tab_or_fig_or_no, year, rule};
		return values;
	}
	
	public static void main(String[] args) {
		// Tab separated file of id and collation
		String inputfile = args[0];
		String outputfile = args[1];

		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputfile),"utf8"));
				FileWriter fw = new FileWriter(outputfile);
				BufferedWriter bw = new BufferedWriter(fw);) {
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