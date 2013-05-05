package ethz.nlp.headgen.util;

import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;

public final class StopWords {
	public static final String[] STOP_WORDS = { "a", "able", "about", "across",
			"after", "all", "almost", "also", "am", "among", "an", "and",
			"any", "are", "as", "at", "be", "because", "been", "but", "by",
			"can", "cannot", "could", "dear", "did", "do", "does", "either",
			"else", "ever", "every", "for", "from", "get", "got", "had", "has",
			"have", "he", "her", "hers", "him", "his", "how", "however", "i",
			"if", "in", "into", "is", "it", "its", "just", "least", "let",
			"like", "likely", "may", "me", "might", "most", "must", "my",
			"neither", "no", "nor", "not", "of", "off", "often", "on", "only",
			"or", "other", "our", "own", "rather", "said", "say", "says",
			"she", "should", "since", "so", "some", "than", "that", "the",
			"their", "them", "then", "there", "these", "they", "this", "tis",
			"to", "too", "twas", "us", "wants", "was", "we", "were", "what",
			"when", "where", "which", "while", "who", "whom", "why", "will",
			"with", "would", "yet", "you", "your" };

	private static ConcurrentRadixTree<Boolean> searchTree = null;

	public static boolean isStopWord(String word) {
		if (word == null) {
			return false;
		}

		if (searchTree == null) {
			initSearchTree();
		}

		return searchTree.getValueForExactKey(word.toLowerCase()) != null;
	}

	private static void initSearchTree() {
		searchTree = new ConcurrentRadixTree<Boolean>(
				new DefaultCharArrayNodeFactory());
		for (String s : STOP_WORDS) {
			searchTree.put(s, Boolean.TRUE);
		}
	}

	public static void main(String[] args) {
		String[] testWords = { "tHe", "a", "haberdasher" };

		for (String s : testWords) {
			System.out.println(s + ": " + isStopWord(s));
		}
	}
}
