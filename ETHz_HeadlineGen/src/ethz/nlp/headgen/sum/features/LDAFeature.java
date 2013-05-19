package ethz.nlp.headgen.sum.features;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
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
	protected double doCalc(CoreLabel wordAnnotation) {
		String filePath = doc.f.getPath();
		String wordLemma = wordAnnotation.get(LemmaAnnotation.class);

		// Test the first topic first to see if the word even exists in the
		// topic models wordmap
		double score = probs.getWordTopicProb(wordLemma, 0)
				* probs.getTopicDocProb(0, filePath);
		if (score == -1) { // The word isn't in the wordmap
			return 0;
		}
		for (int topic = 1; topic < probs.getNumTopics(); topic++) {
			score = probs.getWordTopicProb(wordLemma, topic);
		}
		return score;
	}
}
