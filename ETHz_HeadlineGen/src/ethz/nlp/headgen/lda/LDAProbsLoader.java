package ethz.nlp.headgen.lda;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;

import ethz.nlp.headgen.io.SerializableWrapper;
import ethz.nlp.headgen.util.ConfigFactory;

public class LDAProbsLoader {
	public static final String MODEL_PROBS_100_SAVE_PATH = "data/model-100_lda_probs";
	public static final String MODEL_PROBS_500_SAVE_PATH = "data/model-500_lda_probs";
	public static final String MODEL_PROBS_1000_SAVE_PATH = "data/model-1000_lda_probs";
	public static final String INFERRED_PROBS_SAVE_PATH = "data/inf_lda_probs";
	public static final String WORD_TOPIC_SUFFIX = ".phi";
	public static final String TOPIC_DOC_SUFFIX = ".theta";
	public static final String WORDMAP = "wordmap.txt";
	public static final String DOCMAP = "docmap.txt";
	public static final String COLLAPSED = "LDA.dat";

	private LDAProbsLoader() {
	}

	public static LDAProbs loadLDAProbsCollapsed(LDAEstimatorConfig estConf)
			throws IOException {
		File modelDir = new File(estConf.getModelDir());

		System.err.println("Loading word list");
		String[] wordList = getWordList(modelDir);

		System.err.println("Loading collapsed doc list");
		LDAProbsImpl ldaProbs = new LDAProbsImpl(getDocListCollapsed(modelDir));

		System.err.println("Loading word/topic probs");
		loadWordTopicProbs(ldaProbs, new File(modelDir, estConf.getModel()
				+ WORD_TOPIC_SUFFIX), wordList);

		System.err.println("Loading topic/doc probs");
		loadTopicDocProbs(ldaProbs, new File(modelDir, estConf.getModel()
				+ TOPIC_DOC_SUFFIX));
		return ldaProbs;
	}

	public static LDAProbs loadLDAProbs(LDAEstimatorConfig estConf)
			throws IOException {
		File modelDir = new File(estConf.getModelDir());

		String[] wordList = getWordList(modelDir);
		LDAProbsImpl ldaProbs = new LDAProbsImpl(getDocList(modelDir));
		loadWordTopicProbs(ldaProbs, new File(modelDir, estConf.getModel()
				+ WORD_TOPIC_SUFFIX), wordList);
		loadTopicDocProbs(ldaProbs, new File(modelDir, estConf.getModel()
				+ TOPIC_DOC_SUFFIX));
		return ldaProbs;
	}

	public static LDAProbs loadLDAProbs(LDAEstimatorConfig estConf,
			LDAInferenceConfig infConf) throws IOException {
		File modelDir = new File(estConf.getModelDir()), infDir = new File(
				infConf.getModelDir());

		String[] wordList = getWordList(modelDir);
		LDAProbsImpl ldaProbs = new LDAProbsImpl(getDocList(infDir));
		loadWordTopicProbs(ldaProbs, new File(infDir, infConf.getDataFile()
				+ "model-final" + WORD_TOPIC_SUFFIX), wordList);
		loadTopicDocProbs(ldaProbs, new File(infDir, infConf.getDataFile()
				+ "model-final" + TOPIC_DOC_SUFFIX));
		return ldaProbs;
	}

	private static String[] getDocListCollapsed(File modelDir)
			throws IOException {
		BufferedReader br = null;
		int index = 0;
		String[] docs;
		try {
			br = new BufferedReader(new FileReader(
					new File(modelDir, COLLAPSED)));
			docs = new String[Integer.parseInt(br.readLine())];
			while (br.readLine() != null) {
				docs[index] = "" + index++;
			}
		} finally {
			if (br != null) {
				br.close();
			}
		}
		return docs;
	}

	private static String[] getDocList(File modelDir) throws IOException {
		BufferedReader br = null;
		int index = 0;
		String line;
		String[] docs;
		try {
			br = new BufferedReader(new FileReader(new File(modelDir, DOCMAP)));
			docs = new String[Integer.parseInt(br.readLine())];
			while ((line = br.readLine()) != null) {
				docs[index++] = line;
			}
		} finally {
			if (br != null) {
				br.close();
			}
		}
		return docs;
	}

