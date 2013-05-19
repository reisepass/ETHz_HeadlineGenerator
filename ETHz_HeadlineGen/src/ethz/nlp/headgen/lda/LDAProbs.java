package ethz.nlp.headgen.lda;

public interface LDAProbs {

	public int getNumTopics();

	public String[] getDocList();

	/**
	 * Returns the probability P(word | topic)
	 * 
	 * @param word
	 *            The word whose conditional probability you want.
	 * @param topic
	 *            The topic you want to condition on
	 * @return The conditional probability P(word | topic)
	 */
	public double getWordTopicProb(String word, int topic);

	/**
	 * Returns the probability P(topic | document)
	 * 
	 * @param topic
	 *            The topic whose conditional probability you want
	 * @param doc
	 *            The document you was to condition on
	 * @return The conditional probability P(topic | document)
	 */
	public double getTopicDocProb(int topic, String doc);

	public int getMostLikelyTopic(String doc);
}
