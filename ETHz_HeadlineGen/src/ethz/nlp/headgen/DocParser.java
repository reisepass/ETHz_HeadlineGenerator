package ethz.nlp.headgen;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import ethz.nlp.headgen.xml.XMLDoc;

public class DocParser {
	StanfordCoreNLP pipeline;

	public DocParser(String annotators) {
		Properties props = new Properties();
		props.put("annotators", annotators);
		pipeline = new StanfordCoreNLP(props);
	}

	public Annotation parse(String text) {
		Annotation document = new Annotation(text);
		pipeline.annotate(document);
		return document;
	}

	public Map<String, Annotation> parseFiles(String inputDir)
			throws IOException {
		File dir = new File(inputDir);
		Map<String, Annotation> parsedDocs = new HashMap<String, Annotation>();

		if (!dir.isDirectory()) {
			throw new IOException(dir.getAbsolutePath()
					+ " is not a valid directory");
		}

		// Parse all the documents in the directory
		for (File f : dir.listFiles()) {
			System.out.println("Reading file: " + f.getName());
			Doc d = XMLDoc.readXML(f);
			System.out.println("Parsing file: " + f.getName());
			parsedDocs.put(f.getName(), parse(d.cont));
		}

		return parsedDocs;
	}
}
