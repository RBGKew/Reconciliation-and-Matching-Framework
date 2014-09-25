package org.kew.rmf.matchers;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;

import org.kew.rmf.transformers.Transformer;

/**
 * This matcher tests for common author abbreviations in two authorship strings
 * It takes two input files - one is for a standard set of author abbreviations and the other is for any author misspellings-
 * that are specific to the data-set being compared. e.g variations of 'Linnaeus'. 
 * @author nb00kg
 *
 */
public class AuthorAbbreviationsMatcher extends AuthorCommonTokensMatcher{

	public static int COST = 1;
	
	private List<Transformer> transformers;
	
	public void setTransformers(List<Transformer> transformers){
		this.transformers = transformers;
	}
	

	@Override
	public int getCost() {
		return COST;
	}
	
	private static String IN_MARKER = " in ";
	private static String EX_MARKER = " ex ";
	private static String AMP_MARKER = " & ";
	private static String ET_MARKER = ".et ";
	private static String FORMA_MARKER = " forma ";
			
	
	private static final String FILE_DELIMETER = "%";
	
	private static HashMap<String, String> authorAbbreviations;
	private static HashMap<String, String> authorSpecialCases;
	
	
	AuthorAbbreviationsMatcher() {}
	
	AuthorAbbreviationsMatcher(FileInputStream abbreviationsFile, FileInputStream specialCasesFile) {
		
		authorAbbreviations = loadAbbreviationsList(abbreviationsFile,FILE_DELIMETER);
		authorSpecialCases =  loadAbbreviationsList(specialCasesFile,FILE_DELIMETER);
	}

	boolean storeAndReuseTransformations;

	public boolean matches_new(String s1, String s2) throws Exception {
		// First check for match w/out transform
		boolean matches = match_inner(s1, s2);
		if (!matches){
			String s1_transformed = s1;
			String s2_transformed = s2;
			for (Transformer t : transformers){
				// First attempt to match uses the results of this transformer standalone
				matches = match_inner(t.transform(s1),t.transform(s2));
				if (matches) break;
				if (storeAndReuseTransformations){
					// Apply transformer to results of previous run and store the results
					s1_transformed = t.transform(s1_transformed);
					s2_transformed = t.transform(s2_transformed);
					matches = match_inner(s1_transformed,s2_transformed);
				}
				if (matches) break;
			}
        }
        return matches;
	}
	

	public boolean match_inner(String s1, String s2) {
		boolean matches = false;
		matches = new ExactMatcher().matches(s1, s2);
		if (!matches){
			// TODO : do the common substring calc etc here
		}
		return matches;
	}

