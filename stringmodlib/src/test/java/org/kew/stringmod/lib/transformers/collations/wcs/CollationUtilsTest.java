package org.kew.stringmod.lib.transformers.collations.wcs;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kew.stringmod.lib.transformers.collations.wcs.CollationUtils;

@RunWith(Parameterized.class)
public class CollationUtilsTest {

	   private String collation;
	   private String  expectedSer;
	   private String  expectedVol;
	   private String  expectedIssue;
	   private String  expectedPage;
	   private String  expectedTabOrFig;
	   private String  expectedYear;
	   private String  expectedRule;
	   
	   // Each parameter should be placed as an argument here
	   // Every time runner triggers, it will pass the arguments
	   // from parameters we defined in collations() method
	   public CollationUtilsTest(String collation
			   , String expectedSer
			   , String expectedVol
			   , String expectedIssue
			   , String expectedPage
			   , String expectedTabOrFig
			   , String expectedYear
			   , String expectedRule
			   ) {
	      this.collation = collation;
	      this.expectedSer= expectedSer;
	      this.expectedVol = expectedVol;
	      this.expectedIssue=expectedIssue;
	      this.expectedPage=expectedPage;
	      this.expectedTabOrFig=expectedTabOrFig;
	      this.expectedYear=expectedYear;
	      this.expectedRule=expectedRule;
	   }

