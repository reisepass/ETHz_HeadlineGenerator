package ethz.nlp.headgen.sum.features;

import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import ethz.nlp.headgen.Doc;
import ethz.nlp.headgen.data.CorpusCounts;
import ethz.nlp.headgen.data.TF_IDF;

public class Tf_IdfFeature extends WeightedFeature {
	private CorpusCounts counts;
	private Doc doc;

	public Tf_IdfFeature(CorpusCounts counts, Doc doc) {
		this(1, counts, doc);
	}

	public Tf_IdfFeature(double weight, CorpusCounts counts, Doc doc) {
		super(weight);
		this.counts = counts;
		this.doc = doc;
	}

	@Override
	protected double doCalc(CoreLabel wordAnnotation) {
		return TF_IDF.calc(wordAnnotation.get(TextAnnotation.class), doc,
				counts);
	}
}
