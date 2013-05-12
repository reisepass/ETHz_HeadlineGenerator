package ethz.nlp.headgen.prob;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;

import ethz.nlp.headgen.Doc;

public class NgramSimple implements NGramProbs {
	protected TreeMap<ArrayList<String>, Double> ngramFreq;
	protected int n = 2;

	public NgramSimple(TreeMap<ArrayList<String>, Double> inNgrams) {
		ngramFreq = inNgrams;
		n = inNgrams.firstEntry().getKey().size();

	}

	public NgramSimple(TreeMap<ArrayList<String>, Double> inNgrams, int N) {
		ngramFreq = inNgrams;
		this.n = N;
	}

	public TreeMap<ArrayList<String>, Double> filterNgrams(Doc doc) {

		return filterNgrams(doc,
				(Comparator<ArrayList<String>>) ngramFreq.comparator());
	}

	/**
	 * This will only work with very large corpus of topics because we will only
	 * included bigrams from the article that exactly match one in the query
	 * Document
	 * 
	 * 
	 */
	public TreeMap<ArrayList<String>, Double> filterNgrams(Doc doc,
			Comparator<ArrayList<String>> comp) { // TODO paramaterize
													// Comparator in this method

		Annotation docAno = doc.getAno();

		List<CoreMap> sentences = docAno.get(SentencesAnnotation.class);
		TreeMap<ArrayList<String>, Double> filteredNgram = new TreeMap<ArrayList<String>, Double>(
				comp);
		ArrayList<ArrayList<String>> unUsed = new ArrayList<ArrayList<String>>();
		Queue<String> lastWords = new LinkedBlockingQueue<String>();
		String curWord = null;

		for (CoreMap sentence : sentences) {
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				if (curWord != null)
					lastWords.add(curWord);
				curWord = token.get(TextAnnotation.class);

				if (lastWords.size() == n) {
					String[] tmp = new String[n];
					lastWords.toArray(tmp);
					ArrayList<String> ngram = new ArrayList<String>(
							Arrays.asList(tmp));
					if (ngramFreq.containsKey(ngram))
						filteredNgram.put(ngram, ngramFreq.get(ngram));
					else
						unUsed.add(ngram);
					lastWords.poll();

				}
			}
		}

		return filteredNgram;
	}

	@Override
	public double getProb(String... words) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getProb(List<String> words) {
		ArrayList<String> ngram = new ArrayList<String>();
		for (String s : words) {
			ngram.add(s.toLowerCase());
		}

		if (ngramFreq.containsKey(ngram)) {
			return ngramFreq.get(ngram);
		} else {
			return -1;
		}
	}

}
