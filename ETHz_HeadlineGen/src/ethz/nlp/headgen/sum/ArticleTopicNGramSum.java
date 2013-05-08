package ethz.nlp.headgen.sum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ethz.nlp.headgen.Doc;
import ethz.nlp.headgen.prob.DocNGramProbs;
import ethz.nlp.headgen.prob.NGramProbs;
import ethz.nlp.headgen.prob.NgramLightFilter;
import ethz.nlp.headgen.prob.NgramSimple;
 
public class ArticleTopicNGramSum extends FirstSentSum implements Summerizer {

	protected TreeMap<ArrayList<String>,Double> topicWeightedNgrams;
	
	public ArticleTopicNGramSum(Doc doc, int summaryLength) {
		super(doc, summaryLength);
		// TODO Auto-generated constructor stub
	}
	

	/**
	 * @param args
	 */


	protected String printArray(ArrayList<String> in){
		StringBuilder strBld = new StringBuilder();
		for(String el : in){
			strBld.append(el+" ");
			
		}
		return strBld.toString();
	}
	
	
	protected void testData(){
		topicWeightedNgrams = new  TreeMap<ArrayList<String>, Double>(new Comparator() {
	         public int compare(Object o1, Object o2) {
	              return  o1.toString().compareTo(o2.toString());
	         }
	    });
		ArrayList<String> tmpEl = new ArrayList<String>();
		tmpEl.add("Herp"); tmpEl.add("Derp");
		topicWeightedNgrams.put(tmpEl,0.1 );
		
		tmpEl = new ArrayList<String>();
		tmpEl.add("Ruben"); tmpEl.add("Wolff");
		topicWeightedNgrams.put(tmpEl,0.1 );
		
		tmpEl = new ArrayList<String>();
		tmpEl.add("Wolff"); tmpEl.add("is");
		topicWeightedNgrams.put(tmpEl,0.1 );
		
		tmpEl = new ArrayList<String>();
		tmpEl.add("is"); tmpEl.add("cool");
		topicWeightedNgrams.put(tmpEl,0.1 );
		
		tmpEl = new ArrayList<String>();
		tmpEl.add("Ruben"); tmpEl.add("likes");
		topicWeightedNgrams.put(tmpEl,0.1 );
		
		tmpEl = new ArrayList<String>();
		tmpEl.add("likes"); tmpEl.add("being");
		topicWeightedNgrams.put(tmpEl,0.1 );
		
		tmpEl = new ArrayList<String>();
		tmpEl.add("being"); tmpEl.add("awsome");
		topicWeightedNgrams.put(tmpEl,0.1 );
		
		tmpEl = new ArrayList<String>();
		tmpEl.add("Ruben"); tmpEl.add("Wolff");
		topicWeightedNgrams.put(tmpEl,0.1 );
		
		tmpEl = new ArrayList<String>();
		tmpEl.add("Wolff"); tmpEl.add("is");
		topicWeightedNgrams.put(tmpEl,0.1 );
		
		tmpEl = new ArrayList<String>();
		tmpEl.add("the"); tmpEl.add("man");
		topicWeightedNgrams.put(tmpEl,0.1 );
		
		tmpEl = new ArrayList<String>();
		tmpEl.add("Ruben"); tmpEl.add("just");
		topicWeightedNgrams.put(tmpEl,0.2 );
		
		tmpEl = new ArrayList<String>();
		tmpEl.add("just"); tmpEl.add("passed");
		topicWeightedNgrams.put(tmpEl,0.3 );
		
		tmpEl = new ArrayList<String>();
		tmpEl.add("passed"); tmpEl.add("his");
		topicWeightedNgrams.put(tmpEl,0.1 );
		
		tmpEl = new ArrayList<String>();
		tmpEl.add("his"); tmpEl.add("final");
		topicWeightedNgrams.put(tmpEl,0.1 );
		
		tmpEl = new ArrayList<String>();
		tmpEl.add("final"); tmpEl.add("exam");
		topicWeightedNgrams.put(tmpEl,0.3 );
		
	}
	
	
	// Very basic method simply listing the top most likely ngrams in a row to fill up the character limit. 
	public String summary() {
			StringBuilder strBld = new StringBuilder();
			String out="##################################### #";
			testData();  //TODO <- Instead of doing this we need to save the real topicWeighted ngrams for this article to var topicWeightedNgrams
			NgramSimple topicNgrams = new NgramLightFilter(topicWeightedNgrams,2,1);
			TreeMap<ArrayList<String>,Double> filtered = topicNgrams.filterNgrams(doc);
				
			
	
			
			 List< Map.Entry<ArrayList<String>, Double>> list = new LinkedList< Map.Entry<ArrayList<String>, Double>>(filtered.entrySet());
			    Collections.sort(list, new Comparator() {
			         public int compare(Object o1, Object o2) {
			              return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
			         }
			    });
			    Collections.reverse(list);

			for(Map.Entry<ArrayList<String>, Double> elem : list){
	 
				strBld.append(printArray(elem.getKey()));
				
				if(strBld.length()>sumLeng){
					out = strBld.toString();
					out = out.substring(0,sumLeng-1);
					out = out.substring(0,out.lastIndexOf(" "));
					break;
				}
			}
			
		
			return out;
	}
	

}
