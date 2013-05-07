package ethz.nlp.headgen.prob;

import java.util.List;

public interface NGramProbs {
	/**
	 * Take list of strings and return the probability that sequence of words
	 * occurred
	 * 
	 * @param words
	 * @return
	 */
	public double getProb(String... words);

	/**
	 * Take list of strings and return the probability that sequence of words
	 * occurred
	 * 
	 * @param words
	 * @return
	 */
	public double getProb(List<String> words);
}
