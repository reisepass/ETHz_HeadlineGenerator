package ethz.nlp.headgen;
import java.util.HashMap;
import java.util.TreeMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;

import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;
import ethz.nlp.headgen.sum.FirstSentSum;

public class Extractor {

	/*
	 * This class has a variety of different feature extraction methods which
	 * were intended to be used later in to classify documents into groups. So
	 * that we can create a mixture of experts, I.E. we can do learn which one
	 * of our headline generation methods worked well on which type of document.
	 * 
	 * For this purpose I am creating as many random bits of comparative
	 * information as I can think of.
	 * 
	 * 
	 * 
	 * 
	 * 
	 * TEMPORARY NOTES TEMPORARY NOTES TEMPORARY NOTES TEMPORARY NOTES TEMPORARY
	 * NOTES TEMPORARY NOTES TEMPORARY NOTES TEMPORARY NOTES TEMPORARY NOTES
	 * TEMPORARY NOTES TEMPORARY NOTES TEMPORARY NOTES - I want to merge all the
	 * functions that iterate over every word in the document into one loop,
	 * inside runAll. method, but i am not sure how to do this without repeating
	 * code form the methods which call individual feature extraction loops.
	 */

	private Annotation document;
	private Map<String, Integer> posTagCounts;
	private Map<String, Integer> nameEntityCounts;
	private Map<String, Integer> nameEntityTypeCounts;
	private Map<String, Integer> wordCounts;
	private Map<String, Integer> nounCounts;
	private Map<StrPair, Integer> biNNSentCounts;

	public Extractor(){
		document = null;
	}
	public Extractor(Annotation preAnnotatedDoc) {

		document = preAnnotatedDoc;
		Map<String, Integer> nameEntityCounts = null;

	}
	
