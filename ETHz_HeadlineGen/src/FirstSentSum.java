import edu.stanford.nlp.pipeline.Annotation;

public class FirstSentSum implements Summerizer {
	private Doc doc;
	private Annotation anot;
	private int sumLeng;
	private String derp;
	public FirstSentSum(Doc doc, Annotation anot, int summaryLength) {
		this.doc = doc;
		this.anot = anot;
		this.sumLeng = summaryLength;
	}

	@Override
	public String summary() {
		// String firstSentence =
		// TODO Auto-generated method stub
		
		derp = derp+" herp";
		return null;
	}
	
	public String trimPastComma(String inp){
			return "";
		
	}

	public void test() {

	}

}
