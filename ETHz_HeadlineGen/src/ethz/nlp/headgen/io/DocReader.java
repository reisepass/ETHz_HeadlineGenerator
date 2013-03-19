package ethz.nlp.headgen.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.LinkedList;
import java.util.List;

import edu.stanford.nlp.pipeline.Annotation;
import ethz.nlp.headgen.util.ConfigFactory;

public class DocReader {
	public DocReader() {
	}

	public List<Annotation> readAll(File dir) throws IOException {
		if (!dir.isDirectory()) {
			throw new IOException(dir + " is not a valid directory");
		}

		List<Annotation> docs = new LinkedList<Annotation>();
		for (File f : dir.listFiles()) {
			docs.add(read(f));
		}

		return docs;
	}

	public Annotation read(File f) throws IOException {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(f));
			ParsedDoc doc = (ParsedDoc) ois.readObject();
			return doc.getAnnotation();
		} catch (ClassNotFoundException e) {
			throw new IOException(e);
		} finally {
			if (ois != null) {
				ois.close();
			}
		}
	}

	public static void main(String[] args) throws IOException {
		IOConfig ioConf = ConfigFactory.loadConfiguration(IOConfig.class,
				IOConfig.DEFAULT);
		File outputDir = new File(ioConf.getParsedDir());

		DocReader reader = new DocReader();

		List<Annotation> docs = reader.readAll(outputDir);
		System.out.println("# Docs read: " + docs.size());
	}
}
