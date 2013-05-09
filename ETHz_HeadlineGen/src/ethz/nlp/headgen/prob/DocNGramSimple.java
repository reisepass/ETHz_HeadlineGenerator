package ethz.nlp.headgen.prob;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

import com.googlecode.concurrenttrees.common.KeyValuePair;

import ethz.nlp.headgen.data.WordCount;
import ethz.nlp.headgen.data.WordCountTree;
import ethz.nlp.headgen.lda.RawToLDA;

public class DocNGramSimple implements DocNGramProbs {
	private int n;

	public DocNGramSimple(int n) {
		this.n = n;
	}

	@Override
	public NGramProbs getProbs(String docText) {
		TreeMap<ArrayList<String>, Double> ngrams = new TreeMap<ArrayList<String>, Double>(
				new Comparator<ArrayList<String>>() {
					@Override
					public int compare(ArrayList<String> o1,
							ArrayList<String> o2) {
						return o1.toString().compareTo(o2.toString());
					}
				});

		// Clean the text by converting it into the LDA format
		String cleanedText = RawToLDA.convert(docText);

		// Add the cleaned ngrams to the ngram tree
		String[] words = cleanedText.split(" ");
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

		return new NgramLightFilter(ngrams, n);
	}

	private WordCountTree getCounts(String[] words) {
		WordCountTree tree = new WordCountTree();
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

	public static void main(String[] args) {
		DocNGramSimple simple = new DocNGramSimple(2);
		NGramProbs probs = simple
				.getProbs("My name is Herp Derp Niederhauser.  It is Herp Derp");
		List<String> test = new ArrayList<String>(2);
		test.add("herp");
		test.add("derp");
		// TODO: There seems to be an issue if the words are not all in lower
		// case
		double val = probs.getProb(test);
		System.out.println(val);
	}
}
