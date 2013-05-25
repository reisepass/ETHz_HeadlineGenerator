package ethz.nlp.headgen.sum;

import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;

import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import ethz.nlp.headgen.Doc;
import ethz.nlp.headgen.prob.NGramProbs;
import ethz.nlp.headgen.sum.FeatureBasedSummary.EntityEntry;
import ethz.nlp.headgen.sum.features.Feature;

public class FeatureBasedSummary_BagOfWords extends FeatureBasedSummary {

	public FeatureBasedSummary_BagOfWords(Doc doc, int length,
			Feature... features) {
		super(doc, length, null, features);
	}

	public FeatureBasedSummary_BagOfWords(Doc doc, int length,
			NGramProbs probs, Feature... features) {
		super(doc, length, probs, features);
	}

	@Override
	public String summary() {
		StringBuilder sb = new StringBuilder();
		SortedSet<WordEntry> topWords = getScoredWords();
		String topVerb = getTopVerb(topWords);
		SortedSet<EntityEntry>[] topEntities = getTopEntities();
		
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

		boolean verbFound = false;
//		sb.append(subjectEntity);
		for (WordEntry word : topWords) {
			if (word.getWord().equals(topVerb)) {
				verbFound = true;
			}
			sb.append(" " + word.getWord());
		}

		if (!verbFound) {
//			sb.append(" " + topVerb);
		}
//		sb.append(" " + objectEntity);

		return sb.toString();
	}

	@Override
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
}
