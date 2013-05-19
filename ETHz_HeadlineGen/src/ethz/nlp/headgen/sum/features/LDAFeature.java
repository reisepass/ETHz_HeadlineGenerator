package ethz.nlp.headgen.sum.features;

import ethz.nlp.headgen.Doc;
import ethz.nlp.headgen.lda.LDAProbs;

public class LDAFeature extends WeightedFeature {
	private LDAProbs probs;
	private Doc doc;

	public LDAFeature(LDAProbs inferredProbs, Doc doc) {
		this(inferredProbs, doc, 1.0);
	}

	public LDAFeature(LDAProbs inferredProbs, Doc doc, double weight) {
		super(weight);
		this.probs = inferredProbs;
		this.doc = doc;
	}

	@Override
	protected double doCalc(String word) {
		String filePath = doc.f.getPath();

		// Test the first topic first to see if the word even exists in the
		// topic models wordmap
		double score = probs.getWordTopicProb(word, 0)
				* probs.getTopicDocProb(0, filePath);
		if (score == -1) { // The word isn't in the wordmap
			return 0;
		}
		for (int topic = 1; topic < probs.getNumTopics(); topic++) {
			score = probs.getWordTopicProb(word, topic);
		}
		return score;
	}
}
