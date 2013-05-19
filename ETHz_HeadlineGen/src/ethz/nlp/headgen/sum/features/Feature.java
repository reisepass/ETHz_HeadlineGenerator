package ethz.nlp.headgen.sum.features;

import edu.stanford.nlp.ling.CoreLabel;

public interface Feature {

	/**
	 * Calculate the feature function on the specified word
	 * 
	 * @param wordAnnotation
	 * @return Feature score for the specified word.
	 */
	public double calc(CoreLabel wordAnnotation);
}
