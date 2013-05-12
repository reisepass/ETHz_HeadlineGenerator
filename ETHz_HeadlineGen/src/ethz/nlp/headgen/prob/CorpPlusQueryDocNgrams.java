package ethz.nlp.headgen.prob;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import ethz.nlp.headgen.Doc;

public class CorpPlusQueryDocNgrams extends NgramLightFilter implements
		NGramProbs {
	protected double qDocNgramWeights;
	
	
	public CorpPlusQueryDocNgrams(TreeMap<ArrayList<String>, Double> inNgrams) {
		super(inNgrams);
		qDocNgramWeights=1.5;
		// TODO Auto-generated constructor stub
	}

	public CorpPlusQueryDocNgrams(TreeMap<ArrayList<String>, Double> inNgrams,double queryDocNgramWeights ) {
		super(inNgrams);
		qDocNgramWeights=queryDocNgramWeights;
		// TODO Auto-generated constructor stub
	}
	
	public CorpPlusQueryDocNgrams(TreeMap<ArrayList<String>, Double> inNgrams,
			int N) {
		super(inNgrams, N);
		qDocNgramWeights=1.5;
		// TODO Auto-generated constructor stub
	}

	public CorpPlusQueryDocNgrams(TreeMap<ArrayList<String>, Double> inNgrams,
			int N, int filterCode) {
		super(inNgrams, N, filterCode);
		
		qDocNgramWeights=1.5;
		// TODO Auto-generated constructor stub
	}
	public CorpPlusQueryDocNgrams(TreeMap<ArrayList<String>, Double> inNgrams,
			int N, int filterCode, double queryDocNgramWeights) {
		super(inNgrams, N, filterCode);
		qDocNgramWeights=queryDocNgramWeights;
		// TODO Auto-generated constructor stub
	}

	public TreeMap<ArrayList<String>, Double> filterNgrams(Doc doc,
			Comparator<ArrayList<String>> comp) {
	
		TreeMap<ArrayList<String>, Double> outNgram = new TreeMap<ArrayList<String>, Double>(
				comp);
		for (Map.Entry<ArrayList<String>, Double> ele : this.ngramFreq
				.entrySet()) {
			if (filterType == 1) {
				if (doc.containsOneOrMore(ele.getKey())) {
					outNgram.put(ele.getKey(), ele.getValue());
				}
			} else {
				if (doc.containsAll(ele.getKey())) {
					outNgram.put(ele.getKey(), ele.getValue());
				}
			}
		}
		
		
		DocNGramProbs ngramMaker = new DocNGramSimple(n);
		TreeMap<ArrayList<String>, Double> topicNgrams = ngramMaker.getProbs(doc.cont);
		TreeMap<ArrayList<String>, Double> docNgrams = new NgramLightFilter(topicNgrams,n).filterNgrams(doc);
		for(Map.Entry<ArrayList<String>, Double> ele : docNgrams.entrySet()){
			outNgram.put(ele.getKey(),ele.getValue()*qDocNgramWeights);
		}

		
		return outNgram;
	}

}
