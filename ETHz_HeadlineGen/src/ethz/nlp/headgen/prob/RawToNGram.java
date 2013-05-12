package ethz.nlp.headgen.prob;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class RawToNGram {
	private static final Pattern ACCEPT_PATTERN = Pattern
			.compile("^[a-zA-Z]+|\\.$");

	private static StanfordCoreNLP pipeline = null;

	private RawToNGram() {
	}

	public static String convert(String text) {
		if (pipeline == null) {
			initPipeline();
		}
		Annotation a = new Annotation(text);
		pipeline.annotate(a);
		List<CoreLabel> tokens = a.get(TokensAnnotation.class);
		List<String> cleanedTokens = cleanTokens(tokens);
		return toString(cleanedTokens);
	}

	private static List<String> cleanTokens(List<CoreLabel> tokens) {
		List<String> cleaned = new ArrayList<String>();
		String clean;
		for (CoreLabel token : tokens) {
			clean = token.get(TextAnnotation.class);
			if (ACCEPT_PATTERN.matcher(clean).matches()) {
				cleaned.add(clean);
			}
		}
		return cleaned;
	}

	private static void initPipeline() {
		Properties props = new Properties();
		props.put("annotators", "tokenize");
		pipeline = new StanfordCoreNLP(props);
	}

	private static String toString(List<String> lemmas) {
		StringBuilder sb = new StringBuilder();
		for (String s : lemmas) {
			sb.append(s.toLowerCase() + " ");
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		System.out
				.println(convert("We are 213 testing [a2s] this class. This is a new sentence"));
	}
}
