package ethz.nlp.headgen.lda;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import ethz.nlp.headgen.prob.DocNGramSimple;
import ethz.nlp.headgen.prob.RawToNGram;
import ethz.nlp.headgen.util.FileIO;

@SuppressWarnings("unchecked")
public class DocCluster {
	private LDAProbs probs;
	private List<String>[] docClusters = null;
	private TreeMap<ArrayList<String>, Double>[] clusterNgrams;

	public DocCluster(LDAProbs probs) {
		this.probs = probs;
		clusterNgrams = new TreeMap[probs.getNumTopics()];
	}

	public List<String>[] getDocClusters() {
		if (docClusters == null) {
			genClusters();
		}
		return docClusters;
	}

	public List<String> getDocsInCluster(int cluster) {
		if (cluster < 0 || cluster > probs.getNumTopics()) {
			throw new IllegalArgumentException("Invalid cluster (" + cluster
					+ "). Cluster must be in range 0-"
					+ (probs.getNumTopics() - 1));
		}
		return getDocClusters()[cluster];
	}

	private void genClusters() {
		int numTopics = probs.getNumTopics();

		docClusters = new List[numTopics];
		for (int i = 0; i < numTopics; i++) {
			docClusters[i] = new ArrayList<String>();
		}
		for (String d : probs.getDocList()) {
			docClusters[probs.getMostLikelyTopic(d)].add(d);
		}
	}

	public TreeMap<ArrayList<String>, Double> getClusterNgramProbs(int cluster)
			throws IOException {
		if (clusterNgrams[cluster] == null) {
			List<String> clusterDocs = getDocsInCluster(cluster);
			StringBuilder docTexts = new StringBuilder();
			DocNGramSimple docNgrams = new DocNGramSimple();
			String text;
			for (String d : clusterDocs) {
				text = FileIO.readTextFile(new File(d));
				docTexts.append(RawToNGram.convert(text));
			}
			clusterNgrams[cluster] = docNgrams.getProbs(docTexts.toString());
		}
		return clusterNgrams[cluster];
	}
}
