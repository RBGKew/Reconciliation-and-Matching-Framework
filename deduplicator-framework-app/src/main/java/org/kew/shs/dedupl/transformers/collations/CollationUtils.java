package org.kew.shs.dedupl.transformers.collations;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;

import org.kew.shs.dedupl.transformers.RomanNumeralTransformer;

public class CollationUtils {

	public static int VOL_INDEX = 0;
	public static int ISSUE_INDEX = 1;
	public static int PAGE_INDEX = 2;
	public static int YEAR1_INDEX = 3;
	public static int YEAR2_INDEX = 4;
		
	public static String assessCollationStructure(String collation){
		String c_structure = null;
		if (collation != null){
			c_structure  = collation.toLowerCase();
			c_structure=c_structure.replaceAll("1[7-9][0-9][0-9]","YYYY");
			c_structure=c_structure.replaceAll("20[0-1][0-9]","YYYY");
			c_structure=c_structure.replaceAll("[0-9]+","D");
			c_structure=c_structure.replaceAll("[ivxlc]+","R");
			c_structure=c_structure.replaceAll("[a-z]+","A");
			c_structure=c_structure.replaceAll("RA","A");
			c_structure=c_structure.replaceAll("AR","A");
			c_structure=c_structure.replaceAll("A+","A");
			c_structure=c_structure.toLowerCase();
		}
		return c_structure;
	}

	private static String convertRoman(String r){
		return new RomanNumeralTransformer().transform(r);
	}
	
