package ethz.nlp.headgen.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

import edu.stanford.nlp.pipeline.Annotation;
import ethz.nlp.headgen.Config;
import ethz.nlp.headgen.DocParser;
import ethz.nlp.headgen.util.ConfigFactory;

public class DocWriter {
	private File dir;

	public DocWriter(File dir) {
		this.dir = dir;
	}

	public void writeOutput(Annotation annotation, String outFile)
			throws IOException {
		ParsedDoc out = new ParsedDoc(annotation);
		File f = new File(dir, outFile);
		writeOut(out, f);
	}

	private void writeOut(ParsedDoc out, File f) throws IOException {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(f));
			oos.writeObject(out);
		} finally {
			if (oos != null) {
				oos.close();
			}
		}
	}

	public static void main(String[] args) throws IOException {
		Config conf = ConfigFactory.loadConfiguration(Config.class,
				Config.DEFAULT);
		IOConfig ioConf = ConfigFactory.loadConfiguration(IOConfig.class,
				IOConfig.DEFAULT);
		File outDir = new File(ioConf.getParsedDir());

		DocParser parser = new DocParser(conf.getAnnotators());
		DocWriter writer = new DocWriter(outDir);

		Map<String, Annotation> parsedDocs = parser.parseFiles(ioConf
				.getRawDir());

		for (String k : parsedDocs.keySet()) {
			System.out.println("Writing file " + k);
			writer.writeOutput(parsedDocs.get(k), k + ".parsed");
		}
	}
}
