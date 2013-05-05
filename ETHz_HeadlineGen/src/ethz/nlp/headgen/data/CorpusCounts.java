package ethz.nlp.headgen.data;

import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import ethz.nlp.headgen.Doc;

public class CorpusCounts {
	private int numDocs = 0;
	private WordCountTree totalWordCounts = new WordCountTree();
	private WordCountTree docAppearanceCounts = new WordCountTree();

	private CorpusCounts() {
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
		if (d.annotation != null) {
			tokens = d.annotation.get(TokensAnnotation.class);
		} else {
			// Generate the tokens
			Annotation a = new Annotation(d.cont);
			Properties props = new Properties();
			props.put("annotators", "tokenize");
			StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
			pipeline.annotate(a);
			tokens = a.get(TokensAnnotation.class);
		}

		d.wordCounts = new WordCountTree();
		for (CoreLabel token : tokens) {
			totalWordCounts.put(token.get(TextAnnotation.class));
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

	public WordCountTree getTotalWordCounts() {
		return totalWordCounts;
	}

	public WordCountTree getDocAppearanceCounts() {
		return docAppearanceCounts;
	}
}