	public static boolean parsableCollation(String collation){
		return !Arrays.toString(parseCollation(collation)).equals("[, , , , ]");
	}
	public static String[] parseCollation(String collation){
		boolean parsed = false;
		String c_patt = assessCollationStructure(collation);
		String vol = "";
		String issue = "";
		String page = "";
		String year1 = "";
		String year2 = "";
		// extract components (vol,issue,page,year1,year2) depending on collation structure
		if (c_patt.equals("d")){
			page=collation;
		}
		if (c_patt.equals("d.")){
			page=collation.substring(0,collation.length()-1);
		}
		if (c_patt.equals("d: d")){
			String[] c = collation.split("[^0-9]+");
			vol=c[0];
			page=c[1];
			parsed=true;
		}
		if (c_patt.equals("d(d): d")){
			String[] c= collation.split("[^0-9]+");
			vol=c[0];
			issue=c[1];
			page=c[2];
			parsed=true;
		}
		if (c_patt.equals("d(d): d (yyyy)")){
			String[] c = collation.split("[^0-9]+");
			vol=c[0];
			issue=c[1];
			page=c[2];
			year1=c[3];
			parsed=true;
		}
		if (c_patt.equals("r. d.")){
			String[] c = collation.split("\\.");
			vol=convertRoman(c[0]);
			page=c[1].replace(" ","");
			parsed=true;
		}
		if (c_patt.equals("r. d (yyyy)")){
			// CHECK
			vol=collation.split("\\.")[0];
			vol=convertRoman(vol);
			page=collation.split(" ")[1];
			year1=collation.split("\\(")[1].split("\\)")[0];
			parsed=true;
		}
		if (c_patt.equals("d(d): d (yyyy):")){
			String[] c=collation.split("[^0-9]+");
			vol=c[0];
			issue=c[1];
			page=c[2];
			year1=c[3];
			parsed=true;
		}
		if (c_patt.equals("d, a. d: d")){
			String[] c=collation.split("[^0-9]+");
			vol=c[0];
			issue=c[1];
			page=c[2];
			parsed=true;
		}
		if (c_patt.equals("d(d-d): d")){
		    String[] c=collation.split("[^0-9]+");
		    vol=c[0];
		    issue=c[1];
		    page=c[3];
		    parsed=true;
		}
		if (c_patt.equals("r. d (yyyy).")){
			year1=collation.split("\\(")[1].split("\\)")[0];
			vol=collation.split("\\.")[0];
			vol=convertRoman(vol);
			page=collation.split(" ")[1];
			parsed=true;
		}
		if (c_patt.equals("d[d]: d")){
			String[] c=collation.split("[^0-9]+");
			vol=c[0];
			issue=c[1];
			page=c[2];
			parsed=true;
		}
		if (c_patt.equals("d(d, a. d): d")){
			String[] c=collation.split("[^0-9]+");
			vol=c[0];
			page=c[3];
			parsed=true;
		}
		if (c_patt.equals("yyyy, r. d.")){
			// CHECK
			year1=collation.split(",")[0];
			vol=collation.split(" ")[1].split("\\.")[0];
			vol=convertRoman(vol);
			page=collation.split(" ")[2].split("\\.")[0];
			parsed=true;
		}
		if (c_patt.equals("yyyy, r. d")){
		    // CHECK
			year1=collation.split(",")[0];
			vol=collation.split(" ")[1].split("\\.")[0];
			vol=convertRoman(vol);
			page=collation.split(" ")[2].split("\\.")[0];
			parsed=true;
		  }
		if (c_patt.equals("(yyyy), r. d.") || c_patt.equals("(yyyy) r. d.") ){
			// CHECK
			year1=collation.substring(1,4);
			vol=collation.split(" ")[1].split("\\.")[0];
			vol=convertRoman(vol);
			page=collation.split(" ")[2].split("\\.")[0];
			parsed=true;
		}
		if (c_patt.equals("(yyyy), r. d") || c_patt.equals("(yyyy) r. d")){
		    // CHECK
			year1=collation.substring(1,4);
			vol=collation.split(" ")[1].split("\\.")[0];
			vol=convertRoman(vol);
			page=collation.split(" ")[2].split("\\.")[0];
			parsed=true;
		  }
		if (c_patt.equals("d(d): d (yyyy).")){
			String[] c=collation.split("[^0-9]+");
			vol=c[0];
			issue=c[1];
			page=c[2];
			year1=c[3];
			parsed=true;
		}
		// check
		if (c_patt.equals("yyyy, d.")){
		    String[] c=collation.split("[^0-9]+");
		    year1=c[0];
		    page=c[1];
		    parsed=true;
		}
		if (c_patt.equals("r. d")){
			// CHECK
			vol=collation.split("\\.")[0];
			vol=convertRoman(vol);
			page=collation.split(" ")[1];
			parsed=true;
		}
		if (c_patt.equals("d: d (-d)")){
			String[] c=collation.split("[^0-9]+");
			vol=c[0];
			page=c[1];
			parsed=true;
		}
		if (c_patt.equals("yyyy, d (yyyy).")){
			String[] c=collation.split("[^0-9]+");
			page=c[1];
			year1=c[0];
			year2=c[2];
			parsed=true;
		}
		if (c_patt.equals("d. d. yyyy")){
			String[] c=collation.split("[^0-9]+");
			vol=c[0];
			page=c[1];
			year1=c[2];
			parsed=true;
		}
		if (c_patt.equals("d(d-d, a. d): d")){
		    String[] c=collation.split("[^0-9]+");
		    vol=c[0];
		    issue=c[1];
		    page=c[4];
		    parsed=true;
		  }
		if (c_patt.equals("d: d. yyyy.")){
		    String[] c=collation.split("[^0-9]+");
		    vol=c[0];
		    page=c[1];
		    year1=c[2];
		    parsed=true;
		}
		  if (c_patt.equals("d: d, a")){
		    String[] c=collation.split("[^0-9]+");
		    vol=c[0];
		    page=c[1];
		    parsed=true;
		  }
		  //  #GOT THIS FAR
		  if (c_patt.equals("d: d, a. d")){
		    String[] c=collation.split("[^0-9]+");
		    vol=c[0];
		    page=c[1];
		    parsed=true;
		  }
		  if (c_patt.equals("r. (yyyy) d.")){
		    vol=collation.split("\\.")[0];
		    vol=convertRoman(vol);
		    year1=collation.split(" ")[1].replace("\\(","").replace("\\)","");
		    page=collation.split(" ")[2].replace("\\.","");
		    parsed=true;
		  }
		  if (c_patt.equals("yyyy, d (yyyy)")){
		    String[] c=collation.split("[^0-9]+");
		    year1=c[0];
		    page=c[1];
		    year2=c[2];
		    parsed=true;
		  }
		  if (c_patt.equals("yyyy, d")){
		    String[] c=collation.split("[^0-9]+");
		    year1=c[0];
		    page=c[1];
		    parsed=true;
		  }
		  if (c_patt.equals("d: d (-d), a")){
		    String[] c=collation.split("[^0-9]+");
		    vol=c[0];
		    page=c[1];
		    parsed=true;
		  }
		  if (c_patt.equals("d: d, d")){
		    String[] c=collation.split("[^0-9]+");
		    vol=c[0];
		    page=c[1];
		    parsed=true;
		  }
		  if (c_patt.equals("d(d): d (yyyy), a. a.:")){
		    String[] c=collation.split("[^0-9]+");
		    vol=c[0];
		    issue=c[1];
		    page=c[2];
		    year1=c[3];
		    parsed=true;
		  }
		  if (c_patt.equals("d(d): d (-d; a. d)")){
		    String[] c=collation.split("[^0-9]+");
		    vol=c[0];
		    issue=c[1];
		    page=c[2];
		    parsed=true;
		  }
		  if (c_patt.equals("d: d (yyyy-d)")){
		    String[] c=collation.split("[^0-9]+");
		    vol=c[0];
		    page=c[1];
		    year1=c[1];
		    if(year2.length()==1){
		    	year2=new StringBuffer(year1.substring(0, 2)).append(year2).toString();
		    }
		    parsed=true;
		  }
		  if (c_patt.equals("r. (yyyy) d")){
		    vol=collation.split("\\.")[0];
		    vol=convertRoman(vol);
		    year1=collation.split("\\(")[1].split("\\)")[0];
		    page=collation.split(" ")[2];
		    parsed=true;
		  }
		  if (c_patt.equals("d, a. d: d, a. d, a. d")){
		    String[] c=collation.split("[^0-9]+");
		    vol=c[0];
		    issue=c[1];
		    page=c[2];
		    parsed=true;
		  }
		  if (c_patt.equals("d(d): d (-d)")){
		    String[] c=collation.split("[^0-9]+");
		    vol=c[0];
		    issue=c[1];
		    page=c[2];
		    parsed=true;
		  }
		  if (c_patt.equals("r. d (yyyy), a a.")){
		    vol=collation.split("\\.")[0];
		    vol=convertRoman(vol);
		    year1=collation.split("\\(")[1].split("\\)")[0];
		    page=collation.split(" ")[1];
		    parsed=true;
		  }
		  if (c_patt.equals("r. a. d, d")){
		    vol=collation.split("\\.")[0];
		    vol=convertRoman(vol);
		    issue=collation.split(" ")[2].replace(",","");
		    page=collation.split(" ")[3];
		    parsed=true;
		  }
		  if (c_patt.equals("d (d): d (yyyy)")){
		    String[] c=collation.split("[^0-9]+");
		    vol=c[0];
		    issue=c[1];
		    page=c[2];
		    year1=c[3];
		    parsed=true;
		  }
		  if (c_patt.equals("d. d. yyyy.")){
		    String[] c=collation.split("[^0-9]+");
		    vol=c[0];
		    page=c[1];
		    year1=c[2];
		    parsed=true;
		  }
		  if (c_patt.equals("d: d, a a.")){
		    String[] c=collation.split("[^0-9]+");
		    vol=c[0];
		    page=c[1];
		    parsed=true;
		  }
		  if (c_patt.equals("d(a.): d")){
		    String[] c=collation.split("[^0-9]+");
		    vol=c[0];
		    page=c[1];
		    parsed=true;
		  }
		  if (c_patt.equals("d (yyyy).")){
		    String[] c=collation.split("[^0-9]+");
		    page=c[0];
		    year1=c[1];
		    parsed=true;
		  }
		  if (c_patt.equals("d (d): d (yyyy).")){
		    String[] c=collation.split("[^0-9]+");
		    vol=c[0];
		    issue=c[1];
		    page=c[2];
		    year1=c[3];
		    parsed=true;
		  }
		  if (c_patt.equals("d(d): d, a. d")){
		    String[] c=collation.split("[^0-9]+");
		    vol=c[0];
		    issue=c[1];
		    page=c[2];
		    parsed=true;
		  }
		  if (c_patt.equals("yyyy: d")){
		    String[] c=collation.split("[^0-9]+");
		    year1=c[0];
		    page=c[1];
		    parsed=true;
		  }
		  if (c_patt.equals("d (yyyy)")){
		    String[] c=collation.split("[^0-9]+");
		    page=c[0];
		    year1=c[1];
		    parsed=true;
		  }
		  if (c_patt.equals("d, a. d: d, a. d")){
		    String[] c=collation.split("[^0-9]+");
		    vol=c[0];
		    issue=c[1];
		    page=c[2];
		    parsed=true;
		  }
		  if (c_patt.equals("(yyyy) d.")){
		    String[] c=collation.replace("(","").replace(")","").replace("\\.","").split(" ");
		    year1=c[0];
		    page=c[1];
		    parsed=true;
		  }
		  if (c_patt.equals("(yyyy) d")){
			    String[] c=collation.replace("(","").replace(")","").split(" ");
			    year1=c[0];
			    page=c[1];
			    parsed=true;
		  }		  
		  if (c_patt.equals("d(d): d, a a.")){
		    String[] c=collation.split("[^0-9]+");
		    vol=c[0];
		    issue=c[1];
		    page=c[2];
		    parsed=true;
		  }
		  if (c_patt.equals("a. d, d: d" )){
		    if (collation.toLowerCase().startsWith("ser. ")){
		      String[] c=collation.toLowerCase().replaceFirst("ser. ","").split("[^0-9]+");
		      vol="ser." + c[0] + " v." + c[1];
		      page=c[2];
		      parsed=true;
		    }
		  }
		  if (c_patt.equals("a. d, d(d): d")){
		    if (collation.toLowerCase().startsWith("ser. ")){
		      String[] c=collation.toLowerCase().replaceFirst("ser. ","").split("[^0-9]+");
		      vol="ser." + c[0] + " v." + c[1];
		      issue=c[2];
		      page=c[3];
		      parsed=true;
		    }
		  }
		  /*
		  if (c_patt=="a. r. r. d."):
		    print value + " : " + c_patt
		    if (value.lower().startswith('ser. ')):
		      print value + " : " + c_patt
		      c=value[5:].replace('.','').lower().split(' ')
		      print c
		      #vol="ser." + convertRoman(c[0]) + " v." + convertRoman(c[1])
		      print "converting " + c[0] + "..."
		      print ord(c[0][0]
		      c0=convertRoman(c[0])
		      print "converted " + c[0] + " to " + c0
		      print "converting " + c[1] + "..."
		      c1=convertRoman(c[1])
		      print "converted " + c[1] + " to " + c1
		      vol="ser." + c0 + " v." + c1
		      page=c[2]
		      print vol + " " + page
		   */
		  if (!parsed){
		    // do some final catches - LOOK AT THIS
		    if (c_patt.startsWith("d: d")){
		      String[] c=collation.split("[^a-z0-9]+");
		      vol=c[0];
		      page=c[1];
		    }
		    if (c_patt.startsWith("d(d): d")){
		      String[] c=collation.split("[^a-z0-9]+");
		      vol=c[0];
		      issue=c[1];
		      page=c[2];
		    }
		    if (c_patt.startsWith("r(d): d")){
		      String[] c=collation.split("[^a-z0-9]+");
		      vol=c[0];
		      vol=convertRoman(vol);
		      issue=c[1];
		      page=c[2];
		    }
		    if (c_patt.startsWith("r: d")){
		      String[] c=collation.split("[^a-z0-9]+");
		      vol=c[0];
		      vol=convertRoman(vol);
		      page=c[1];
		    }
		    if (c_patt.startsWith("r. d")){
		      String[] c=collation.split("[^a-z0-9]+");
		      vol=c[0];
		      vol=convertRoman(vol);
		      page=c[1];
		    }
		    if (c_patt.startsWith("d, ")){
			      String[] c=collation.split(",");
			      page=c[0];
		    }
		    if (c_patt.startsWith("d (yyyy)")){
		    	  page=collation.split(" ")[0];
		    	  year1=collation.split("\\(")[1].split("\\)")[0];
		    }
		  }
		  // Put the components into a list for return
		  String[] c_parsed={vol,issue,page,year1,year2};
		  return c_parsed;
	}
	
