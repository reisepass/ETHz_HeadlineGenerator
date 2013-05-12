package ethz.nlp.headgen.sum;

import java.util.ArrayList;
import java.util.TreeMap;

import ethz.nlp.headgen.Doc;
import ethz.nlp.headgen.prob.NGramProbs;

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
		
		
		
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
