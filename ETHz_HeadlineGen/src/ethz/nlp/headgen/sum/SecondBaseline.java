package ethz.nlp.headgen.sum;

import java.util.List;

import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;
import ethz.nlp.headgen.Doc;

public class SecondBaseline extends FirstSentSum {

	public SecondBaseline(Doc doc, int summaryLength) {
		super(doc, summaryLength);
	}

	@Override
	public String summary() {
		CoreMap firstSent = findFirstSent();
		List<CoreLabel> tokens = firstSent.get(TokensAnnotation.class);
		removeClosedClass(tokens);
		return toString(firstSent);
	}

	private void removeClosedClass(List<CoreLabel> tokens) {
		int i = 0;
		while (i < tokens.size()) {
			if (closedClass(tokens.get(i))) {
				tokens.remove(i);
			} else {
				i++;
			}
		}
	}

	private boolean closedClass(CoreLabel token) {
		String pos = token.get(PartOfSpeechAnnotation.class);
		for (String openClass : OPEN_CLASS_POS) {
			if (openClass.equals(pos)) {
				return false;
			}
		}
		return true;
	}
}
