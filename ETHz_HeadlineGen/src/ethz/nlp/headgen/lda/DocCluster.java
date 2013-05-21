package ethz.nlp.headgen.lda;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.TreeMap;

public interface DocCluster extends Serializable {
	public static final String CLUSTER_100_PATH = "data/model-100-cluster";
	
	public TreeMap<ArrayList<String>, Double> getClusterNgramProbs(int cluster)
			throws IOException;
}
