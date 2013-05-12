package ethz.nlp.headgen.prob;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import ethz.nlp.headgen.Doc;

public class NoFilterAddTestCorpus extends CorpPlusQueryDocNgrams implements
		NGramProbs {

	public NoFilterAddTestCorpus(TreeMap<ArrayList<String>, Double> inNgrams) {
		super(inNgrams);
		// TODO Auto-generated constructor stub
	}

	public NoFilterAddTestCorpus(TreeMap<ArrayList<String>, Double> inNgrams,
			double queryDocNgramWeights) {
		super(inNgrams, queryDocNgramWeights);
		// TODO Auto-generated constructor stub
	}

	public NoFilterAddTestCorpus(TreeMap<ArrayList<String>, Double> inNgrams,
			int N) {
		super(inNgrams, N);
		// TODO Auto-generated constructor stub
	}

	public NoFilterAddTestCorpus(TreeMap<ArrayList<String>, Double> inNgrams,
			int N, int filterCode) {
		super(inNgrams, N, filterCode);
		// TODO Auto-generated constructor stub
	}

	public NoFilterAddTestCorpus(TreeMap<ArrayList<String>, Double> inNgrams,
			int N, int filterCode, double queryDocNgramWeights) {
		super(inNgrams, N, filterCode, queryDocNgramWeights);
		// TODO Auto-generated constructor stub
	}

	public TreeMap<ArrayList<String>, Double> filterNgrams(Doc doc,
			Comparator<ArrayList<String>> comp) {
	
		TreeMap<ArrayList<String>, Double> outNgram = this.ngramFreq;
		
		
		DocNGramProbs ngramMaker = new DocNGramSimple(n);
		TreeMap<ArrayList<String>, Double> topicNgrams = ngramMaker.getProbs(doc.cont);
		TreeMap<ArrayList<String>, Double> docNgrams = new NgramLightFilter(topicNgrams,n).filterNgrams(doc);
		for(Map.Entry<ArrayList<String>, Double> ele : docNgrams.entrySet()){
			outNgram.put(ele.getKey(),ele.getValue()*qDocNgramWeights);
		}

		
		return outNgram;
	}

}
