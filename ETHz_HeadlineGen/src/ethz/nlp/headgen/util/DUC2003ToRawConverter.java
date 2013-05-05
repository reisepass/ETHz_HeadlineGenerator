package ethz.nlp.headgen.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DUC2003ToRawConverter {
	private File inDir, outDir;

	public DUC2003ToRawConverter(String inDir, String outDir) {
		this(new File(inDir), new File(outDir));
	}

	public DUC2003ToRawConverter(File inDir, File outDir) {
		this.inDir = inDir;
		this.outDir = outDir;
	}

	public void convert() throws IOException {
		convertFile(inDir);
	}

	private void convertFile(File in) throws IOException {
		if (in.isDirectory()) {
			for (File f : in.listFiles()) {
				convertFile(f);
			}
		} else {
			FileWriter fw = null;
			File outFile;
			String text;
			try {
				text = getRawText(in);

				outFile = new File(outDir, in.getName());
				fw = new FileWriter(outFile);
				fw.write(text);
			} finally {
				if (fw != null) {
					fw.close();
				}
			}
		}
	}

	private String getRawText(File f) throws IOException {
		BufferedReader br = null;
		StringBuilder text = new StringBuilder();
		String line;
		boolean read = false;
		try {
			br = new BufferedReader(new FileReader(f));
			while ((line = br.readLine()) != null) {
				if (line.startsWith("<TEXT>") || line.startsWith("</TEXT>")) {
					read = !read;
					continue;
				}
				if (read && !line.startsWith("<P>") && !line.startsWith("</P>")) {
					text.append(line + "\n");
				}
			}
		} finally {
			if (br != null) {
				br.close();
			}
		}
		return text.toString();
	}

	public static void main(String[] args) throws IOException {
		DUC2003ToRawConverter converter = new DUC2003ToRawConverter(
				"./data/duc2003_original", "./data/duc2003_raw");
		converter.convert();
	}
}
