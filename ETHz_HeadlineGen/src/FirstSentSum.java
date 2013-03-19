import java.util.List;

import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;

public class FirstSentSum implements Summerizer {
	private static final String[] START_POS = { "NNP", "NN" };
	private static final String[] END_POS = { "NNP", "NN" };
	
	private Doc doc;
	private Annotation anot;
	private int sumLeng;
	private String firstSent;

	public FirstSentSum(Doc doc, Annotation anot, int summaryLength) {
		this.doc = doc;
		this.anot = anot;
		this.sumLeng = summaryLength;
	}

	@Override
	public String summary() {
		CoreMap firstSentence = getFirstSent();
		// TODO Auto-generated method stub
		// Jared is the best
		return null;
	}

	private CoreMap getFirstSent() {
		CoreMap sentence = anot.get(SentencesAnnotation.class).get(0);
		return sentence;
	}

	// Get rid of words that don't fit at the beginning or end of the sentence
	private void trimSentenceEnds(CoreMap sentence) {
		List<CoreLabel> tokens = sentence.get(TokensAnnotation.class);
		trimBeginning(tokens);
		trimEnd(tokens);
	}

	private void trimBeginning(List<CoreLabel> tokens) {
		while (tokens.size() > 0) {
			String pos = tokens.get(0).get(PartOfSpeechAnnotation.class);

			for (String s : START_POS) {
				if (s.equals(pos)) {
					return; // Suitable starting token found
				}
			}

			// Remove the first token
			tokens.remove(0);
		}
	}

	private void trimEnd(List<CoreLabel> tokens) {
		while (tokens.size() > 0) {
			String pos = tokens.get(tokens.size() - 1).get(
					PartOfSpeechAnnotation.class);

			for (String s : END_POS) {
				if (s.equals(pos)) {
					return; // Suitable starting token found
				}
			}

			// Remove the last token
			tokens.remove(tokens.size() - 1);
		}
	}
}