	   @Parameterized.Parameters
	   public static Collection<?> collations() {
	      return Arrays.asList(new Object[][] {
	    		  	{ "", "", "", "", "", "", "", ""}
	    		  	
	    		  , {": 7", "", "", "", "7", "", "", "2"}
	    		  , {": xvi", "", "", "", "xvi", "", "", "2"}
	    		  , {": 1901", "", "", "", "1901", "", "", "2"}
	    		  , {": 21A", "", "", "", "21A", "", "", "2"}

	    		  , {"4: 16", "", "4", "", "16", "", "", "1"}
	    		  , {"1987: 9", "", "1987", "", "9", "", "", "1"}
	    		  , {"1816: 199", "", "1816", "", "199", "", "", "1"}
	    		  , {"12A: 89", "", "12A", "", "89", "", "", "1"}

	    		  , {"1(11-12): 653", "", "1", "11-12", "653", "", "", "3"}
	    		  , {"15(1): 124", "", "15", "1", "124", "", "", "3"}
	    		  , {"1892(9): 425", "", "1892", "9", "425", "", "", "3"}
	    		  
	    		  
	    		  // From spreadsheet
	    		  , {", III, 102: 414","III","102","","414","","","65.1"}
	    		  ,{", ed. 3: 273","ed. 3","","","273","","","65.2"}
	    		  ,{", a.s., 3: 508","a.s.","3","","508","","","65.3"}
	    		  ,{", ed. 9, 1: 197","ed. 9","1","","197","","","65.4"}
	    		  ,{", II, 1(1): 28","II","1","1","28","","","65.5"}
	    		  ,{"1(Conif.): 42","","1(Conif.)","","42","","","65.6"}

	    		  ,{", Aloac.: 41","","","","41","","","65.8"}
	    		  ,{"1799(2): 226","","1799","2","226","","","3"}
	    		  ,{": t. 10","","","","","t. 10","","67"}
	    		  ,{", 1: 1023","","1","","1023","","","1.1"}
	    		  ,{", IV, 147a: 12","IV","147a","","12","","","65.1"}
	    		  ,{"816-5: 1","","816-5","","1","","","1"}
	    		  
//	    		  ,{":n.� 9008","","","","","n.�9008","","68"}
	    		  ,{", n.s., 4(1): 30","n.s.","4","1","30","","","65.12"}
	    		  ,{"13: t. 1291","","13","","","t. 1291","","7"}
	    		  ,{"19(Suppl. 1): 131","","19(Suppl. 1)","","131","","","65.13"}
	    		  ,{"118(1291, Suppl.): 61","","118","1291, Suppl.","61","","","65.14"}
	    		  ,{"2: 128, 331","","2","","128, 331","","","65.15"}
	    		  ,{"20A(5): 21","","20A","5","21","","","3"}
	    		  ,{"5: t. 1748","","5","","","t. 1748","","7"}
	    		  
	    		  ,{"93(1097) cppo: 12","","93","1097 cppo","12","","","65.17"}
	    		  ,{", 54(5): 88","","54","5","88","","","65.18"}
	    		  ,{", C: 96","C","","","96","","","65.19"}
	    		  ,{"4(3b): 214","","4","3b","214","","","3"}
	    		  ,{"28B: 36","","28B","","36","","","1"}
	    		  ,{"40(1 Anh.): 88","","40","1 Anh.","88","","","65.22"}
	    		  ,{": lxxiv","","","","lxxiv","","","2"}
	    		  ,{", n.s., f.m., 1: 107","n.s., f.m.","1","","107","","","65.23"}
	    		  ,{", Texte 3: 1149","","Texte 3","","1149","","","65.24"}
	    		  ,{"9: 388, t. 116","","9","","388","t. 116","","65.25"}
	    		  ,{": 487, 701","","","","487, 701","","","65.26"}
	    		  ,{", IV, 276c: 960","IV","276c","","960","","","65.1"}
	    		  
	    		  ,{", 15a: 620","","15a","","620","","","1.1"}
	    		  ,{"1857(App.): 4","","1857(App.)","","4","","","65.29"}
	    		  ,{", ed. 3, 1(11-12): 677","ed. 3","1","11-12","677","","","65.30"}
	    		  ,{"2012-39: 35","","2012-39","","35","","","65.31"}
	    		  ,{", Spec. No.: 566","","","","","Spec. No. 566","","65.32"}
	    		  ,{", ed. rev.: 1439","ed. rev.","","","1439","","","65.33"}
	    		  ,{", IV, 50 II B 21: 242","IV","50 II B 21","","242","","","65.34"}
	    		  ,{", Suppl. 115(1276): 18","","Suppl. 115","1276","18","","","65.35"}
	    		  ,{", n.s., 1883(2): 328","n.s.","1883","2","328","","","65.36"}
	    		  ,{", V, 7: t. 44","V","7","","","t. 44","","70"}
	    		  ,{"120(1299,  Suppl.): 49","","120","1299, Suppl.","49","","","65.37"}
	    		  ,{"1927-1929: 9","","1927-1929","","9","","","65.38"}
	    		  ,{"20: xxxv","","20","","xxxv","","","1"}
	    		  ,{"12(3-4): 79","","12","3-4","79","","","3"}
	    		  ,{"24(3; 10): 23","","24","3; 10","23","","","65.39"}
	    		  ,{"9(Suppl. Bot.): 33","","9(Suppl. Bot.)","","33","","","65.40"}
	    		  
	    		  ,{"17: n.\u00ba 4","","17","","","n.\u00ba 4","","73"}
	    		  ,{": n.\u00ba 4","","","","","n.\u00ba 4","","73.1"}
	    		  
	    		  ,{", III, 1893(1): 413","III","1893","1","413","","","65.41"}
	    		  ,{"339: s.p.","","339","","s.p.","","","74"}
	    		  ,{"47: t. 2113, p. 5","","47","","t. 2113, p. 5","","","75"}
	    		  ,{", Prodr. Fl. Cap.: 5","Prodr. Fl. Cap.","","","5","","","65.42"}
	    		  ,{", 4(13A): 124","","4","13A","124","","","65.18"}
	    		  ,{"1927: n.\u00ba 6186","","1927","","","n.\u00ba 6186","","73"}
	    		  ,{"9(225: 1): 267","","9","225:1","267","","","65.44"}
	    		  ,{", Atlas: 13","Atlas","","","13","","","65.45"}
	    		  ,{"1(2): t. 20","","1","2","","t. 20","","77"}
	    		  ,{", n.s., 1875: 162","n.s.","1875","","162","","","65.46"}
	    		  
	    		  ,{", III, 1893: 629","III","1893","","629","","","65.47"}
	    		  ,{", n.f., 13-14: 57","n.f.","13-14","","57","","","65.3"}
	    		  ,{", ed. 3, 1(14): 854","ed. 3","1","14","854","","","65.30"}
	    		  ,{"15(Extra): 408","","15(Extra)","","408","","","65.50"}
	    		  ,{", IV, 243 II: 95","IV","243 II","","95","","","65.51"}
	    		  ,{"6: 461b","","6","","461b","","","1"}
	    		  ,{"7:","","7","","","","","65.53"}
	    		  
	    		  ,{", ser. 4, 3: 534","4","3","","534","","","65.4a"}
	    		  ,{", s\u00E9r. 4, 3: 534","4","3","","534","","","65.4a"}
	    		  ,{", ser. 4, 3(1): 534","4","3","1","534","","","65.30a"}
	    		  ,{", s\u00E9r. 4, 3(1): 534","4","3","1","534","","","65.30a"}
	    		  
	      });
	   }

	   @Test
	   public void testCollationParsing() {
	      System.out.println("Collation is : " + collation 
	    		  + ", series: " + expectedSer 
	    		  + ", volume: " + expectedVol
	    		  + ", issue: " + expectedIssue
	    		  + ", page: " + expectedPage
	    		  + ", tab_or_fig: " + expectedTabOrFig
	    		  + ", year: " + expectedYear
	    		  + ", rule: " + expectedRule
	    		  );
	      String[] parsedCollation =  CollationUtils.parseCollation(collation);
	      assertEquals(expectedSer, parsedCollation[CollationUtils.SERIES_INDEX]);
	      assertEquals(expectedVol, parsedCollation[CollationUtils.VOL_INDEX]);
	      assertEquals(expectedIssue, parsedCollation[CollationUtils.ISSUE_INDEX]);
	      assertEquals(expectedPage, parsedCollation[CollationUtils.PAGE_INDEX]);
	      assertEquals(expectedTabOrFig, parsedCollation[CollationUtils.TAB_OR_FIG_INDEX]);
	      assertEquals(expectedYear, parsedCollation[CollationUtils.YEAR_INDEX]);
	      assertEquals(expectedRule, parsedCollation[CollationUtils.RULE_INDEX]);
	   }
	}