package ethz.nlp.headgen.prob;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import ethz.nlp.headgen.Doc;

public interface NGramProbs {
	/**
	 * Take list of strings and return the probability that sequence of words
	 * occurred
	 * 
	 * @param words
	 * @return
	 */
	public double getProb(String... words);

	// it takes the big ngram library and restricts it to only ngrams that are
	// related to the document
	public TreeMap<ArrayList<String>, Double> filterNgrams(Doc DOC);

	/**
	 * Take list of strings and return the probability that sequence of words
	 * occurred
	 * 
	 * @param words
	 * @return
	 */
	public double getProb(List<String> words);
}