	public Map<StrPair, Integer> getBiNNSentCounts(){
		if(biNNSentCounts==null){
			countWordCooccurances();
		}
		else if(biNNSentCounts.size()<1){
			countWordCooccurances();
		}
		return biNNSentCounts;
	}
	// By Sentence this 
	public void countWordCooccurances(){
		biNNSentCounts = new HashMap<StrPair, Integer>();
		ArrayList<String> openClassPos = new ArrayList(Arrays.asList(FirstSentSum.OPEN_CLASS_POS));  // Change this to a hashset or something that is faster lookup for .contains
		
		
	
		
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		int ab=3;
		for (CoreMap sentence : sentences) {
				int a=1+1;
				a=2;
				
				
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				String wrd = token.get(TextAnnotation.class);
				String pos = token.get(PartOfSpeechAnnotation.class);
				if(openClassPos.contains(pos)){
				
					for (CoreLabel token2 : sentence.get(TokensAnnotation.class)) {
						String wrd2 = token2.get(TextAnnotation.class);
						String pos2 = token2.get(PartOfSpeechAnnotation.class);
						if(openClassPos.contains(pos2)&&!wrd2.equals(wrd)){
							StrPair wrdPair = new StrPair(wrd,wrd2);
							if(biNNSentCounts.get(wrdPair)!=null){
								
								biNNSentCounts.put(wrdPair,biNNSentCounts.get(wrdPair)+1);
							}
							else{
								biNNSentCounts.put(wrdPair,1);
							}
						}
					}
				}
			}
			
			
		
		}
		
		
	}
	
	
	public void runAll() {
		// posTagCount();
		// nameEntityTypeCount();
		// wordCount();

		Map<String, Integer> posCount = new HashMap<String, Integer>();
		Map<String, Integer> words = new HashMap<String, Integer>();
		Map<String, Integer> nameCount = new HashMap<String, Integer>(); 
		Map<String, Integer> nounCnt = new HashMap<String, Integer>();
		
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {
			// traversing the words in the current sentence
			// a CoreLabel is a CoreMap with additional token-specific methods
			String lastType = "";
			String lastWord = "";
			
			
			
			
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				// this is the text of the token
				String ne = token.get(NamedEntityTagAnnotation.class);
				//counting PoS tags
				String pos = token.get(PartOfSpeechAnnotation.class);
				if (posCount.get(pos) == null) {
					posCount.put(pos, 1);
				} else
					posCount.put(pos, posCount.get(pos) + 1);
				
				
				//counting words
				String wrd = token.get(TextAnnotation.class);
				if (words.get(wrd) == null) {
					words.put(wrd, 1);
				} else
					words.put(wrd, words.get(wrd) + 1);

				//counting nouns seperatly
				if(pos.equals("NN")||pos.equals("NNS")){
					
					if(nounCnt.get(wrd) == null){
						nounCnt.put(wrd, 1);
					}
					else
						nounCnt.put(wrd, nounCnt.get(wrd) + 1) ;
					
				}
				
				if (lastType.equals("PERSON") || lastType.equals("MISC")
						|| lastType.equals("LOCATION")
						|| lastType.equals("ORGANIZATION")) {
					if (ne.equals("PERSON") || ne.equals("MISC")
							|| ne.equals("LOCATION")
							|| ne.equals("ORGANIZATION")) {
						String word = token.get(TextAnnotation.class);
						lastWord = lastWord + " " + wrd;

					} else {

						if (nameCount.get(lastWord) == null) {
							nameCount.put(lastWord, 1);
						} else
							nameCount
									.put(lastWord, nameCount.get(lastWord) + 1);
						lastWord = wrd;
						lastType = ne;
					}
				} else {
					lastWord = wrd;
					lastType = ne;
				}
				// I assume that every document ends with a period. Periods are
				// not named entities and hence will triger the else
				// to enter in the last named entity into the hash table, if it
				// happens to be

			}

		}

		nameEntityCounts = nameCount; // Save locally in case we runAll and want
										// to retrieve them differently
		wordCounts = words;
		posTagCounts = posCount;
		nounCounts = nounCnt;
	}

	public Map<String, Integer> posTagCount() {
		Map<String, Integer> posCount = new HashMap<String, Integer>();

		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		for (CoreMap sentence : sentences) {
			// traversing the words in the current sentence
			// a CoreLabel is a CoreMap with additional token-specific methods
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				// this is the text of the token
				String pos = token.get(PartOfSpeechAnnotation.class);
				if (posCount.get(pos) == null) {
					posCount.put(pos, 1);
				} else
					posCount.put(pos, posCount.get(pos) + 1);
			}

		}

		posTagCounts = posCount; // Save locally in case we runAll and want to
									// retrieve them differently
		return posCount;
	}

	public Map<String, Integer> wordCount() {
		Map<String, Integer> words = new HashMap<String, Integer>();

		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		for (CoreMap sentence : sentences) {
			// traversing the words in the current sentence
			// a CoreLabel is a CoreMap with additional token-specific methods
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				// this is the text of the token
				String wrd = token.get(TextAnnotation.class);
				if (words.get(wrd) == null) {
					words.put(wrd, 1);
				} else
					words.put(wrd, words.get(wrd) + 1);
			}

		}

		wordCounts = words; // Save locally in case we runAll and want to
							// retrieve them differently
		return words;
	}

	/*
	 * 
	 * 
	 * ????? Is it safe to assume that if there are n words in a row with the
	 * same NE type tag then all n words are actually part of 1 long word.
	 * 
	 * Ok in class it has been confirmed not safe. For example the United states of America
	 * but the stanford NLP tool kit actually labels the Of in that example as part of the
	 * organization. So that means they already solved the problem for us
	 * 
	 * 
	 * 
	 */
	public Map<String, Integer> nameEntityCount() {   

		Map<String, Integer> nameCount = new HashMap<String, Integer>();

		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		for (CoreMap sentence : sentences) {
			// traversing the words in the current sentence
			// a CoreLabel is a CoreMap with additional token-specific methods
			String lastType = "";
			String lastWord = "";
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				// this is the text of the token
				String ne = token.get(NamedEntityTagAnnotation.class);

				if (lastType.equals("PERSON") || lastType.equals("MISC")
						|| lastType.equals("LOCATION")
						|| lastType.equals("ORGANIZATION")) {
					if (ne.equals("PERSON") || ne.equals("MISC")
							|| ne.equals("LOCATION")
							|| ne.equals("ORGANIZATION")) {
						String word = token.get(TextAnnotation.class);
						lastWord = lastWord + " " + word;
						continue;
					} else {

						if (nameCount.get(lastWord) == null) {
							nameCount.put(lastWord, 1);
						} else
							nameCount
									.put(lastWord, nameCount.get(lastWord) + 1);
						continue;
					}
				} else {

				}

			}

		}

		nameEntityCounts = nameCount; // Save locally in case we runAll and
											// want to retrieve them differently
		return nameCount;
	}

	
	public String [] rankedNounCounts(int max){
		return getRankingFromMap(max,nounCounts);
	}
	// Finally 
	public String[] rankedNameEntityCount(int max) {
		
		

		
		
		//-----------------------------------
		
		//NavigableSet<String> sortedKeys=sortedMap.descendingKeySet();
		
		 List< Map.Entry<String, Integer>> list = new LinkedList< Map.Entry<String, Integer>>(nameEntityCounts.entrySet());
		    Collections.sort(list, new Comparator() {
		         public int compare(Object o1, Object o2) {
		              return ((Comparable) ((Map.Entry) (o1)).getValue())
		             .compareTo(((Map.Entry) (o2)).getValue());
		         }
		    });
		Collections.reverse(list);

		
	
		 int count=0;
		 String[] topNE= new String[max];
		Iterator<Entry<String, Integer>> iter = list.iterator();
		while(iter.hasNext()&&count<max){
			topNE[count]=iter.next().getKey();
			count++;
		}
		
		return topNE;
	}
	
	public String[] getRankingFromMap(int max,Map<String, Integer> mapInp ) {

		 List< Map.Entry<String, Integer>> list = new LinkedList< Map.Entry<String, Integer>>(mapInp.entrySet());
		    Collections.sort(list, new Comparator() {
		         public int compare(Object o1, Object o2) {
		              return ((Comparable) ((Map.Entry) (o1)).getValue())
		             .compareTo(((Map.Entry) (o2)).getValue());
		         }
		    });
		Collections.reverse(list);

		 int count=0;
		 String[] topNE= new String[max];
		Iterator<Entry<String, Integer>> iter = list.iterator();
		while(iter.hasNext()&&count<max){
			topNE[count]=iter.next().getKey();
			count++;
		}
		
		return topNE;
	}
	
	
	
	// There should be a way of making the method getRankingFromMap more generic so that it can include this usecase
	public  StrPair[] getRankingFromPairMap(int max,Map< StrPair, Integer> mapInp ) {

		 List< Map.Entry<StrPair, Integer>> list = new LinkedList< Map.Entry<StrPair, Integer>>(mapInp.entrySet());
		    Collections.sort(list, new Comparator() {
		         public int compare(Object o1, Object o2) {
		              return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
		         }
		    });
		Collections.reverse(list);

		 int count=0;
		 StrPair[] topNE= new StrPair[max];
		Iterator<Entry<StrPair, Integer>> iter = list.iterator();
		while(iter.hasNext()&&count<max){
			Entry<StrPair,Integer> tmp=iter.next();
			topNE[count]=tmp.getKey();
			System.out.println(tmp.toString());
			count++;
		}
		
		return topNE;
	}
}






