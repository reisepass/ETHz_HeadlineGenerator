package ethz.nlp.headgen.rouge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;

public class RougeScript {
	private String rougePath;
	private int confidence;
	private int nBytes;
	private int nGrams;
	private double weight;

	public RougeScript(String rougePath, int confidence, int nBytes,
			int nGrams, double weight) {
		this.confidence = confidence;
		this.nBytes = nBytes;
		this.nGrams = nGrams;
		this.weight = weight;

		if (!rougePath.endsWith("/")) {
			rougePath += "/";
		}

		this.rougePath = rougePath;
	}

	public void run(String inFile, String outFile) throws IOException {
		System.out.println(genCommand(inFile));
		Process p = Runtime.getRuntime().exec(genCommand(inFile));

		writeOutput(p, new FileWriter(new File(outFile)));
		try {
			if (p.waitFor() != 0) {
				throw new IOException("Error running ROUGE script");
			}
		} catch (InterruptedException e) {
			throw new IOException(e);
		}
	}

	public RougeResults run(String inFile) throws IOException {
		System.out.println(genCommand(inFile));
		Process p = Runtime.getRuntime().exec(genCommand(inFile));

		StringWriter sw = new StringWriter();
		writeOutput(p, sw);
		try {
			if (p.waitFor() != 0) {
				throw new IOException("Error running ROUGE script");
			}
		} catch (InterruptedException e) {
			throw new IOException(e);
		}
		return RougeResults.parseResults(sw.toString());
	}

	private void writeOutput(Process p, Writer writer) throws IOException {
		BufferedReader br = null;
		String line = null;
		try {
			br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = br.readLine()) != null) {
				writer.write(line + "\n");
			}
		} finally {
			if (br != null) {
				br.close();
			}
			if (writer != null) {
				writer.close();
			}
		}
	}

	/*
	 * Template: ROUGE/RELEASE-1.5.5/ROUGE-1.5.5.pl -e ROUGE/RELEASE-1.5.5/data
	 * -a -c 95 -b 75 -m -n 4 -w 1.2 t1.rouge.in > output
	 */
	private String genCommand(String inFile) {
		return rougePath + "ROUGE-1.5.5.pl -e " + rougePath + "data -a -c "
				+ confidence + " -b " + nBytes + " -m -n " + nGrams + " -w "
				+ weight + " " + inFile;
	}
}
