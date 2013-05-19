package ethz.nlp.headgen.sum.features;

import edu.stanford.nlp.ling.CoreLabel;

public abstract class WeightedFeature implements Feature {
	protected double weight;

	public WeightedFeature(double weight) {
		this.weight = weight;
	}

	@Override
	public double calc(CoreLabel wordAnnotation) {
		return weight * doCalc(wordAnnotation);
	}

	protected abstract double doCalc(CoreLabel wordAnnotation);
}
