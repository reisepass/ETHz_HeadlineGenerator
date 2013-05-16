package ethz.nlp.headgen.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import ethz.nlp.headgen.Doc;
import ethz.nlp.headgen.util.FileIO;

@SuppressWarnings("serial")
public class CorpusCounts implements Serializable {
	private static StanfordCoreNLP pipeline;
	static {
		Properties props = new Properties();
		props.put("annotators", "tokenize");
		pipeline = new StanfordCoreNLP(props);
	}

	private int numDocs = 0;
	private WordCountTree docAppearanceCounts = new WordCountTree();

	private CorpusCounts() {
	}

	public static CorpusCounts generateCounts(String docMapFile)
			throws IOException {
		BufferedReader br = null;
		String line;
		CorpusCounts counts = new CorpusCounts();
		int count = 0, total;
		try {
			br = new BufferedReader(new FileReader(new File(docMapFile)));
			total = Integer.parseInt(line = br.readLine());

			while ((line = br.readLine()) != null) {
				if (++count % 500 == 0) {
					System.out.println("Adding counts for file " + count + "/"
							+ total);
				}
				counts.addCounts(FileIO.readTextFile(new File(line)));
			}
		} finally {
			if (br != null) {
				br.close();
			}
		}
		return counts;
	}

	private void addCounts(String rawText) {
		// Generate the tokens
		List<CoreLabel> tokens;
		Annotation a = new Annotation(rawText);
		pipeline.annotate(a);
		tokens = a.get(TokensAnnotation.class);

		WordCountTree docCounts = new WordCountTree();

		for (CoreLabel token : tokens) {
			docCounts.put(token.get(TextAnnotation.class));
		}

		for (CharSequence word : docCounts.getKeysStartingWith("")) {
			docAppearanceCounts.put(word);
		}

		numDocs++;
	}

	public static CorpusCounts generateCounts(List<Doc> docs) {
		CorpusCounts counts = new CorpusCounts();
		for (Doc d : docs) {
			counts.addCounts(d);
		}
		return counts;
	}

	private void addCounts(Doc d) {
		List<CoreLabel> tokens;
		if (d.getAno() != null) {
			tokens = d.getAno().get(TokensAnnotation.class);
		} else {
			// Generate the tokens
			Annotation a = new Annotation(d.cont);
			pipeline.annotate(a);
			tokens = a.get(TokensAnnotation.class);
		}

		d.wordCounts = new WordCountTree();

		for (CoreLabel token : tokens) {
			d.wordCounts.put(token.get(TextAnnotation.class));
		}

		for (CharSequence word : d.wordCounts.getKeysStartingWith("")) {
			docAppearanceCounts.put(word);
		}

		numDocs++;
	}

	public int getNumDocs() {
		return numDocs;
	}

	public WordCountTree getDocAppearanceCounts() {
		return docAppearanceCounts;
	}
}