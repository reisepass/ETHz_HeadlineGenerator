package ethz.nlp.headgen;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.trees.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.util.CoreMap;
import ethz.nlp.headgen.io.DocReader;
import ethz.nlp.headgen.io.IOConfig;
import ethz.nlp.headgen.sum.FirstSentSum;
import ethz.nlp.headgen.sum.Summerizer;
import ethz.nlp.headgen.util.ConfigFactory;
import ethz.nlp.headgen.xml.XMLDoc;

public class main {
	public main() {
		super();
	}

	public Annotation nlpTest(String data) {
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization,
		// NER, parsing, and coreference resolution
		Properties props = new Properties();
		props.put("annotators",
				"tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		// read some text in the text variable
		String text = data;

		// create an empty Annotation just with the given text
		Annotation document = new Annotation(text);

		// run all Annotators on this text
		pipeline.annotate(document);

		// these are all the sentences in this document
		// a CoreMap is essentially a Map that uses class objects as keys and
		// has values with custom types
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		for (CoreMap sentence : sentences) {
			// traversing the words in the current sentence
			// a CoreLabel is a CoreMap with additional token-specific methods
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				// this is the text of the token
				String word = token.get(TextAnnotation.class);
				// this is the POS tag of the token
				String pos = token.get(PartOfSpeechAnnotation.class);
				// this is the NER label of the token
				String ne = token.get(NamedEntityTagAnnotation.class);

				System.out.print(word + "(" + pos + ")[" + ne + "]");
			}
			System.out.println();

			// this is the parse tree of the current sentence
			Tree tree = sentence.get(TreeAnnotation.class);

			// this is the Stanford dependency graph of the current sentence
			SemanticGraph dependencies = sentence
					.get(CollapsedCCProcessedDependenciesAnnotation.class);
			int a = 1 + 1;
		}

		// This is the coreference link graph
		// Each chain stores a set of mentions that link to each other,
		// along with a method for getting the most representative mention
		// Both sentence and token offsets start at 1!
		Map<Integer, CorefChain> graph = document
				.get(CorefChainAnnotation.class);

		return document;
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Config conf = ConfigFactory.loadConfiguration(Config.class,
				Config.DEFAULT);
		IOConfig ioConf = ConfigFactory.loadConfiguration(IOConfig.class,
				IOConfig.DEFAULT);

		Annotation parsedDoc;
		if ("parsed".equals(conf.getDocType())) {
			DocReader reader = new DocReader(ioConf.getParsedDir());
			parsedDoc = reader.read("APW19981116.0205.parsed");
			Doc test2 = XMLDoc.readXML("data/raw/APW19981116.0205");

			FirstSentSum naiveSum2 = new FirstSentSum(test2, parsedDoc,
					test2.cont.length());
			String sum = naiveSum2.summary();

			System.out.println("Before :"
					+ test2.cont.substring(0, test2.cont.indexOf(".")));
			System.out.println("Summary: " + sum);
		} else {
			// TODO: Generate doc summaries here

			// Just gonna mess around in here for a while
			int maxSummaryLength = 100; // in characters //TODO should be
										// retrieved
										// from args

			// Doc test = XMLDoc.readXML("data/raw/APW19981016.0240");
			Doc test2 = XMLDoc.readXML("data/raw/APW19981116.0205");
			/*
			 * Properties props = new Properties(); props.put("annotators",
			 * conf.getAnnotators()); StanfordCoreNLP pipeline = new
			 * StanfordCoreNLP(props); String text = test.cont; Annotation
			 * document = new Annotation(text); pipeline.annotate(document);
			 * Extractor feat = new Extractor(document); feat.runAll();
			 */

			DocReader readFile = new DocReader("data/parsed/");
			// Annotation document = readFile.read("APW19981016.0240.parsed");
			Annotation document2 = readFile.read("APW19981116.0205.parsed");
			// Summerizer naiveSum = new FirstSentSum(test,
			// document,maxSummaryLength);
			// System.out.println("Summary: "+naiveSum.summary());

			FirstSentSum naiveSum2 = new FirstSentSum(test2, document2,
					maxSummaryLength);
			System.out.println("FirstSent was: " + naiveSum2.getFirstSent());
			System.out.println("Summary2: " + naiveSum2.summary());

			int a = 1 + 1;
		}
	}

}
