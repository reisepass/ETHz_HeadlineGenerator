package ethz.nlp.headgen.sum;

import java.util.List;

import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;
import ethz.nlp.headgen.Doc;
 
public class FirstSentSum implements Summerizer {

	
	public static final String WILDCARD_STRING = "*@#$^$#@$*";
	protected static final String[] FLUFF_POS = {}; // POS that can be
													// removed without
													// changing sentence
													// information EX: very,
													// much, super
	protected static final String[] SEPERATOR_POS = { ",", ";", "." };
	public static final String[] OPEN_CLASS_POS = { "NN", "NNS", "NNP",
			"NNP", "NNPS", "RB", "RBR", "UH", "VBD", "VBG", "VBN", "VBP",
			"VBZ", "FW", "JJ", "JJR", "JJS" }; // http://www.ling.upenn.edu/courses/Fall_2003/ling001/penn_treebank_pos.html
	protected static final String[] START_POS = OPEN_CLASS_POS;
	protected static final String[] END_POS = OPEN_CLASS_POS;
	public static final String[] CLEAN_WORDS = OPEN_CLASS_POS;

	protected Doc doc;
	protected Annotation anot;
	protected int sumLeng;
	protected String firstSent;

	public FirstSentSum(Doc doc, int summaryLength) {
		this.doc = doc;
		this.anot = doc.getAno();
		this.sumLeng = summaryLength;
	}

	@Override
	public String summary() {
		CoreMap firstSentence = findFirstSent();
		trimSentenceEnds(firstSentence);
		removePoSInList(firstSentence.get(TokensAnnotation.class), FLUFF_POS);
		removeInternalDependentClause(firstSentence.get(TokensAnnotation.class));
		removePoSNotInList(firstSentence.get(TokensAnnotation.class),
				OPEN_CLASS_POS);
		// firstSentence.get(TokensAnnotation.class);
		// firstSentence.set(TokensAnnotation.class, outTest);
		// you can undo this stuff i was just testing things

		// String before = firstSentence.get(TokensAnnotation.class).toString();
		// firstSentence.get(TokensAnnotation.class).remove(0);
		// String after = firstSentence.get(TokensAnnotation.class).toString();
		// String test = toString(firstSentence);

		// TODO Auto-generated method stub
		// Jared is the best
		String out = toString(firstSentence);
		out = fixCapitalization(out);
		out = fixWhiteSpace(out);
		return out;
	}

	protected CoreMap findFirstSent() {
		CoreMap sentence = anot.get(SentencesAnnotation.class).get(0);
		return sentence;
	}

	public String getFirstSent() {
		if (firstSent == null) {
			CoreMap tmp = findFirstSent();
			firstSent = tmp.toString();

		}
		return firstSent;
	}

	// Get rid of words that don't fit at the beginning or end of the sentence
	protected void trimSentenceEnds(CoreMap sentence) {
		List<CoreLabel> tokens = sentence.get(TokensAnnotation.class);
		trimBeginning(tokens);
		trimEnd(tokens);
	}

	protected void trimBeginning(List<CoreLabel> tokens) {
		while (tokens.size() > 0) {
			String pos = tokens.get(0).get(PartOfSpeechAnnotation.class);

			for (String s : START_POS) {
				if (s.equals(pos)) {
					return; // Suitable starting token found
				}
			}

			// Remove the first token
			tokens.remove(0);
		}
	}

	protected void trimEnd(List<CoreLabel> tokens) {
		while (tokens.size() > 0) {
			String pos = tokens.get(tokens.size() - 1).get(
					PartOfSpeechAnnotation.class);

			for (String s : END_POS) {
				if (s.equals(pos)) {
					return; // Suitable ending token found
				}
			}

			// Remove the last token
			tokens.remove(tokens.size() - 1);
		}
	}

	protected void removePoSInList(List<CoreLabel> tokens, String[] List) {
		if (List == null) {
			List = FLUFF_POS;
		}

		for (int i = 0; i < tokens.size(); i++) {
			String pos = tokens.get(i).get(PartOfSpeechAnnotation.class);

			for (String s : List) {
				if (s.equals(pos)) {
					tokens.remove(i);
					i--;
				}
			}

			// Remove the last token

		}
	}

	public static void removePoSNotInList(List<CoreLabel> tokens, String[] List) {
		if (List == null) {
			List = OPEN_CLASS_POS;
		}
		for (int i = 0; i < tokens.size(); i++) {
			String pos = tokens.get(i).get(PartOfSpeechAnnotation.class);
			boolean inList = false;
			for (String s : List) {
				if (s.equals(pos)) {
					inList = true;
				}
			}
			if (!inList) {
				tokens.remove(i);
				i--;
			}

			// Remove the last token

		}
	}

	protected void removeInternalDependentClause(List<CoreLabel> tokens) {
		boolean dependOn = false;
		for (int i = 0; i < tokens.size(); i++) {
			String pos = tokens.get(i).get(PartOfSpeechAnnotation.class);
			String debugTxt = tokens.get(i).get(TextAnnotation.class);
			if (pos.equals(",")) {
				dependOn = !dependOn;
				tokens.remove(i);
				i--;
			} else if (pos.equals(".")) {
				dependOn = !dependOn;
			} else {
				if (dependOn) {
					tokens.remove(i);
					i--;
				}

			}

			// Remove the last token
			// System.out.println(tokens.toString());
		}

	}

	public static String fixWhiteSpace(String inp) {

		while (inp.indexOf(" ,") != -1) {
			inp = inp.substring(0, inp.indexOf(" ,")) + ","
					+ inp.substring(inp.indexOf(" ,") + 2, inp.length());
		}

		while (inp.indexOf(" .") != -1) {
			inp = inp.substring(0, inp.indexOf(" .")) + ". "
					+ inp.substring(inp.indexOf(" .") + 2, inp.length());
		}
		while (inp.indexOf("  ") != -1) {
			inp = inp.substring(0, inp.indexOf("  ")) + " "
					+ inp.substring(inp.indexOf("  ") + 2, inp.length());
		}
		return inp;
	}

	public static String fixCapitalization(String inp) {
		while (!('a' <= inp.charAt(0) && inp.charAt(0) <= 'z')
				&& !('A' <= inp.charAt(0) && inp.charAt(0) <= 'Z')) {
			inp = inp.substring(1, inp.length());
		}
		if ('a' <= inp.charAt(0) && inp.charAt(0) <= 'z') {
			inp = (char) ((int) inp.charAt(0) - 32)
					+ inp.substring(1, inp.length());
		}
		return inp;
	}

	public String toString(CoreMap sentence) {
		StringBuilder sb = new StringBuilder();
		for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
			sb.append(token.get(TextAnnotation.class) + " ");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
}
