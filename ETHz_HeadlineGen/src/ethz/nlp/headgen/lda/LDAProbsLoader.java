package ethz.nlp.headgen.lda;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;

import ethz.nlp.headgen.util.ConfigFactory;

public class LDAProbsLoader {
	public static final String WORD_TOPIC_SUFFIX = "-final.phi";
	public static final String TOPIC_DOC_SUFFIX = "-final.theta";

	private LDAProbsLoader() {
	}

	// TODO: Need to load the word map into an array to associate words with
	// their IDs
	public static LDAProbs loadLDAProbs(File modelDir) throws IOException {
		return loadLDAProbs(modelDir, "model");
	}

	public static LDAProbs loadLDAProbs(File modelDir, String modelName)
			throws IOException {
		LDAProbsImpl ldaProbs = new LDAProbsImpl();
		String[] wordList = getWordList(modelDir);
		loadWordTopicProbs(ldaProbs, new File(modelDir, modelName
				+ WORD_TOPIC_SUFFIX), wordList);
		loadTopicDocProbs(ldaProbs, new File(modelDir, modelName
				+ TOPIC_DOC_SUFFIX));
		return ldaProbs;
	}

	private static String[] getWordList(File modelDir) throws IOException {
		BufferedReader br = null;
		int length, index;
		String line;
		String[] vals;
		String[] words;
		try {
			br = new BufferedReader(new FileReader(new File(modelDir,
					"wordmap.txt")));
			length = Integer.parseInt(br.readLine());
			words = new String[length];
			while ((line = br.readLine()) != null) {
				vals = line.split(" ");
				index = Integer.parseInt(vals[1]);
				words[index] = vals[0];
			}
		} finally {
			if (br != null) {
				br.close();
			}
		}
		return words;
	}

	// FORMAT: Each line is a topic, each column is a word in the vocabulary
	private static void loadWordTopicProbs(LDAProbsImpl ldaProbs, File file,
			String[] wordList) throws IOException {
		BufferedReader br = null;
		String line;
		String[] vals;
		int topic = 0;
		try {
			br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {
				vals = line.split(" ");
				for (int i = 0; i < vals.length; i++) {
					ldaProbs.putWordTopic(wordList[i], topic,
							Double.parseDouble(vals[i]));
				}
				topic++;
			}
		} finally {
			if (br != null) {
				br.close();
			}
		}

	}

	private static void loadTopicDocProbs(LDAProbsImpl ldaProbs, File file) {
		// TODO: Need a way to link the document # to the doc name
	}

	private static class LDAProbsImpl implements LDAProbs {
		private ConcurrentRadixTree<Double> wordTopicProbs = new ConcurrentRadixTree<Double>(
				new DefaultCharArrayNodeFactory());
		private ConcurrentRadixTree<Double> topicDocProbs = new ConcurrentRadixTree<Double>(
				new DefaultCharArrayNodeFactory());

		protected void putWordTopic(String word, int topic, Double prob) {
			wordTopicProbs.put(getKey(word, "" + topic), prob);
		}

		protected void putTopicDoc(int topic, String doc, Double prob) {
			topicDocProbs.put(getKey("" + topic, doc), prob);
		}

		private String getKey(String desired, String given) {
			return desired + ":" + given;
		}

		@Override
		public double getWordTopicProb(String word, int topic) {
			return wordTopicProbs.getValueForExactKey(getKey(word, "" + topic));
		}

		@Override
		public double getTopicDocProb(int topic, String doc) {
			return topicDocProbs.getValueForExactKey(getKey("" + topic, doc));
		}
	}

	public static void main(String[] args) throws IOException {
		LDAConfig config = ConfigFactory.loadConfiguration(LDAConfig.class,
				"./conf/lda-baseModel.conf");
		LDAProbs probs = loadLDAProbs(new File(config.getModelDir()));
		System.out.println("STOP");
	}
}
