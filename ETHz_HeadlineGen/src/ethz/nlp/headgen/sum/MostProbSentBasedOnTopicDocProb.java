/**
 * 
 */
package ethz.nlp.headgen.sum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ethz.nlp.headgen.Doc;
import ethz.nlp.headgen.Extractor;
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
		out.add(ngramLength-1, inp);
		return out;
	}
	
	protected ArrayList<String> wildWithInpAtFront(String inp){
		ArrayList<String> out = new ArrayList<String>(ngramLength);
		out.add(0, inp);
		for(int i=1;i<ngramLength;i++){
			out.add(i, FirstSentSum.WILDCARD_STRING);
		}
		return out;
	}
	
	public String summary() {
		
		StringBuilder strBld = new StringBuilder();
		String out="##################################### #";
		testData();  //TODO <- Instead of doing this we need to save the real topicWeighted ngrams for this article to var topicWeightedNgrams
		ngramLength=topicWeightedNgrams.firstEntry().getKey().size();
		
		NgramLightFilter topicNgrams = new NgramLightFilter(topicWeightedNgrams,ngramLength,1);
		
		Comparator<ArrayList<String>> CompareObj=  new Comparator<ArrayList<String>>(){
			public int compare(ArrayList<String> o1, ArrayList<String> o2) {
				
					if(o1.size()!=o2.size()){
						int size = o1.size();
						if(o2.size()< o1.size())
							size=o2.size();
						int result=0;
						for(int i=0; i<size;i++){
							if(o1.get(i).equals(o2.get(i)))
								continue;
							else
								result = o1.get(i).compareTo(o2.get(i));
						}
						if(result==0){
							return result;
						}
						else{
							for(int j=size-1; j<=0 ; j--){
								if(o1.get(j).equals(o2.get(j)))
									continue;
								else
									result = o1.get(j).compareTo(o2.get(j));
							}
							return result;
							
						}
	
					}
					else{
						
						for(int i=0;i<o1.size();i++){
								if(o1.get(i).equals(FirstSentSum.WILDCARD_STRING))
									continue;
								if(o2.get(i).equals(FirstSentSum.WILDCARD_STRING))
									continue;
								if(o1.get(i).equals(o2.get(i)))
									continue;
								else
									return o1.get(i).compareTo(o2.get(i));
							
						}
						return 0;
						
						
					}
				
				
				
            }} ;
		
		
		
		
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
		
		if(topNE[0]!=null){ // adding describter for the top NE
			
			ArrayList<String> begin = wildWithInpAtBack(topNE[0]);
			boolean found=false;
			
			for(Map.Entry<ArrayList<String>, Double> elem : list){	// OMG This may read through the entire library	
				if(CompareObj.compare(elem.getKey(),begin)==0){
					strBld.append(printArray(elem.getKey()));	//I hope this ngram would describe the NE since it comes before
					found=true;
					lastAdded=elem.getKey().get(elem.getKey().size()-1);
					break;
				}
			}
		
			//TODO Now i want to add the most used verb // Need some more code for that in Extractor
			//Instead i am jsut going to look for the best ngram that begins with the TopNE
			ArrayList<String> next = wildWithInpAtFront(topNE[0]);

			for(Map.Entry<ArrayList<String>, Double> elem : list){	// OMG This may read through the entire library	
				if(CompareObj.compare(elem.getKey(),next)==0){
					strBld.append(printArray(elem.getKey()));	//I hope this ngram would describe the NE since it comes before
					elem.getKey().remove(0);// If we dont do this then the TopNE[0] will appear twice
					lastAdded=elem.getKey().get(elem.getKey().size()-1);
					break;
				}
			}
		
		}
		//TODO It would be best if we have a verb connecting the top most used NE 
		
		if(topNE[1]!=null){
			ArrayList<String> begin = wildWithInpAtBack(topNE[1]);
				for(Map.Entry<ArrayList<String>, Double> elem : list){	// OMG This may read through the entire library	
				if(CompareObj.compare(elem.getKey(),begin)==0){
					strBld.append(printArray(elem.getKey()));	//I hope this ngram would describe the NE since it comes before
					lastAdded=elem.getKey().get(elem.getKey().size()-1);
					break;
				}
			}
		
			ArrayList<String> next = wildWithInpAtFront(topNE[1]);
			
			
			for(Map.Entry<ArrayList<String>, Double> elem : list){	// OMG This may read through the entire library	
				if(CompareObj.compare(elem.getKey(),next)==0){
					elem.getKey().remove(0);// If we dont do this then the TopNE[0] will appear twice
					strBld.append(printArray(elem.getKey()));	//I hope this ngram would describe the NE since it comes before
					
					lastAdded=elem.getKey().get(elem.getKey().size()-1);
					break;
				}
			}
		}
		out = strBld.toString();
		String[] wordsSoFar  = strBld.toString().split("\\s");
		String justAdded= wordsSoFar[wordsSoFar.length-1];
		  while(  strBld.length()>sumLeng   ){ 
			  for(Map.Entry<ArrayList<String>, Double> elem : list){	// OMG This may read through the entire library	
				
					if(CompareObj.compare(elem.getKey(),wildWithInpAtFront(justAdded))==0){
						elem.getKey().remove(0);// If we dont do this then the TopNE[0] will appear twice
						strBld.append(printArray(elem.getKey()));
						wordsSoFar  = strBld.toString().split("\\s");
						justAdded= wordsSoFar[wordsSoFar.length-1];
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
