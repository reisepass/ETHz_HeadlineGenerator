package ethz.nlp.headgen;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
import ethz.nlp.headgen.io.IOConfig;
import ethz.nlp.headgen.io.ParsedDocReader;
import ethz.nlp.headgen.io.ParsedDocWriter;
import ethz.nlp.headgen.rouge.RougeEvalBuilder;
import ethz.nlp.headgen.rouge.RougeScript;
import ethz.nlp.headgen.sum.FirstBaseline;
import ethz.nlp.headgen.sum.NeFreqBasedSum;
import ethz.nlp.headgen.sum.NeNounFreqSum;
import ethz.nlp.headgen.sum.Summerizer;
import ethz.nlp.headgen.util.ConfigFactory;
import ethz.nlp.headgen.xml.XMLDoc;

public class main {

	public static final int DEFAULT_MAX_SUMMARY_LENGTH = 100;

	private Config conf;
	private IOConfig ioConf;

	private List<Doc> documents = new ArrayList<Doc>();

	private StanfordCoreNLP pipeline;

	public main(Config conf, IOConfig ioConf) {
		this.conf = conf;
		this.ioConf = ioConf;
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

		main m = new main(conf, ioConf);

		m.loadFiles();
		for (Doc d : m.documents) {
			m.generateSummary(d, new NeNounFreqSum(d,d.annotation,
					DEFAULT_MAX_SUMMARY_LENGTH));
		}
		for (Doc d : m.documents) {
			System.out.println(d.summary);
		}

		String rougeInFile = "ROUGE-IN.xml", rougeOutFile = "FirstBaselineOutput-2";
		m.writeSummaries();
		RougeEvalBuilder reb = m.genRouge();
		reb.write(rougeInFile);

		RougeScript rs = new RougeScript(conf.getRougePath(), 95, 500, 2, 1.2);
		rs.run(rougeInFile, rougeOutFile);
	}

	private RougeEvalBuilder genRouge() throws IOException {
		Map<Doc, String[]> evalMap = linkModels();
		RougeEvalBuilder reb = new RougeEvalBuilder(ioConf.getOutputDir(),
				ioConf.getModelDir());
		reb.addEvals(evalMap);
		return reb;
	}

	private void writeSummaries() throws IOException {
		File outputDir = new File(ioConf.getOutputDir());
		if (!outputDir.exists()) {
			if (outputDir.mkdirs()) {
				throw new IOException("Unable to create the output directory: "
						+ outputDir.getAbsolutePath());
			}
		}

		FileWriter fw = null;
		String fileName;
		for (Doc d : documents) {
			fileName = d.f.getName();

			try {
				fw = new FileWriter(new File(outputDir, fileName));
				fw.write(d.summary);
			} finally {
				if (fw != null) {
					fw.close();
				}
			}

		}
	}

	private void loadFiles() throws IOException {
		File rawDir = new File(ioConf.getRawDir());

		if (!rawDir.exists() || !rawDir.isDirectory()) {
			throw new IOException(rawDir.getAbsolutePath()
					+ " is not a valid directory");
		}

		Doc doc;
		for (File d : rawDir.listFiles()) {
			if (d.isDirectory()) {
				for (File f : d.listFiles()) {
					doc = XMLDoc.readXML(f);
					documents.add(doc);

					addAnnotation(doc);
				}
			}
		}
	}

	private Map<Doc, String[]> linkModels() {
		// Mapping of doc names to models
		Map<Doc, String[]> map = new HashMap<Doc, String[]>();

		File modelDir = new File(ioConf.getModelDir());

		String prefix, suffix;
		for (Doc d : documents) {
			// Set model prefix/suffix
			prefix = d.getParentDirName();
			prefix = prefix.substring(0, prefix.length() - 1).toUpperCase();

			suffix = d.f.getName();
			suffix = suffix.substring(suffix.lastIndexOf(".") + 1);

			// Add doc/models to the map
			map.put(d, modelDir.list(new ModelFileFilter(prefix, suffix)));
		}
		return map;
	}

