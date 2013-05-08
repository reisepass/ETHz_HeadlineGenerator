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
	public TreeMap<ArrayList<String>,Double> filterNgrams(Doc DOC);
	/**
	 * Take list of strings and return the probability that sequence of words
	 * occurred
	 * 
	 * @param words
	 * @return
	 */
	public double getProb(List<String> words);
}
