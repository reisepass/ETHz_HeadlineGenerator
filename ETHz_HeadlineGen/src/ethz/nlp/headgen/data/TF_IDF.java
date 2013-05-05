package ethz.nlp.headgen.data;

import ethz.nlp.headgen.Doc;

public class TF_IDF {
	private TF_IDF() {
	}

	public static double calc(String word, Doc d, CorpusCounts c) {
		double docCount = d.wordCounts.getValueForExactKey(word).getCount();
		double maxCount = d.wordCounts.getMax();
		double numDocs = c.getNumDocs();
		double docAppearanceCount = c.getDocAppearanceCounts()
				.getValueForExactKey(word).getCount();
		return (docCount / maxCount)
				* (Math.log(numDocs / (1 + docAppearanceCount)));
	}
}
