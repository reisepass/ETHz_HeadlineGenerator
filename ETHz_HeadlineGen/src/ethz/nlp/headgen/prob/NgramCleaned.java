package ethz.nlp.headgen.prob;

import java.util.ArrayList;
import java.util.TreeMap;

import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import ethz.nlp.headgen.Doc;
import ethz.nlp.headgen.sum.FirstSentSum;
  
public class NgramCleaned extends NgramSimple implements NGramProbs {

	public NgramCleaned(TreeMap<ArrayList<String>, Double> inNgrams) {
		super(inNgrams);
		// TODO Auto-generated constructor stub
	}

	public NgramCleaned(TreeMap<ArrayList<String>, Double> inNgrams, int N) {
		super(inNgrams, N);
		// TODO Auto-generated constructor stub
		
	}

		// This class should only get used if the corpus it uses in the TreeMap was also cleaned in the same way
	public TreeMap<ArrayList<String>,Double> filterNgrams(Doc doc){
		FirstSentSum.removePoSNotInList(doc.getAno().get(TokensAnnotation.class),FirstSentSum.CLEAN_WORDS);
		return super.filterNgrams(doc);  //TODO make sure that removePoSNotInList works as expected 
	}
	
}
