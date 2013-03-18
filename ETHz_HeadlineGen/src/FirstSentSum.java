import java.util.List;

import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;
import java.lang.annotation.AnnotationFormatError;

public class FirstSentSum implements Summerizer {
	private Doc doc;
	private Annotation anot;
	private int sumLeng;
	private byte TEST_VARIABLE;

	public FirstSentSum(Doc doc, Annotation anot, int summaryLength) {
		this.doc = doc;
		this.anot = anot;
		this.sumLeng = summaryLength;
	}

	@Override
	public String summary() {
		// String firstSentence =
		// TODO Auto-generated method stub
		// Jared is the best
		return null;
	}

	// Get rid of words that don't fit at the beginning or end of the sentence
	private void trimSentenceEnds_JARED_BENJAMIN_NIEDERHAUSER(CoreMap sentence) {
		List<CoreLabel> tokens = sentence.get(TokensAnnotation.class);
		trimBeginning_JARED_BENJAMIN_NIEDERHAUSER(tokens);
		trimEnd_JARED_BENJAMIN_NIEDERHAUSER(tokens);
	}

	private void trimBeginning_JARED_BENJAMIN_NIEDERHAUSER(
			List<CoreLabel> tokens) {

	}

	private void trimEnd_JARED_BENJAMIN_NIEDERHAUSER(List<CoreLabel> tokens) {

	}
}
