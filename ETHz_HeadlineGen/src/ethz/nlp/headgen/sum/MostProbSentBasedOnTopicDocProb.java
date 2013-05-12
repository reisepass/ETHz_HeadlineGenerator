/**
 * 
 */
package ethz.nlp.headgen.sum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ethz.nlp.headgen.Constants;
import ethz.nlp.headgen.Doc;
import ethz.nlp.headgen.Extractor;
import ethz.nlp.headgen.lda.RawToLDA;
import ethz.nlp.headgen.prob.CorpPlusQueryDocNgrams;
import ethz.nlp.headgen.prob.DocNGramProbs;
import ethz.nlp.headgen.prob.DocNGramSimple;
import ethz.nlp.headgen.prob.NGramProbs;
import ethz.nlp.headgen.prob.NgramLightFilter;
import ethz.nlp.headgen.prob.NgramSimple;

/**
 * @author mort
 *
 */
public class MostProbSentBasedOnTopicDocProb extends ArticleTopicNGramSum implements Summerizer {
	protected Extractor extr;
	protected int ngramLength= 3;
 
	/**
	 * @param doc
	 * @param summaryLength
	 * 
	 */
	public MostProbSentBasedOnTopicDocProb(Doc doc, int summaryLength) {
		super(doc, summaryLength);
		extr = new Extractor(doc.getAno());
		extr.runAll();
		// TODO Auto-generated constructor stub
	}

	protected ArrayList<String> wildWithInpAtBack(String inp){
		ArrayList<String> out = new ArrayList<String>(ngramLength);
		for(int i=0;i<ngramLength-1;i++){
			out.add(i, FirstSentSum.WILDCARD_STRING);
		}
		if(inp.indexOf(" ")!=-1){
			for(String sepI : inp.split(" ")){
				out.add(sepI);
			}
		}
		else
			out.add(ngramLength-1, inp);
		return out;
	}
	
	protected ArrayList<String> wildWithInpAtFront(String inp){
		ArrayList<String> out = new ArrayList<String>(ngramLength);
		
		if(inp.indexOf(" ")!=-1){
			for(String sepI : inp.split(" ")){
				out.add(sepI);
			}
		}
		else
			out.add(0, inp);
		for(int i=1;i<ngramLength;i++){
			out.add(i, FirstSentSum.WILDCARD_STRING);
		}
		return out;
	}
	
	public String summary() {
		
		StringBuilder strBld = new StringBuilder();
		String out="######################################";
		//testData();  //TODO <- Instead of doing this we need to save the real topicWeighted ngrams for this article to var topicWeightedNgrams		
		//ngramLength=topicWeightedNgrams.firstEntry().getKey().size();
		
	
		//NgramLightFilter topicNgrams = new NgramLightFilter(topicWeightedNgrams,ngramLength,1);
		DocNGramProbs ngramMaker = new DocNGramSimple(3);
		TreeMap<ArrayList<String>, Double> filterThis = ngramMaker.getProbs(doc.cont);
		NGramProbs topicNgrams = new CorpPlusQueryDocNgrams(filterThis);
	
		    Comparator<ArrayList<String>> localCompareObj=Constants.CompareObj;
		
		
		TreeMap<ArrayList<String>,Double> filtered = topicNgrams.filterNgrams(doc);
			
			
		
		 List< Map.Entry<ArrayList<String>, Double>> list = new LinkedList< Map.Entry<ArrayList<String>, Double>>(filtered.entrySet());
		    Collections.sort(list, new Comparator() {
		         public int compare(Object o1, Object o2) {
		              return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
		         }
		    });
		    Collections.reverse(list);

		    
		String[] topNE = extr.rankedNameEntityCount(5);
		String lastAdded = "";
		ArrayList<String> topNE_rl = new ArrayList<String>(Arrays.asList(topNE));
		
		for(int i =0; i<topNE_rl.size();i++){
			if(topNE_rl.get(i)!=null){

				topNE_rl.set(i,RawToLDA.convert(topNE_rl.get(i)));
			
			}
		}
		
		
		if(topNE_rl.get(0)!=null){ // adding describers for the top NE


			
			ArrayList<String> begin = wildWithInpAtBack(topNE_rl.get(0));
			
			
			
			boolean found=false;
			
			for(Map.Entry<ArrayList<String>, Double> elem : list){	// OMG This may read through the entire library	
				if(localCompareObj.compare(elem.getKey(),begin)==0){
					strBld.append(printArray(elem.getKey()));	//I hope this ngram would describe the NE since it comes before
					found=true;
					lastAdded=elem.getKey().get(elem.getKey().size()-1);
					break;
				}
			}
		
			//TODO Now i want to add the most used verb // Need some more code for that in Extractor
			//Instead i am jsut going to look for the best ngram that begins with the TopNE
			ArrayList<String> next = wildWithInpAtFront(topNE_rl.get(0));

			for(Map.Entry<ArrayList<String>, Double> elem : list){	// OMG This may read through the entire library	
				if(localCompareObj.compare(elem.getKey(),next)==0){
					strBld.append(printArray(elem.getKey()));	//I hope this ngram would describe the NE since it comes before
					elem.getKey().remove(0);// If we dont do this then the TopNE[0] will appear twice
					lastAdded=elem.getKey().get(elem.getKey().size()-1);
					break;
				}
			}
		
		}
		//TODO It would be best if we have a verb connecting the top most used NE 
		
		if(topNE_rl.get(1)!=null){
			ArrayList<String> begin = wildWithInpAtBack(topNE_rl.get(1));
				for(Map.Entry<ArrayList<String>, Double> elem : list){	// OMG This may read through the entire library	
				if(localCompareObj.compare(elem.getKey(),begin)==0){
					strBld.append(printArray(elem.getKey()));	//I hope this ngram would describe the NE since it comes before
					lastAdded=elem.getKey().get(elem.getKey().size()-1);
					break;
				}
			}
		
			ArrayList<String> next = wildWithInpAtFront(topNE_rl.get(1));
			
			
			for(Map.Entry<ArrayList<String>, Double> elem : list){	// OMG This may read through the entire library	
				if(localCompareObj.compare(elem.getKey(),next)==0){
					elem.getKey().remove(0);// If we dont do this then the TopNE[0] will appear twice
					strBld.append(printArray(elem.getKey()));	//I hope this ngram would describe the NE since it comes before
					
					lastAdded=elem.getKey().get(elem.getKey().size()-1);
					break;
				}
			}
		}
		out = strBld.toString();
		String[] wordsSoFar  = strBld.toString().split(" ");
		String justAdded= wordsSoFar[wordsSoFar.length-1];
		while(  strBld.length()<sumLeng   ){ 
			  for(Map.Entry<ArrayList<String>, Double> elem : list){	// OMG This may read through the entire library	
				  if(elem.getKey().size()>1){
					if(localCompareObj.compare(elem.getKey(),wildWithInpAtFront(justAdded))==0){
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

}
