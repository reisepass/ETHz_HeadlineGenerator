package ethz.nlp.headgen.sum;

import java.util.Iterator;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;
import ethz.nlp.headgen.Doc;
import ethz.nlp.headgen.Extractor;

public class NeFreqBasedSum extends FirstSentSum implements Summerizer {
	protected Extractor extr;

	public NeFreqBasedSum(Doc doc,int summaryLength) {
		super(doc, summaryLength);
		extr = new Extractor(doc.annotation);
		extr.runAll();
	} 
	
	public NeFreqBasedSum(Doc doc, Annotation anot, int summaryLength) {
		super(doc, summaryLength);
		extr = new Extractor(anot);
		extr.runAll();
	}

	/*
	 * Currently i just look for the sentence which as the most of the top
	 * ranking name entities Currently not accounting for ties Not sure how many
	 * of the top NE we want to include.
	 * 
	 * 
	 * Ok if i were to collect all the sentences that have atleast 2 of the top
	 * NE. How would i choose the best ????
	 */
	private CoreMap findImpSent() {
		String[] topNE = extr.rankedNameEntityCount(5);
		Iterator<CoreMap> sentItr = anot.get(SentencesAnnotation.class)
				.iterator();

		String bestSentSofar = "";
		CoreMap best = null;
		int numTopIncluded = 0;
		while (sentItr.hasNext()) {
			CoreMap curSent = sentItr.next();
			String curText = curSent.get(TextAnnotation.class);

			int curCount = 0;
			for (String str : topNE) {
				if (curText.contains(str))
					curCount++;
			}
			if (curCount > numTopIncluded) {
				bestSentSofar = curText;
				numTopIncluded = curCount;
				best = curSent;
			}
		}
		return best;
	}

	/*
	 * So at the moment this class simply returns the sentence which has the
	 * most NE in which are also in the TOP_MOST_FRequent NE list. I noticed
	 * that several sentences tend to tie for first place. So we should have a
	 * better way of choosing among those Maybe we could even construct one
	 * sentence out of several of the top sentences.
	 * 
	 * 
	 * 
	 * 
	 * (non-Javadoc)
	 * 
	 * @see ethz.nlp.headgen.sum.FirstSentSum#summary()
	 */

	@Override
	public String summary() {

		CoreMap topSent = findImpSent();
		if (topSent == null) {
			return "";
			// should not happen if doc is non empty
		}
		trimSentenceEnds(topSent);
		removePoSInList(topSent.get(TokensAnnotation.class), FLUFF_POS);
		removeInternalDependentClause(topSent.get(TokensAnnotation.class));
		removePoSNotInList(topSent.get(TokensAnnotation.class), OPEN_CLASS_POS);

		String out = toString(topSent);
		out = fixCapitalization(out);
		out = fixWhiteSpace(out);
		return out;
	}

}
