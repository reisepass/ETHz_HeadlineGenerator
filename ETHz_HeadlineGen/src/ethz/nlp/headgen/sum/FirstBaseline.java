package ethz.nlp.headgen.sum;

import ethz.nlp.headgen.Doc;

public class FirstBaseline extends FirstSentSum {

	public FirstBaseline(Doc doc, int summaryLength) {
		super(doc, summaryLength);
	}

	@Override
	public String summary() {
		return getFirstSent();
	}
}
