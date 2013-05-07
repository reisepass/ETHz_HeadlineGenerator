package ethz.nlp.headgen.prob;

public interface DocNGramProbs {
	/**
	 * Take in the text of a document and return the a queryable ngram
	 * probabilities object
	 * 
	 * @param docText
	 * @return
	 */
	public NGramProbs getProbs(String docText);
}
