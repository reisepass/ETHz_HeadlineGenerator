package ethz.nlp.headgen.prob;
 
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.TreeMap;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;
import ethz.nlp.headgen.sum.FirstSentSum;

public class NgramSimple implements NGramProbs {
	protected TreeMap<ArrayList<String>,Double> ngramFreq;
	protected int n = 2; 
	
	public NgramSimple(TreeMap<ArrayList<String>,Double> inNgrams) {
		ngramFreq=inNgrams;
		n=inNgrams.firstEntry().getKey().size();
		
	}
	public NgramSimple(TreeMap<ArrayList<String>,Double> inNgrams, int N) {
		ngramFreq=inNgrams;
		this.n=N;
	}
 

	
	public TreeMap<ArrayList<String>,Double> filterNgrams(Annotation docAno){ //TODO it would be good to strip docAno of punctuation and closed form words
		List<CoreMap> sentences = docAno.get(SentencesAnnotation.class);
		TreeMap<ArrayList<String>,Double> filteredNgram = new TreeMap<ArrayList<String>,Double>(ngramFreq.comparator());		
		ArrayList<ArrayList<String>> unUsed = new ArrayList<ArrayList<String>>();
		Stack<String> lastWords = new Stack<String>();
		String curWord=null;
		for (CoreMap sentence : sentences) {			
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				if(curWord!=null)
					lastWords.add(curWord);
				curWord = token.get(TextAnnotation.class);
				
				if(lastWords.size()==n){
					String[] tmp = new String[n];
					lastWords.toArray(tmp);
					ArrayList<String> ngram = new ArrayList<String>(Arrays.asList(tmp));
					if(ngramFreq.containsKey(ngram))
						filteredNgram.put(ngram,ngramFreq.get(ngram));
					else
						unUsed.add(ngram);
					
				}
			}
		}
		
		return filteredNgram;
	}
	
	@Override
	public double getProb(String... words) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getProb(List<String> words) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
