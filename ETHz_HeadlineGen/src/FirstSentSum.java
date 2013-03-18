import java.util.List;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;

public class FirstSentSum implements Summerizer {
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
		//Jared is the best
		return null;
	}
	private CoreMap getFirstSent(){
		CoreMap sentence = anot.get(SentencesAnnotation.class).get(0);
		return sentence;
	}
	public void test() {

	}

}