	private static String[] getWordList(File modelDir) throws IOException {
		BufferedReader br = null;
		int length, index;
		String line;
		String[] vals;
		String[] words;
		try {
			br = new BufferedReader(new FileReader(new File(modelDir, WORDMAP)));
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
				System.err.println("Loading topic " + topic);
				vals = line.split(" ");
				for (int i = 0; i < vals.length; i++) {
					ldaProbs.putWordTopic(i, topic,
							Double.parseDouble(vals[i]), wordList);
				}
				topic++;
			}
			ldaProbs.numTopics = topic;
		} finally {
			if (br != null) {
				br.close();
			}
		}
	}

	// FORMAT: Each line is a document and each column is a topic
	private static void loadTopicDocProbs(LDAProbsImpl ldaProbs, File file)
			throws IOException {
		BufferedReader br = null;
		String line;
		String[] vals;
		int doc = 0;
		try {
			br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {
				System.err.println("Loading doc " + doc);
				vals = line.split(" ");
				for (int i = 0; i < vals.length; i++) {
					ldaProbs.putTopicDoc(i, doc, Double.parseDouble(vals[i]));
				}
				doc++;
			}
		} finally {
			if (br != null) {
				br.close();
			}
		}
	}

	@SuppressWarnings("serial")
	public static class LDAProbsImpl implements LDAProbs {
		private ConcurrentRadixTree<Double> wordTopicProbs = new ConcurrentRadixTree<Double>(
				new DefaultCharArrayNodeFactory());
		private ConcurrentRadixTree<Double> topicDocProbs = new ConcurrentRadixTree<Double>(
				new DefaultCharArrayNodeFactory());
		private String[] docList;
		private int numTopics;

		public LDAProbsImpl() {
		}

		public LDAProbsImpl(String[] docList) {
			this.docList = docList;
		}

		protected void putWordTopic(int word, int topic, Double prob,
				String[] wordList) {
			wordTopicProbs.put(getKey(wordList[word], "" + topic), prob);
		}

		protected void putTopicDoc(int topic, int doc, Double prob) {
			topicDocProbs.put(getKey("" + topic, docList[doc]), prob);
		}

		private String getKey(String desired, String given) {
			return desired + ":" + given;
		}

		@Override
		public double getWordTopicProb(String word, int topic) {
			Double val = wordTopicProbs.getValueForExactKey(getKey(word, ""
					+ topic));
			return val == null ? -1 : val;
		}

		@Override
		public double getTopicDocProb(int topic, String doc) {
			if (topicDocProbs.getValueForExactKey(getKey("" + topic, doc)) != null)
				return topicDocProbs
						.getValueForExactKey(getKey("" + topic, doc));
			else
				return 0;
		}

		@Override
		public String[] getDocList() {
			return docList;
		}

		@Override
		public int getNumTopics() {
			return numTopics;
		}

		@Override
		public int getMostLikelyTopic(String doc) {
			double topProb = -1, prob;
			int maxIndex = -1;
			for (int i = 0; i < numTopics; i++) {
				prob = getTopicDocProb(i, doc);
				if (prob > topProb) {
					maxIndex = i;
					topProb = prob;
				}
			}
			return maxIndex;
		}
	}

	public static void main(String[] args) throws IOException {
		LDAEstimatorConfig estConf = ConfigFactory.loadConfiguration(
				LDAEstimatorConfig.class, LDAEstimatorConfig.DEFAULT);
		LDAInferenceConfig infConf = ConfigFactory.loadConfiguration(
				LDAInferenceConfig.class, LDAInferenceConfig.DEFAULT);
		LDAProbs probs = loadLDAProbsCollapsed(estConf);

		SerializableWrapper sw = new SerializableWrapper(probs);
		sw.save(MODEL_PROBS_100_SAVE_PATH);

		probs = SerializableWrapper.readObject(MODEL_PROBS_100_SAVE_PATH);

		// for (String doc : probs.getDocList()) {
		// System.out.println(doc + ": " + probs.getMostLikelyTopic(doc));
		// }

		// LDAConfig config = ConfigFactory.loadConfiguration(LDAConfig.class,
		// "./conf/lda-baseModel.conf");
		// LDAProbs probs = loadLDAProbs(new File(config.getModelDir()));
		// DocCluster cluster = new DocCluster(probs);
		// List<String>[] clusters = cluster.getDocClusters();
		// int count = 1;
		// int topic = 0;
		// for (List<String> c : clusters) {
		// if (c.size() > 10) {
		// topic = count - 1;
		// }
		// System.out.println("Cluster " + (count++) + " has " + c.size()
		// + " docs");
		// }
		//
		// System.err.println("Getting cluster ngram probs");
		// TreeMap<ArrayList<String>, Double> ngrams = cluster
		// .getClusterNgramProbs(topic, 2);
		// for (Entry<ArrayList<String>, Double> e : ngrams.entrySet()) {
		// System.out.println("Key: " + e.getKey());
		// System.out.println("Value: " + e.getValue());
		// break;
		// }
	}
}
