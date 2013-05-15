package ethz.nlp.headgen.sum;

public interface Feature {

	/**
	 * Calculate the feature function on the specified word
	 * 
	 * @param word
	 * @return Feature score for the specified word.
	 */
	public double calc(String word);
}
