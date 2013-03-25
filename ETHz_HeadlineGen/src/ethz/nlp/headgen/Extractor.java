package ethz.nlp.headgen;
import java.util.HashMap;
import java.util.TreeMap;

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

	public Extractor(Annotation preAnnotatedDoc) {

		document = preAnnotatedDoc;
		Map<String, Integer> nameEntityCounts = null;

	}

	public void runAll() {
		// posTagCount();
		// nameEntityTypeCount();
		// wordCount();

		Map<String, Integer> posCount = new HashMap<String, Integer>();
		Map<String, Integer> words = new HashMap<String, Integer>();
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

				String pos = token.get(PartOfSpeechAnnotation.class);
				if (posCount.get(pos) == null) {
					posCount.put(pos, 1);
				} else
					posCount.put(pos, posCount.get(pos) + 1);

				String wrd = token.get(TextAnnotation.class);
				if (words.get(wrd) == null) {
					words.put(wrd, 1);
				} else
					words.put(wrd, words.get(wrd) + 1);

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

	
	
	// Finally 
	public String[] rankedNameEntityCount(int max) {
		
		

		/*
		HashMap<String,Integer> map = new HashMap<String,Integer>();
	        ValueComparator bvc =  new ValueComparator(map);
	        TreeMap<String, Integer> sortedMap = new TreeMap<String, Integer>(bvc);
			
		sortedMap.putAll(nameEntityCounts);
		
		String[] topNE = new String[max];
		int count=0;
		for (Map.Entry entry : sortedMap.entrySet()) {
			System.out.println("Key : " + entry.getKey() + " Value : "
				+ entry.getValue());
			
			topNE[count]=(String) entry.getKey();
			count++;
			if(count>max)
				continue;
		}
		
		*/
		
		
		
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
	
	
	
	
	/*
	static Map sortByValue(Map map) {
	    List list = new LinkedList(map.entrySet());
	    Collections.sort(list, new Comparator() {
	         public int compare(Object o1, Object o2) {
	              return ((Comparable) ((Map.Entry) (o1)).getValue())
	             .compareTo(((Map.Entry) (o2)).getValue());
	         }
	    });

	   Map result = new LinkedHashMap();
	   for (Iterator it = list.iterator(); it.hasNext();) {
	       Map.Entry entry = (Map.Entry)it.next();
	       result.put(entry.getKey(), entry.getValue());
	   }
	   return result;
	} 
	*/
	
	
}


/*
class ValueComparator implements Comparator<String> {

    Map<String, Integer> base;
    public ValueComparator(Map<String, Integer> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}
*/



