package ethz.nlp.headgen.rouge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class RougeScript {
	private String rougePath;
	private int confidence;
	private int nBytes;
	private int nGrams;
	private double weight;

	public RougeScript(String rougePath, int confidence, int nBytes,
			int nGrams, double weight) {
		this.rougePath = rougePath;
		this.confidence = confidence;
		this.nBytes = nBytes;
		this.nGrams = nGrams;
		this.weight = weight;

		if (!rougePath.endsWith("/")) {
			rougePath += "/";
		}
	}

	public void run(String inFile, String outFile) throws IOException {
		System.out.println(genCommand(inFile, outFile));
		Process p = Runtime.getRuntime().exec(genCommand(inFile, outFile));

		writeOutput(p, new File(outFile));
		try {
			if (p.waitFor() != 0) {
				throw new IOException("Error running ROUGE script");
			}
		} catch (InterruptedException e) {
			throw new IOException(e);
		}
	}

	private void writeOutput(Process p, File outFile) throws IOException {
		FileWriter fw = null;
		BufferedReader br = null;
		String line = null;
		try {
			br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			fw = new FileWriter(outFile);
			while ((line = br.readLine()) != null) {
				fw.write(line + "\n");
			}
		} finally {
			if (br != null) {
				br.close();
			}
			if (fw != null) {
				fw.close();
			}
		}
	}

	/*
	 * Template: ROUGE/RELEASE-1.5.5/ROUGE-1.5.5.pl -e ROUGE/RELEASE-1.5.5/data
	 * -a -c 95 -b 75 -m -n 4 -w 1.2 t1.rouge.in > output
	 */
	private String genCommand(String inFile, String outFile) {
		return rougePath + "ROUGE-1.5.5.pl -e " + rougePath + "data -a -c "
				+ confidence + " -b " + nBytes + " -m -n " + nGrams + " -w "
				+ weight + " " + inFile + " > " + outFile;
	}
}
