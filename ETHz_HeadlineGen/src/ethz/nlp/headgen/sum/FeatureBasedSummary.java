package ethz.nlp.headgen.sum;

import java.util.SortedSet;
import java.util.TreeSet;

import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import ethz.nlp.headgen.Doc;

public class FeatureBasedSummary implements Summerizer {
	public static final int NUM_TOP_WORDS = 20;

	private Doc doc;
	private Feature[] features;

	public FeatureBasedSummary(Doc doc, int length, Feature... features) {
		this.doc = doc;
		this.features = features;
	}

	@Override
	public String summary() {
		SortedSet<WordEntry> topWords = getTopWords();
		StringBuilder sb = new StringBuilder();
		for (WordEntry w : topWords) {
			sb.append(w.token.get(TextAnnotation.class));
		}
		return sb.toString();
	}

	private SortedSet<WordEntry> getTopWords() {
		SortedSet<WordEntry> topWords = new TreeSet<WordEntry>();
		Annotation a = doc.getAno();
		WordEntry entry;

		for (CoreLabel token : a.get(TokensAnnotation.class)) {
			entry = new WordEntry(token);

			if (topWords.contains(entry)) {
				continue;
			}

			entry.score = scoreWord(entry.token.get(TextAnnotation.class));
			if (topWords.size() < NUM_TOP_WORDS) {
				topWords.add(entry);
			} else if (entry.score > topWords.last().score) {
				topWords.remove(topWords.last());
				topWords.add(entry);
			}
		}

		return topWords;
	}

	private double scoreWord(String word) {
		double score = 0;
		for (Feature f : features) {
			score += f.calc(word);
		}
		return score;
	}

	@Override
	public void setDoc(Doc inD) {
		doc = inD;
	}

	private static class WordEntry {
		CoreLabel token;
		double score = -1;

		public WordEntry(CoreLabel token) {
			this.token = token;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof WordEntry) {
				return ((WordEntry) obj).token.get(TextAnnotation.class)
						.equals(token.get(TextAnnotation.class));
			}
			return false;
		}
	}
}
