package ethz.nlp.headgen.sum;

public abstract class WeightedFeature implements Feature {
	protected double weight;

	public WeightedFeature(double weight) {
		this.weight = weight;
	}

	@Override
	public double calc(String word) {
		return weight * doCalc(word);
	}

	protected abstract double doCalc(String word);
}
