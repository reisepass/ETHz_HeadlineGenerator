package ethz.nlp.headgen.sum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;
import ethz.nlp.headgen.Constants;
import ethz.nlp.headgen.Doc;
import ethz.nlp.headgen.prob.NGramProbs;
import ethz.nlp.headgen.prob.NoFilterAddTestCorpus;
import ethz.nlp.headgen.sum.features.Feature;

public class FeatureBasedSummary extends ArticleTopicNGramSum implements
		Summerizer {
	public static final int NUM_TOP_WORDS = 20;
	public static final String[] VERB_POS = { "VB", "VBD", "VBG", "VBN", "VBP",
			"VBZ" };
	public static final String[] REJECT_ENTITIES = { "O", "NUMBER", "MISC",
			"DATE", "DURATION" };

	protected Doc doc;
	protected Feature[] features;
	protected int length;
	protected NGramProbs probs;

	public FeatureBasedSummary(Doc doc, int length, NGramProbs probs,
			Feature... features) {
		super(doc, length);
		this.probs = probs;
		this.doc = doc;
		this.features = features;
		this.length = length;
	}

	@Override
	public String summary() {
		SortedSet<WordEntry> topWords = getScoredWords();
		String topVerb = getTopVerb(topWords);
		SortedSet<EntityEntry>[] topEntities = getTopEntities();
		// StringBuilder sb = new StringBuilder();
		// for (WordEntry word : topWords) {
		// sb.append(word.getWord() + " ");
		// }
		// return sb.toString();
		return genHeadline(topWords, topEntities, topVerb);
	}

	protected String getTopVerb(SortedSet<WordEntry> topWords) {
		String topVerb = null;
		for (WordEntry w : topWords) {
			if (isVerb(w.token.get(PartOfSpeechAnnotation.class))) {
				topVerb = w.getWord();
				break;
			}
		}
		return topVerb;
	}

	/*
	 * Strategy: 1) Find the top subject NE, top verb and top object NE 2)
	 * Construct: SubNE + verb + NGram-Chain + ObjNE
	 * 
	 * Filter the clustered ngram probs based on the top words list and generate
	 * a sentence from bigrams until the length cutoff has been reached
	 */
	private String genHeadline(SortedSet<WordEntry> topWords,
			SortedSet<EntityEntry>[] topEntities, String topVerb) {
		String subjectEntity = null;
		if (topEntities[0] == null) {
			// TODO: Get next best subject
		} else {
			try {
				subjectEntity = topEntities[0].first().entity;
			} catch(NoSuchElementException e) {
				//TODO: Get next best subject
			}
		}
		
		String objectEntity = null;
		if (subjectEntity == null) {
			objectEntity = null;
		} else {
			for (EntityEntry entry : topEntities[1]) {
				if (!subjectEntity.equals(entry.entity)) {
					objectEntity = entry.entity;
					break;
				}
			}
		}

		TreeMap<ArrayList<String>, Double> corpTree = probs.filterNgrams(doc);
		NGramProbs concat = new NoFilterAddTestCorpus(corpTree, 1.5);
		TreeMap<ArrayList<String>, Double> corpDocngrams = concat
				.filterNgrams(doc);
		Comparator<ArrayList<String>> localCompareObj = Constants.CompareObj;

		StringBuilder strBld = new StringBuilder();
		String out = "#####################22###############";

		List<Map.Entry<ArrayList<String>, Double>> sorted = new LinkedList<Map.Entry<ArrayList<String>, Double>>(
				corpDocngrams.entrySet());
		Collections.sort(sorted, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue())
						.compareTo(((Map.Entry) (o2)).getValue());
			}
		});
		Collections.reverse(sorted);
		ArrayList<String> first = new ArrayList<String>();
		first.add(subjectEntity);
		first.add(topVerb);

		boolean objectFound = false;
		int ngramLength = first.size();
		strBld.append(printArray(first));

		String[] wordsSoFar = strBld.toString().split(" ");
		String justAdded = wordsSoFar[wordsSoFar.length - 1];
		while (strBld.length() < sumLeng) {
			for (Map.Entry<ArrayList<String>, Double> elem : sorted) {
				if (elem.getKey().size() > 1) {
					if (localCompareObj.compare(elem.getKey(),
							wildWithInpAtFront(justAdded, ngramLength)) == 0) {
						elem.getKey().remove(0);
						strBld.append(printArray(elem.getKey()));
						wordsSoFar = strBld.toString().split(" ");
						justAdded = wordsSoFar[wordsSoFar.length - 1];
						if (justAdded.equals(objectEntity)) {
							objectFound = true;
						}
					}
				}
			}
			strBld.append(" ");
			// Remove words that done fit and we are done
			if (strBld.length() > sumLeng) {
				out = strBld.toString();
				out = out.substring(0, sumLeng - 1);
				out = out.substring(0, out.lastIndexOf(" "));

				if (!objectFound) {
					while (out.length() + objectEntity.length() > sumLeng) {
						out = out.substring(0, out.lastIndexOf(" "));
					}
					out += " " + objectEntity;
				}
				break;
			}

		}

		return out;
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

	protected boolean isVerb(Object object) {
		for (String verb : VERB_POS) {
			if (verb.equals(object)) {
				return true;
			}
		}
		return false;
	}

	protected SortedSet<WordEntry> getScoredWords() {
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
			// if (topWords.size() < NUM_TOP_WORDS) {
			// topWords.add(entry);
			// } else {
			// // Replace the last word if it has a lower score or if the
			// // current word is a verb and the lowest scoring word isn't
			//
			// // If the current word is a verb, add it to the set if it's
			// // score is higher than the last entry's or if the last entry
			// // isn't a verb. Otherwise replace the last word if the score is
			// // higher and it's not a verb
			// if (isVerb(token.get(PartOfSpeechAnnotation.class))) {
			// if (entry.score > topWords.last().score
			// || !isVerb(topWords.last().token
			// .get(PartOfSpeechAnnotation.class))) {
			// topWords.remove(topWords.last());
			// topWords.add(entry);
			// }
			// } else if (entry.score > topWords.last().score
			// && !isVerb(topWords.last().token
			// .get(PartOfSpeechAnnotation.class))) {
			// topWords.remove(topWords.last());
			// topWords.add(entry);
			// }
			// }
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

	static class WordEntry implements Comparable<WordEntry> {
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
			int result = Double.compare(score, arg0.score);
			// if (result == 0) {
			// result = getWord().compareTo(arg0.getWord());
			// }
			return result;
		}

		public String getWord() {
			return token.get(TextAnnotation.class);
		}
	}

	static class EntityEntry implements Comparable<EntityEntry> {
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
