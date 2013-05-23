package ethz.nlp.headgen.lda;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import ethz.nlp.headgen.io.SerializableWrapper;
import ethz.nlp.headgen.prob.DocNGramSimple;
import ethz.nlp.headgen.prob.RawToNGram;

@SuppressWarnings("serial")
public class DocClusterFromCollapsed implements DocCluster {
	private TreeMap<ArrayList<String>, Double>[] clusterNgrams;

	public DocClusterFromCollapsed() {
	}

	public DocClusterFromCollapsed(
			TreeMap<ArrayList<String>, Double>[] clusterNgrams) {
		this.clusterNgrams = clusterNgrams;
	}

	public TreeMap<ArrayList<String>, Double> getClusterNgramProbs(int cluster)
			throws IOException {
		return clusterNgrams[cluster];
	}

	public static DocCluster loadDocCluster(String collapsedDocsPath,
			String thetaProbsPath) throws IOException {
		return loadDocCluster(new File(collapsedDocsPath), new File(
				thetaProbsPath));
	}

	public static DocCluster loadDocCluster(File collapsedDocsFile,
			File thetaProbsFile) throws IOException {
		int numDocs = getNumDocs(collapsedDocsFile);
		System.out.println("Num docs: " + numDocs);
		int numTopics = getNumTopics(thetaProbsFile);
		System.out.println("Num topics: " + numTopics);
		String[] docTexts = loadDocTexts(numDocs, collapsedDocsFile);
		List<String>[] docClusters = genClusters(docTexts, thetaProbsFile,
				numTopics);
		return new DocClusterFromCollapsed(genNGrams(docClusters, numTopics));
	}

	@SuppressWarnings("unchecked")
	private static TreeMap<ArrayList<String>, Double>[] genNGrams(
			List<String>[] docClusters, int numTopics) {
		TreeMap<ArrayList<String>, Double>[] clusterNgrams = new TreeMap[numTopics];
		for (int cluster = 0; cluster < clusterNgrams.length; cluster++) {
			System.out.println("Creating ngrams for cluster " + cluster);
			StringBuilder docTexts = new StringBuilder();
			DocNGramSimple docNgrams = new DocNGramSimple();
			for (String docText : docClusters[cluster]) {
				docTexts.append(RawToNGram.convert(docText));
			}
			clusterNgrams[cluster] = docNgrams.getProbs(docTexts.toString());
		}
		return clusterNgrams;
	}

	@SuppressWarnings("unchecked")
	private static List<String>[] genClusters(String[] docTexts,
			File thetaProbsFile, int numTopics) throws IOException {
		List<String>[] clusterTexts = new List[numTopics];
		for (int i = 0; i < clusterTexts.length; i++) {
			clusterTexts[i] = new ArrayList<String>();
		}
		BufferedReader br = null;
		String line;
		String[] vals;
		int maxTopic, doc = 0;
		double maxValue, val;
		try {
			br = new BufferedReader(new FileReader(thetaProbsFile));
			while ((line = br.readLine()) != null) {
				System.out
						.println("Finding most likely cluster for doc " + doc);
				vals = line.split(" ");
				maxTopic = -1;
				maxValue = -1;
				for (int i = 0; i < vals.length; i++) {
					val = Double.parseDouble(vals[i]);
					if (maxValue < val) {
						maxTopic = i;
						maxValue = val;
					}
				}
				clusterTexts[maxTopic].add(docTexts[doc++]);
			}
		} finally {
			if (br != null) {
				br.close();
			}
		}

		return clusterTexts;
	}

	private static String[] loadDocTexts(int numDocs, File collapsedDocsFile)
			throws IOException {
		String[] texts = new String[numDocs];
		String line;
		BufferedReader br = null;
		int count = 0;
		try {
			br = new BufferedReader(new FileReader(collapsedDocsFile));
			while ((line = br.readLine()) != null) {
				System.out.println("Loading doc " + count);
				texts[count++] = line;
			}
		} finally {
			if (br != null) {
				br.close();
			}
		}

		return texts;
	}

	private static int getNumDocs(File collapsedDocsFile) throws IOException {
		BufferedReader br = null;
		int numDocs = 0;
		try {
			br = new BufferedReader(new FileReader(collapsedDocsFile));
			while (br.readLine() != null) {
				numDocs++;
			}
		} finally {
			if (br != null) {
				br.close();
			}
		}
		return numDocs;
	}

	private static int getNumTopics(File thetaProbsFile) throws IOException {
		BufferedReader br = null;
		int numTopics = 0;
		try {
			br = new BufferedReader(new FileReader(thetaProbsFile));
			numTopics = br.readLine().split(" ").length;
		} finally {
			if (br != null) {
				br.close();
			}
		}
		return numTopics;
	}

	public static void main(String[] args) throws IOException {
		String theta = "data/lda/full-200-model-final.theta";
		String collapsed = "data/all_raw";
		DocCluster cluster = loadDocCluster(collapsed, theta);
		System.out.println("Key: "
				+ cluster.getClusterNgramProbs(0).firstEntry().getKey());
		System.out.println("Value: "
				+ cluster.getClusterNgramProbs(0).firstEntry().getValue());
		SerializableWrapper sw = new SerializableWrapper(cluster);
		sw.save("data/model-200-cluster");
//		DocCluster cluster = SerializableWrapper.readObject("data/model-100-cluster");
//		System.out.println("Key: "
//				+ cluster.getClusterNgramProbs(0).firstEntry().getKey());
//		System.out.println("Value: "
//				+ cluster.getClusterNgramProbs(0).firstEntry().getValue());
	}
}
