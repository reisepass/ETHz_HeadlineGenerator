package ethz.nlp.headgen.lda;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import ethz.nlp.headgen.util.StopWords;

public class RawToLDA {
	private static StanfordCoreNLP pipeline = null;

	private RawToLDA() {
	}

	public static String convert(String text) {
		if (pipeline == null) {
			initPipeline();
		}
		Annotation a = new Annotation(text);
		pipeline.annotate(a);
		List<CoreLabel> tokens = a.get(TokensAnnotation.class);
		List<String> lemmas = lemmatize(tokens);
		removeStopWords(lemmas);
		return toString(lemmas);
	}

	private static List<String> lemmatize(List<CoreLabel> tokens) {
		List<String> lemmas = new ArrayList<String>();
		for (CoreLabel token : tokens) {
			lemmas.add(token.get(LemmaAnnotation.class));
		}
		return lemmas;
	}

	private static void removeStopWords(List<String> lemmas) {
		int i = 0;
		String token;
		while (i < lemmas.size()) {
			token = lemmas.get(i);
			if (StopWords.isStopWord(token)) {
				lemmas.remove(i);
			} else {
				i++;
			}
		}
	}

	private static void initPipeline() {
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma");
		pipeline = new StanfordCoreNLP(props);
	}

	private static String toString(List<String> lemmas) {
		StringBuilder sb = new StringBuilder();
		for (String s : lemmas) {
			sb.append(s + " ");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
	
	public static void main(String[] args) {
		
	}
}
