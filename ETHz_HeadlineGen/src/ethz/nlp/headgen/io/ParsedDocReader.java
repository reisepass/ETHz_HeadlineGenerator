package ethz.nlp.headgen.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.LinkedList;
import java.util.List;

import edu.stanford.nlp.pipeline.Annotation;

public class ParsedDocReader {
	public static List<Annotation> readAll(String dir) throws IOException {
		return readAll(new File(dir));
	}

	public static List<Annotation> readAll(File dir) throws IOException {
		if (!dir.isDirectory() || !dir.exists()) {
			throw new IOException(dir + " is not a valid directory");
		}

		List<Annotation> docs = new LinkedList<Annotation>();
		for (File f : dir.listFiles()) {
			docs.add(read(f));
		}

		return docs;
	}

	public static Annotation read(String f) throws IOException {
		return read(new File(f));
	}

	public static Annotation read(File f) throws IOException {
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
}
