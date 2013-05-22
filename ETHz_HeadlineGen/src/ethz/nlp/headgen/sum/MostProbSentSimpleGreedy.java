package ethz.nlp.headgen.sum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ethz.nlp.headgen.Constants;
import ethz.nlp.headgen.Doc;
import ethz.nlp.headgen.prob.NGramProbs;
import ethz.nlp.headgen.prob.NoFilterAddTestCorpus;

public class MostProbSentSimpleGreedy extends ArticleTopicNGramSum implements
		Summerizer {
	
	int n;
	NGramProbs corpNgrams;
	public MostProbSentSimpleGreedy(Doc doc, int summaryLength, NGramProbs corpusNgramsAndProbs) {
		super(doc, summaryLength);
		
		n=2;
		corpNgrams=corpusNgramsAndProbs;
		
		// TODO Auto-generated constructor stub
	}
	/**
	 * We expect corpusNgramsAndProbs to already have the LDA topic to doc probabilities multiplied in for the given doc 
	 * @param doc
	 * @param summaryLength
	 * @param corpusNgramsAndProbs
	 */
	public MostProbSentSimpleGreedy(Doc doc, int summaryLength,NGramProbs corpusNgramsAndProbs, int ngramLength) {
		super(doc, summaryLength);
		
		n=ngramLength;
		corpNgrams=corpusNgramsAndProbs;

	}

	
	public String summary() {
		TreeMap<ArrayList<String>,Double> corpTree=corpNgrams.filterNgrams(doc);
		NGramProbs concat = new NoFilterAddTestCorpus(corpTree,1.5);
		TreeMap<ArrayList<String>, Double> corpDocngrams = concat.filterNgrams(doc);
		Comparator<ArrayList<String>> localCompareObj=Constants.CompareObj;
		
		
		StringBuilder strBld = new StringBuilder();
		String out="#####################22###############";
	

		 List< Map.Entry<ArrayList<String>, Double>> sorted = new LinkedList< Map.Entry<ArrayList<String>, Double>>(corpDocngrams.entrySet());
		    Collections.sort(sorted, new Comparator() {
		         public int compare(Object o1, Object o2) {
		              return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
		         }
		    });
		    Collections.reverse(sorted);
		ArrayList<String> first = sorted.get(0).getKey();
		int ngramLength = first.size(); 
		strBld.append(printArray(first));
		   
		   String[] wordsSoFar  = strBld.toString().split(" ");
			String justAdded= wordsSoFar[wordsSoFar.length-1];
			while(  strBld.length()<sumLeng   ){ 
				  for(Map.Entry<ArrayList<String>, Double> elem : sorted){	// OMG This may read through the entire library	
					  if(elem.getKey().size()>1){
						if(localCompareObj.compare(elem.getKey(),wildWithInpAtFront(justAdded,ngramLength ))==0){
							elem.getKey().remove(0);// If we dont do this then the TopNE[0] will appear twice
							strBld.append(printArray(elem.getKey()));
							wordsSoFar  = strBld.toString().split(" ");
							justAdded= wordsSoFar[wordsSoFar.length-1];
						}
					  }
				  }
				  strBld.append(" ");
				if(strBld.length()>sumLeng){ //Remove words that done fit and we are done
					out = strBld.toString();
					out = out.substring(0,sumLeng-1);
					out = out.substring(0,out.lastIndexOf(" "));
					break;
				}
				
			  }
		   
		   return out;
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
