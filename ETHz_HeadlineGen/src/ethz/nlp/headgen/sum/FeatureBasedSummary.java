package ethz.nlp.headgen.sum;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;
import ethz.nlp.headgen.Doc;

public class FeatureBasedSummary implements Summerizer {
	public static final int NUM_TOP_WORDS = 20;
	public static final String[] VERB_POS = { "VB", "VBD", "VBG", "VBN", "VBP",
			"VBZ" };

	protected Doc doc;
	protected Feature[] features;

	public FeatureBasedSummary(Doc doc, int length, Feature... features) {
		this.doc = doc;
		this.features = features;
	}

	@Override
	public String summary() {
		SortedSet<WordEntry> topWords = getTopWords();
		SortedSet<EntityEntry>[] topEntities = getTopEntities();
		StringBuilder sb = new StringBuilder();
		String topVerb = null;
		for (WordEntry w : topWords) {
			if (topVerb == null && isVerb(w.getWord())) {
				topVerb = w.getWord();
			}
			sb.append(w.token.get(TextAnnotation.class));
		}
		return sb.toString();
	}

	// Returns an array of size two that returns a sorted list of the most
	// frequently occuring named entities that occur before and after the verb
	// (hopefully indicating whether it's the subject or object of the sentence)
	@SuppressWarnings("unchecked")
	protected SortedSet<EntityEntry>[] getTopEntities() {
		List<EntityEntry> subjectEntities = new ArrayList<EntityEntry>();
		List<EntityEntry> objectEntities = new ArrayList<EntityEntry>();
		Annotation a = doc.getAno();
		EntityEntry entry;
		String entity, pos;
		boolean verbFound;

		for (CoreMap sentence : a.get(SentencesAnnotation.class)) {
			verbFound = false;
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				pos = token.get(PartOfSpeechAnnotation.class);
				if (isVerb(pos)) {
					verbFound = true;
				}

				entity = token.get(NamedEntityTagAnnotation.class);
				if (entity == null || "".equals(entity)) {
					continue;
				}

				entry = new EntityEntry(entity);
				if (verbFound) {
					if (objectEntities.contains(entry)) {
						// increment occurence count
						objectEntities.get(objectEntities.indexOf(entry)).occurences++;
					} else {
						// add to the list
						objectEntities.add(entry);
					}
				} else {
					if (subjectEntities.contains(entry)) {
						// increment occurence count
						subjectEntities.get(objectEntities.indexOf(entry)).occurences++;
					} else {
						// add to the list
						subjectEntities.add(entry);
					}
				}
			}
		}

		return new TreeSet[] { new TreeSet<EntityEntry>(subjectEntities),
				new TreeSet<EntityEntry>(objectEntities) };
	}

	protected boolean isVerb(String pos) {
		for (String verb : VERB_POS) {
			if (verb.equals(pos)) {
				return true;
			}
		}
		return false;
	}

	protected SortedSet<WordEntry> getTopWords() {
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

	protected double scoreWord(String word) {
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

	private static class WordEntry implements Comparable<WordEntry> {
		CoreLabel token;
		double score = -1;

		public WordEntry(CoreLabel token) {
			this.token = token;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof WordEntry) {
				return ((WordEntry) obj).getWord().equals(getWord());
			}
			return false;
		}

		@Override
		public int compareTo(WordEntry arg0) {
			return Double.compare(score, arg0.score);
		}

		public String getWord() {
			return token.get(TextAnnotation.class);
		}
	}

	private static class EntityEntry implements Comparable<EntityEntry> {
		String entity;
		int occurences = 1;

		public EntityEntry(String entity) {
			this.entity = entity;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof EntityEntry) {
				return ((EntityEntry) obj).entity.equals(entity);
			}
			return false;
		}

		@Override
		public int compareTo(EntityEntry arg0) {
			if (occurences > arg0.occurences) {
				return 1;
			} else if (occurences < arg0.occurences) {
				return -1;
			}
			return 0;
		}
	}
}