	public static void main(String[] args) {
		// Tab separated file of id and collation
		String inputfile = "C:/nicky/rubbish/ipni-collations.tab";
		String outputfile = "C:/nicky/rubbish/ipni-collations-parsed.tab";
		
		try {
	    	int count = 0;
	    	BufferedReader br = new BufferedReader(new FileReader(inputfile));
	    	FileWriter fw = new FileWriter(outputfile);
	    	BufferedWriter bw = new BufferedWriter(fw);
			String line = null;
	        while ((line = br.readLine()) != null) {
	        	if ((count++ % 10000) == 0){
	        		System.out.println(count);
	        	}
	        	String[] elems = line.split("\t");
	        	String id = elems[0];
	        	String collation = elems[1];
	        	
	        	String structure = new CollationStructureTransformer().transform(collation);
	        	String vol = new VolExtractorTransformer().transform(collation);
	        	String issue = new IssueExtractorTransformer().transform(collation);
	        	String page = new PageExtractorTransformer().transform(collation);
	        	String year = new YearExtractorTransformer().transform(collation);
	        			
	        	bw.write(id 
	        			+ "\t" + collation
	        			+ "\t" + structure
	        			+ "\t" + vol
	        			+ "\t" + issue
	        			+ "\t" + page
	        			+ "\t" + year
	        			+ "\t" + CollationUtils.parsableCollation(collation)
	        			+ "\n");	        			
	        }
	        bw.flush();
	        bw.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

}