package ethz.nlp.headgen.prob;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeMap;

import com.googlecode.concurrenttrees.common.KeyValuePair;

import ethz.nlp.headgen.data.WordCount;
import ethz.nlp.headgen.data.WordCountTree;

public class DocNGramSimple implements DocNGramProbs {
	public static final int DEFAULT_NGRAMS_LENGTH = 2;

	private int n;

	public DocNGramSimple() {
		this(DEFAULT_NGRAMS_LENGTH);
	}

	public DocNGramSimple(int n) {
		this.n = n;
	}

	@Override
	public TreeMap<ArrayList<String>, Double> getProbs(String docText) {
		TreeMap<ArrayList<String>, Double> ngrams = new TreeMap<ArrayList<String>, Double>(
				new Comparator<ArrayList<String>>() {
					@Override
					public int compare(ArrayList<String> o1,
							ArrayList<String> o2) {
						return o1.toString().compareTo(o2.toString());
					}
				});

		// Add the cleaned ngrams to the ngram tree
		String[] words = docText.split(" ");
		WordCountTree ngramCounts = getCounts(words);
		double val;
		for (KeyValuePair<WordCount> pair : ngramCounts
				.getKeyValuePairsForKeysStartingWith("")) {
			val = ((double) pair.getValue().getCount())
					/ ngramCounts.getTotal();

			ngrams.put(
					new ArrayList<String>(Arrays.asList(pair.getKey()
							.toString().split(":"))), val);
		}

		return ngrams;
	}

	private WordCountTree getCounts(String[] words) {	
		WordCountTree tree = new WordCountTree();
		if(words==null||words.length<n)
			return tree; 
		
		String[] ngramWords = new String[n];

		for (int i = 0; i < n; i++) {
			ngramWords[i] = words[i];
		}

		int index = n, ptr = 0;
		tree.put(combineWords(ngramWords, ptr));

		while (index < words.length) {
			ngramWords[ptr++] = words[index++];
			ptr %= n;
			tree.put(combineWords(ngramWords, ptr));
		}
		return tree;
	}

	private String combineWords(String[] ngramWords, int ptr) {
		StringBuilder sb = new StringBuilder();
		int current = ptr;
		do {
			sb.append(ngramWords[current++] + ":");
			current %= ngramWords.length;
		} while (current != ptr);
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

}
