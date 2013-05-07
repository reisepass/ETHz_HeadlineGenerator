package ethz.nlp.headgen.lda;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import ethz.nlp.headgen.util.FileIO;

public class DataFile {

	private File dir;

	public DataFile(File dir) {
		this.dir = dir;
	}

	public void createDataFile(String outFile) throws IOException {
		createDataFile(new File(outFile));
	}

	public void createDataFile(File outFile) throws IOException {
		FileWriter fw = null;
		String text;
		try {
			fw = new FileWriter(outFile);
			fw.write(dir.list().length + "\n");
			for (File f : dir.listFiles()) {
				try {
					text = FileIO.readTextFile(f);
					fw.write(RawToLDA.convert(text));
				} catch (IOException e) {
					continue;
				}
			}
		} finally {
			if (fw != null) {
				fw.close();
			}
		}
	}

	public static void main(String[] args) throws IOException {
		File rawDir = new File("data/duc2003_raw");
		File out = new File("data/LDA.dat");
		DataFile df = new DataFile(rawDir);
		df.createDataFile(out);
	}
}
