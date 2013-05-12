package ethz.nlp.headgen.prob;

import java.util.ArrayList;
import java.util.TreeMap;

public interface DocNGramProbs {
	/**
	 * Take in the text of a document and return the a queryable ngram
	 * probabilities object
	 * 
	 * @param docText
	 * @return
	 */
	public TreeMap<ArrayList<String>, Double> getProbs(String docText);
}
