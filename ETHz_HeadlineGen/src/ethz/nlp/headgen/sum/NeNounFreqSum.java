package ethz.nlp.headgen.sum;

import java.util.Iterator;
import java.util.Map;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;
import ethz.nlp.headgen.Doc;
import ethz.nlp.headgen.StrPair;

public class NeNounFreqSum extends NeFreqBasedSum {

	public NeNounFreqSum(Doc doc, Annotation anot, int summaryLength) {
		super(doc, anot, summaryLength);
		// TODO Auto-generated constructor stub
	}

	public NeNounFreqSum(Doc doc, int summaryLength) {
		super(doc, summaryLength);
		// TODO Auto-generated constructor stub
	}
	
	protected CoreMap findImpSent() {
		
		Map<StrPair, Integer> bioC=extr.getBiNNSentCounts();
		StrPair[] tmplist=extr.getRankingFromPairMap(5,bioC);
		int a=1;
		String[] topNN = extr.rankedNounCounts(5);
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

}
