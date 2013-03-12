


import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.trees.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.util.CoreMap;


public class Extractor {

	/* 
	 * This class has a variety of different feature extraction methods 
	 * which were intended to be used later in to classify documents into 
	 * groups. So that we can create a mixture of experts, I.E. we can 
	 * do learn which one of our headline generation methods worked well
	 * on which type of document. 
	 * 
	 * For this purpose I am creating as many random bits of comparative
	 * information as I can think of. 
	 * 
	 * 
	 * 
	 * 
	 * 
	 *  TEMPORARY NOTES TEMPORARY NOTES TEMPORARY NOTES TEMPORARY NOTES TEMPORARY NOTES TEMPORARY NOTES 
	 *  TEMPORARY NOTES TEMPORARY NOTES TEMPORARY NOTES TEMPORARY NOTES TEMPORARY NOTES TEMPORARY NOTES 
	 * 		- I want to merge all the functions that iterate over every word in the document into one loop, inside runAll. 
	 * 		  method, but i am not sure how to do this without repeating code form the methods which call individual feature
	 * 		  extraction loops. 
	 * 
	 * 
	 */
	
	private Annotation document;
	private Map<String,Integer> posTagCounts;
	private Map<String,Integer> nameEntityCounts;
	private Map<String,Integer> nameEntityTypeCounts;
	private Map<String,Integer> wordCounts;
 	public Extractor (Annotation preAnnotatedDoc){
		
		document = preAnnotatedDoc;
		Map<String,Integer> nameEntityCounts=null;
		
	}
	

	public void runAll(){
		//posTagCount();
		//nameEntityTypeCount(); 
		//wordCount();
		
		
		Map<String,Integer> posCount=new HashMap<String,Integer> ();
		Map<String,Integer> words=new HashMap<String,Integer> ();
		Map<String,Integer> nameCount=new HashMap<String,Integer> ();
		
		
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	    for(CoreMap sentence: sentences) {
	      // traversing the words in the current sentence
	      // a CoreLabel is a CoreMap with additional token-specific methods
	    	String lastType="";
	    	String lastWord="";
	      for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
	        // this is the text of the token
	    	String ne = token.get(NamedEntityTagAnnotation.class);       
	    	  
	    	String pos = token.get(PartOfSpeechAnnotation.class);
	        if(posCount.get(pos)==null){
	        	posCount.put(pos,1);
	        }
	        else
	        	posCount.put(pos, posCount.get(pos)+1);
	        
	        String wrd = token.get(TextAnnotation.class);
	        if(words.get(wrd)==null){
	        	words.put(wrd,1);
	        }
	        else
	        	words.put(wrd, words.get(wrd)+1);
	    	  
	    	  
	    	  if(lastType.equals("PERSON")||lastType.equals("MISC")||lastType.equals("LOCATION")||lastType.equals("ORGANIZATION")){
	    		  if(ne.equals("PERSON")||ne.equals("MISC")||ne.equals("LOCATION")||ne.equals("ORGANIZATION")){
	    			  String word = token.get(TextAnnotation.class);
	    			  lastWord=lastWord+" "+wrd;
	    			  
	    		  }
	    		  else{
	    			  
			  	        if(nameCount.get(lastWord)==null){
			  	        	nameCount.put(lastWord,1);
			  	        }
			  	        else
			  	        	nameCount.put(lastWord, nameCount.get(lastWord)+1);
			  	      lastWord=wrd;
			  	      lastType=ne;
		    	}
	    	  }
	    	  else{
	    		  lastWord=wrd;
	    		  lastType=ne;
	    	  }
	    	  //I assume that every document ends with a period. Periods are not named entities and hence will triger the else 
	    	  // to enter in the last named entity into the hash table, if it happens to be 
	   
	      }

	    }
		
		
		
	    nameEntityCounts=nameCount; //Save locally in case we runAll and want to retrieve them differently
	    wordCounts=words;
	    posTagCounts=posCount;
		
	
	}
	
	public Map<String,Integer> posTagCount(){
		Map<String,Integer> posCount=new HashMap<String,Integer> ();
		
		
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	    
	    for(CoreMap sentence: sentences) {
	      // traversing the words in the current sentence
	      // a CoreLabel is a CoreMap with additional token-specific methods
	      for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
	        // this is the text of the token
	        String pos = token.get(PartOfSpeechAnnotation.class);
	        if(posCount.get(pos)==null){
	        	posCount.put(pos,1);
	        }
	        else
	        	posCount.put(pos, posCount.get(pos)+1);
	      }

	    }
		
		
		
	    posTagCounts=posCount; //Save locally in case we runAll and want to retrieve them differently
		return posCount;
	}

	public Map<String,Integer> wordCount(){
			Map<String,Integer> words=new HashMap<String,Integer> ();
		
		
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	    
	    for(CoreMap sentence: sentences) {
	      // traversing the words in the current sentence
	      // a CoreLabel is a CoreMap with additional token-specific methods
	      for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
	        // this is the text of the token
	        String wrd = token.get(TextAnnotation.class);
	        if(words.get(wrd)==null){
	        	words.put(wrd,1);
	        }
	        else
	        	words.put(wrd, words.get(wrd)+1);
	      }

	    }
		
		
		
	    wordCounts=words; //Save locally in case we runAll and want to retrieve them differently
		return words;
	}
	
	
	/*
	 * 
	 * Currently this class counts words like New York as 2 seperate things, Even tho 
	 * the framework knows they are to gather. Have not found out where that information
	 * is sitting. 
	 * 
	 * ????? Is it safe to assume that if there are n words in a row with the same 
	 * NE type tag then all n words are actually part of 1 long word. 
	 */
	public Map<String,Integer> nameEntityTypeCount(){
		
		
		
		Map<String,Integer> nameCount=new HashMap<String,Integer> ();
		
		
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	    
	    for(CoreMap sentence: sentences) {
	      // traversing the words in the current sentence
	      // a CoreLabel is a CoreMap with additional token-specific methods
	    	String lastType="";
	    	String lastWord="";
	      for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
	        // this is the text of the token
	    	  String ne = token.get(NamedEntityTagAnnotation.class);       
	    	  
	    	  
	    	  if(lastType.equals("PERSON")||lastType.equals("MISC")||lastType.equals("LOCATION")||lastType.equals("ORGANIZATION")){
	    		  if(ne.equals("PERSON")||ne.equals("MISC")||ne.equals("LOCATION")||ne.equals("ORGANIZATION")){
	    			  String word = token.get(TextAnnotation.class);
	    			  lastWord=lastWord+" "+word;
	    			  continue;
	    		  }
	    		  else{
	    			  
			  	        if(nameCount.get(lastWord)==null){
			  	        	nameCount.put(lastWord,1);
			  	        }
			  	        else
			  	        	nameCount.put(lastWord, nameCount.get(lastWord)+1);
			  	      continue;
		    	}
	    	  }
	    	  else{
	    		  
	    	  }
	   
	      }

	    }
		
		
		
	    nameEntityTypeCounts=nameCount; //Save locally in case we runAll and want to retrieve them differently
		return nameCount;
	}
	
	public void rankedNameEntityCount(){
		//TODO STUB
	}
}
