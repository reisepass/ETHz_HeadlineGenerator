package ethz.nlp.headgen.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import edu.stanford.nlp.pipeline.Annotation;

public class ParsedDocWriter {

	public static void writeOutput(Annotation annotation, String outFile)
			throws IOException {
		writeOutput(annotation, new File(outFile));
	}

	public static void writeOutput(Annotation annotation, File outFile)
			throws IOException {
		ParsedDoc out = new ParsedDoc(annotation);
		writeOut(out, outFile);
	}

	private static void writeOut(ParsedDoc out, File f) throws IOException {
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
}
