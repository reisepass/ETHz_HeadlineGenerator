package ethz.nlp.headgen.sum;

import ethz.nlp.headgen.Doc;
import ethz.nlp.headgen.data.CorpusCounts;
import ethz.nlp.headgen.data.TF_IDF;

public class Tf_IdfFeature extends WeightedFeature {
	private CorpusCounts counts;
	private Doc doc;

	public Tf_IdfFeature(double weight, CorpusCounts counts, Doc doc) {
		super(weight);
		this.counts = counts;
		this.doc = doc;
	}

	@Override
	protected double doCalc(String word) {
		return TF_IDF.calc(word, doc, counts);
	}
}
