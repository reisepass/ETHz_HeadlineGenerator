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
import ethz.nlp.headgen.prob.NGramProbs;
import ethz.nlp.headgen.sum.features.Feature;

public class FeatureBasedSummary implements Summerizer {
	public static final int NUM_TOP_WORDS = 20;
	public static final String[] VERB_POS = { "VB", "VBD", "VBG", "VBN", "VBP",
			"VBZ" };
	public static final String[] REJECT_ENTITIES = { "O", "NUMBER", "MISC",
			"DATE", "DURATION" };

	protected Doc doc;
	protected Feature[] features;
	protected int length;
	protected List<NGramProbs[]> probs;

	public FeatureBasedSummary(Doc doc, int length, List<NGramProbs[]> probs,
			Feature... features) {
		this.probs = probs;
		this.doc = doc;
		this.features = features;
		this.length = length;
	}

	@Override
	public String summary() {
		SortedSet<WordEntry> topWords = getTopWords();
		String topVerb = getTopVerb(topWords);
		SortedSet<EntityEntry>[] topEntities = getTopEntities();
		// StringBuilder sb = new StringBuilder();
		return genHeadline(topWords, topEntities, topVerb);
		// return sb.toString();
	}

	private String getTopVerb(SortedSet<WordEntry> topWords) {
		String topVerb = null;
		for (WordEntry w : topWords) {
			if (topVerb == null
					&& isVerb(w.token.get(PartOfSpeechAnnotation.class))) {
				topVerb = w.getWord();
			}
		}
		return topVerb;
	}

	/*
	 * Strategy: 1) Find the top subject NE, top verb and top object NE 2)
	 * Construct: SubNE + verb + ObjNE
	 * 
	 * Filter the clustered ngram probs based on the top words list and generate
	 * a sentence from bigrams until the length cutoff has been reached
	 */
	private String genHeadline(SortedSet<WordEntry> topWords,
			SortedSet<EntityEntry>[] topEntities, String topVerb) {
		String subjectEntity = topEntities[0].first().entity;
		String objectEntity = null;
		for (EntityEntry entry : topEntities[1]) {
			if (!subjectEntity.equals(entry.entity)) {
				objectEntity = entry.entity;
				break;
			}
		}
		return subjectEntity + " " + topVerb + " " + objectEntity;
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
		StringBuilder entityText = null;
		boolean verbFound = false, entityChain = false;

		for (CoreMap sentence : a.get(SentencesAnnotation.class)) {
			verbFound = false;
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				pos = token.get(PartOfSpeechAnnotation.class);

				entity = token.get(NamedEntityTagAnnotation.class);
				if (!acceptEntity(entity)) {
					if (entityChain) {
						entry = new EntityEntry(entityText.toString());
						if (verbFound) {
							if (objectEntities.contains(entry)) {
								// increment occurence count
								objectEntities.get(objectEntities
										.indexOf(entry)).occurences++;
							} else {
								// add to the list
								objectEntities.add(entry);
							}
						} else {
							if (subjectEntities.contains(entry)) {
								// increment occurence count
								subjectEntities.get(subjectEntities
										.indexOf(entry)).occurences++;
							} else {
								// add to the list
								subjectEntities.add(entry);
							}
						}
						entityChain = false;
					}

					if (isVerb(pos)) {
						verbFound = true;
					}

					continue;
				}

				if (entityChain) {
					entityText.append(" " + token.get(TextAnnotation.class));
				} else {
					entityChain = true;
					entityText = new StringBuilder(
							token.get(TextAnnotation.class));
				}

				if (isVerb(pos)) {
					verbFound = true;
				}
			}
		}

		// Store entityChain at the end of the doc
		if (entityChain) {
			entry = new EntityEntry(entityText.toString());
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
					subjectEntities.get(subjectEntities.indexOf(entry)).occurences++;
				} else {
					// add to the list
					subjectEntities.add(entry);
				}
			}
		}

		return new TreeSet[] { new TreeSet<EntityEntry>(subjectEntities),
				new TreeSet<EntityEntry>(objectEntities) };
	}

	private boolean acceptEntity(String entity) {
		if (entity == null || "".equals(entity)) {
			return false;
		}
		for (String s : REJECT_ENTITIES) {
			if (s.equals(entity)) {
				return false;
			}
		}
		return true;
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

			entry.score = scoreWord(entry.token);
			topWords.add(entry);
			if (topWords.size() < NUM_TOP_WORDS) {
				topWords.add(entry);
			} else {
				// Replace the last word if it has a lower score or if the
				// current word is a verb and the lowest scoring word isn't

				// If the current word is a verb, add it to the set if it's
				// score is higher than the last entry's or if the last entry
				// isn't a verb. Otherwise replace the last word if the score is
				// higher and it's not a verb
				if (isVerb(token.get(PartOfSpeechAnnotation.class))) {
					if (entry.score > topWords.last().score
							|| !isVerb(topWords.last().token
									.get(PartOfSpeechAnnotation.class))) {
						topWords.remove(topWords.last());
						topWords.add(entry);
					}
				} else if (entry.score > topWords.last().score
						&& !isVerb(topWords.last().token
								.get(PartOfSpeechAnnotation.class))) {
					topWords.remove(topWords.last());
					topWords.add(entry);
				}
			}
		}

		return topWords;
	}

	protected double scoreWord(CoreLabel token) {
		double score = 0;
		for (Feature f : features) {
			score += f.calc(token);
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
			} else if (arg0.entity.split(" ").length > entity.split(" ").length) {
				return 1;
			} else if (arg0.entity.split(" ").length < entity.split(" ").length) {
				return -1;
			}
			return 0;
		}
	}
}
