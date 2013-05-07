package ethz.nlp.headgen.sum;

import java.util.ArrayList;
import java.util.TreeMap;

import ethz.nlp.headgen.Doc;
import ethz.nlp.headgen.prob.DocNGramProbs;
import ethz.nlp.headgen.prob.NGramProbs;
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


	private String printArray(ArrayList<String> in){
		StringBuilder strBld = new StringBuilder();
		for(String el : in){
			strBld.append(el+" ");
			
		}
		return strBld.toString();
	}
	
	
	private void testData(){
		topicWeightedNgrams = new  TreeMap<ArrayList<String>, Double>();
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
	}
	
	
	// Very basic method simply listing the top most likely ngrams in a row to fill up the character limit. 
	public String summary() {
			StringBuilder strBld = new StringBuilder();
			String out="##################################### #";
			testData();  //TODO <- Instead of doing this we need to save the real topicWeighted ngrams for this article to var topicWeightedNgrams
			NgramSimple topicNgrams = new NgramSimple(topicWeightedNgrams,2);
			TreeMap<ArrayList<String>,Double> filtered = topicNgrams.filterNgrams(this.doc.annotation);
			ArrayList<String> curNgram=filtered.firstKey();
			while(true){
				
				if(strBld.length()<2){
					strBld.append(printArray(curNgram));
				}
				else{
					curNgram = filtered.lowerKey(curNgram);
					strBld.append(printArray(curNgram));
					
				}
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