	@Override
	public boolean matches(String s1, String s2) {
		boolean matches = false;
		if (s1 == null && s2 == null)
			matches = true;
		else{
			try{
				
				//are strings equal
				matches = s1.equals(s2);
				if (!matches) {
					
						//clean the input Strings
						s1 = clean(s1);
						s2 = clean(s2);
						
						//correct any author misspellings specific to the dataset being compared. e.g. change 'Linn.;' to 'L.'
						s1 = translateMisspellings(s1);
						s2 = translateMisspellings(s2);
						
						
						//split author strings by space
						String[] str1 = s1.split(" ");
						String[] str2 = s2.split(" ");
						
						//check for any abbreviated author strings in each string array and translate them
						String [] transString1 = translateAbbreviation(str1);
						String [] transString2 = translateAbbreviation(str2);
						
						//merge author components back into a single string
						s1  = singleString(transString1);
						s2 =  singleString(transString2);
						
						//replace any full stops with spaces now abbreviations have been translated
						s1 = s1.replaceAll("\\.", " ");
						s2 = s2.replaceAll("\\.", " ");
						
						//split by space again to prevent unwanted author initials corrupting the comparison results     
						str1 = s1.split(" ");
						str2 = s2.split(" ");
						
						//compare the common tokens now
						matches = super.calculateTokensInCommon(str1, str2);
						
						if (!matches) {
							//compare the two arrays and see if any element from one appears as a substring in the other.
							//only substrings of 3 or more characters are compared
							matches = commonSubString(str1, str2);
							if (!matches)
								matches = commonSubString(str2, str1);	
						}
						
						if (!matches) {

							//Finally try the Ngram matcher on the two cleaned and translated author strings
							s1  = singleString(str1);
							s2 =  singleString(str2);

							NGramMatcher gram = new NGramMatcher();
							gram.setMinRatio(0.5);
							gram.setnGramLength(2);
							matches = gram.matches(s1,s2);
						}
				
				}
				
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return matches;
	}

	public String translateMisspellings(String str1) {
		
		String misspell = null;
		misspell = authorSpecialCases.get(str1);
		
		if (misspell == null)
			misspell = str1;
		
		return misspell;
	}

	public String[] translateAbbreviation(String[] str1) {
		
		String abbrev = null;
		String transl = null;
		String[] trans = str1;
		
		for (int x=0; x < trans.length; x++) {
			
			abbrev = trans[x];
			
			transl = authorSpecialCases.get(abbrev);
			if (transl != null) {
				abbrev = transl;
			}
			transl = authorAbbreviations.get(abbrev);
			if (transl != null) 
				trans[x] = transl;
		}
		
		return trans;
	}
	
	/*
    public boolean compareStandardAbbeviations(String s1, String s2) {
    	
    	boolean abbrevMatch = false;
    	
		//is one string a standard abbreviation.
		if (authorAbbreviations != null) {
			String abbrev = null; 
			if (s1.matches("\\S*\\.")) {
			      abbrev = authorAbbreviations.get(s1);
			  if (abbrev != null) {
				  abbrevMatch = s2.equals(abbrev);
				  //clean the latin characters
				  
			  }
			}
			else if (s2.matches("\\S*\\.")) {
				    abbrev = authorAbbreviations.get(s2);
				if (abbrev != null) {
					abbrevMatch = s1.equals(abbrev);
				}
			}	
		}
    	
    	return abbrevMatch;
    }
    */
	
    public String clean(String strIn) {
    	//replace any diacritical mark characters with 'a-z' characters and replace any 'in', 'ex', '&', 'forma' with space.
    	//replace '.et ' with '. '
    	String str1 = strIn;
    	try {
    		
    		str1 = Normalizer.normalize(strIn, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
    		str1 = cleanAmp(cleanEx(cleanIn(cleanEt(cleanForma(str1)))));
    	}	
    	
    	catch (Exception e) {
    		
    	}
    	
    	return str1;
    }
    
	private String cleanEx(String s){
		String cleaned = s;
		if (s != null){
			if (s.indexOf(EX_MARKER) != -1){
				cleaned = s.replaceAll(EX_MARKER, " ");
			}
		}
		return cleaned;
	}
	
	private String cleanIn(String s){
		String cleaned = s;
		if (s != null){
			if (s.indexOf(IN_MARKER) != -1){
				cleaned = s.replaceAll(IN_MARKER, " ");
			}
		}
		return cleaned;		
	}
	
	private String cleanAmp(String s){
		String cleaned = s;
		if (s != null){
			if (s.indexOf(AMP_MARKER) != -1){
				cleaned = s.replaceAll(AMP_MARKER, " ");
			}
		}
		return cleaned;
	}
	
	private String cleanEt(String s){
		String cleaned = s;
		if (s != null){
			if (s.indexOf(ET_MARKER) != -1){
				cleaned = s.replaceAll(ET_MARKER, "\\. ");
			}
		}
		return cleaned;
	}
	
	private String cleanForma(String s){
		String cleaned = s;
		if (s != null){
			if (s.indexOf(FORMA_MARKER) != -1){
				cleaned = s.replaceAll(FORMA_MARKER, " ");
			}
		}
		return cleaned;
	}
    
    /* 
    public boolean matchAbbreviationPattern(String s1,String s2) {
    	
    	boolean abbrevPatternMatch = false;
    	
    	String[] splitS1 = null;
    	String[] splitS2 = null;

    	try {
    		splitS1 = s1.toLowerCase().replaceAll("\\.", "").split("\\s");
    		splitS2 = s2.toLowerCase().replaceAll("\\.", "").split("\\s");
    		
    		if ((splitS1[0].startsWith(splitS2[0])) || (splitS2[0].startsWith(splitS1[0]))) { 
    			
    			abbrevPatternMatch = true;
    		}
    		
    	}	
        catch (Exception e) {
        	e.printStackTrace();
        }
    	
    	return abbrevPatternMatch;
    	
    }
	*/
	
    public String singleString(String[] strArr) {
    	
    	StringBuffer result = new StringBuffer();
    	for (int i = 0; i < strArr.length; i++) {
    		result.append(strArr[i]).append(" ");
    	}
    	
    	return result.toString().trim();
    	
    	    	
    }
    
    public boolean commonSubString(String[] strArr1, String[] strArr2) {
    	//check if any element (3 characters or greater) form one array is found as a substring in the other 
    	String findString = null;
    	boolean StringMatch = false;
    	for (int i = 0; i < strArr1.length; i++) {
    		
            findString = strArr1[i].replaceAll("\\.", "");
            if (findString.length() < 3)
              continue;	
            		
    		for (int x = 0; x < strArr2.length; x++) {
                   			
    			if (strArr2[x].indexOf(findString) > -1) {
    				StringMatch = true;
    				break;
    			}
     		}
    		
    		if (StringMatch)
    		  break;	
    	}
    	
    	return StringMatch;
    	    	
    }
    
    
	public HashMap<String, String> loadAbbreviationsList(FileInputStream abbreviationsFile, String fileSeparator) {
		//load the supplied author abbreviations file into a hash map.
		//as the file is loaded all diacritical marks are replaced with a-z characters and strings are stored as lower case 
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			
			InputStreamReader streamReader = new InputStreamReader(abbreviationsFile, "UTF-8");
			BufferedReader br = new BufferedReader(streamReader);
			String line = null;
			String sKey;
			String sValue;
			while ((line = br.readLine()) != null) {
				String[] elem = line.split(fileSeparator);
				sKey = elem[0];
				sValue = elem[1];
				
				//System.out.println(sKey + "----" + sValue);

				sKey = Normalizer.normalize(sKey, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
				sValue = Normalizer.normalize(sValue, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
				
				//System.out.println(sKey + "----" + sValue); 
				
			    map.put(sKey.toLowerCase(), sValue.toLowerCase());
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return map;
		
	}

	@Override
	public boolean isExact() {
		return false;
	}

	@Override
	public String getExecutionReport() {
		return null;
	}

	/*
	 * TODO: This should be moved into a unit test.
	 */
	public static void main(String[] args) {
		//test this matcher using a test file of author pairs to match. File layout example = 'authorID%author1%author2'
		//The output file shows each author pair after they are cleaned, any abbreviation translated and finally if they match.  
		
		try {
		FileInputStream authorAbbs = new FileInputStream("T:/Development/Medicinal Plants/name-matching/JavaApp/data/ipni-author-abbreviations.txt");
		FileInputStream specialAbbs = new FileInputStream("T:/Development/Medicinal Plants/name-matching/JavaApp/data/uwe-author-misspellings.txt");
		AuthorAbbreviationsMatcher n = new AuthorAbbreviationsMatcher(authorAbbs,specialAbbs);
		
		FileOutputStream out = new FileOutputStream("data/author-abbreviations-matcher-test-output.txt");
		OutputStreamWriter streamWriter = new OutputStreamWriter(out,"UTF-8");
		FileInputStream authTestFile = new FileInputStream("T:/Development/Medicinal Plants/name-matching/JavaApp/data/author-abbreviations-matcher-test-file.txt");
		InputStreamReader streamReader = new InputStreamReader(authTestFile,"UTF-8");
		BufferedReader br = new BufferedReader(streamReader);
		
		
		String id;
		String line = null;
		String s1;
		String s2;
		String s1orig;
		String s2orig;
		
		
		String[] str1;
		String[] str2;
		
		StringBuffer outLn = new StringBuffer();
		
		while ((line = br.readLine()) != null) {
			String[] elem = line.split("%");
			id = elem[0];
			s1orig = elem[1];
			s2orig = elem[2];
			
			s1 = s1orig;
			s2 = s2orig;
			
			outLn.append(id+"%"+s1+"%"+s2+"%");
			
			s1 = n.translateMisspellings(s1);
			s2 = n.translateMisspellings(s2);
			
			outLn.append(s1+"%"+s2+"%");
			
			s1 = n.clean(s1);
			s2 = n.clean(s2);
			
			outLn.append(s1+"%"+s2+"%");
			
			str1 = s1.split(" ");
			str2 = s2.split(" ");
			
			str1 = n.translateAbbreviation(str1);
			str2 = n.translateAbbreviation(str2);
			
			s1  = n.singleString(str1);
			s2 =  n.singleString(str2);	
			
			outLn.append(s1+"%"+s2+"%");
			
			
			outLn.append(n.matches(s1orig, s2orig)+"\r\n");
			streamWriter.write(outLn.toString());
			outLn.setLength(0);
		}	
			
		streamWriter.flush();

		}
		
		catch (Exception e) {
			e.printStackTrace();
		}		
	}
}