	private void addAnnotation(Doc doc) throws IOException {
		File parentDir = new File(ioConf.getParsedDir(), doc.getParentDirName());
		File anotFile = new File(parentDir, doc.getAnotFileName());
		if (!anotFile.exists()) {
			genAnnotation(doc);
			saveAnnotation(doc, anotFile);
		} else {
			doc.annotation = ParsedDocReader.read(anotFile);
		}
	}

	private void saveAnnotation(Doc doc, File outFile) throws IOException {
		// Create the parsed output parent directory
		File parentDir = outFile.getParentFile();
		if (!parentDir.exists()) {
			if (!parentDir.mkdirs()) {
				throw new IOException(
						"Unable to create parsed output directory: "
								+ parentDir);
			}
		}

		ParsedDocWriter.writeOutput(doc.annotation, outFile);
	}

	private void genAnnotation(Doc doc) {
		// Create the pipeline if it doesn't yet exist
		if (pipeline == null) {
			Properties props = new Properties();
			props.put("annotators", conf.getAnnotators());
			pipeline = new StanfordCoreNLP(props);
		}
		Annotation anot = new Annotation(doc.cont);
		pipeline.annotate(anot);
		doc.annotation = anot;
	}

	private void generateSummary(Doc d, Summerizer s) {
		d.summary = s.summary();
	}

	// if ("parsed".equals(conf.getDocType())) {
	// annotations = new ArrayList<Annotation>(1);
	// DocReader reader = new DocReader(ioConf.getParsedDir());
	// annotations.add(reader.read("APW19981116.0205.parsed"));
	// Doc test2 = XMLDoc.readXML("data/raw/APW19981116.0205");
	//
	// FirstSentSum naiveSum2 = new FirstSentSum(test2, parsedDoc,
	// test2.cont.length());
	// String sum = naiveSum2.summary();
	//
	// System.out.println("Before :"
	// + test2.cont.substring(0, test2.cont.indexOf(".")));
	// System.out.println("Summary: " + sum);
	//
	// NeFreqBasedSum naiveSum3 = new NeFreqBasedSum(test2, parsedDoc,
	// test2.cont.length());
	// System.out.println("Summary3: " + naiveSum3.summary());
	// } else {
	// // TODO: Generate doc summaries here
	//
	// // Just gonna mess around in here for a while
	// int maxSummaryLength = 100; // in characters //TODO should be
	// // retrieved
	// // from args
	//
	// // Doc test = XMLDoc.readXML("data/raw/APW19981016.0240");
	// Doc test2 = XMLDoc.readXML("data/raw/APW19981116.0205");
	// /*
	// * Properties props = new Properties(); props.put("annotators",
	// * conf.getAnnotators()); StanfordCoreNLP pipeline = new
	// * StanfordCoreNLP(props); String text = test.cont; Annotation
	// * document = new Annotation(text); pipeline.annotate(document);
	// * Extractor feat = new Extractor(document); feat.runAll();
	// */
	//
	// DocReader readFile = new DocReader("data/parsed/");
	// // Annotation document = readFile.read("APW19981016.0240.parsed");
	// Annotation document2 = readFile.read("APW19981116.0205.parsed");
	// // Summerizer naiveSum = new FirstSentSum(test,
	// // document,maxSummaryLength);
	// // System.out.println("Summary: "+naiveSum.summary());
	//
	// FirstSentSum naiveSum2 = new FirstSentSum(test2, document2,
	// maxSummaryLength);
	// System.out.println("FirstSent was: " + naiveSum2.getFirstSent());
	// System.out.println("Summary2: " + naiveSum2.summary());
	//
	// int a = 1 + 1;
	// }
	public class ModelFileFilter implements FilenameFilter {
		private String prefix, suffix;

		public ModelFileFilter(String prefix, String suffix) {
			this.prefix = prefix;
			this.suffix = suffix;
		}

		@Override
		public boolean accept(File dir, String name) {
			return name.startsWith(prefix) && name.endsWith(suffix);
		}
	}
}
