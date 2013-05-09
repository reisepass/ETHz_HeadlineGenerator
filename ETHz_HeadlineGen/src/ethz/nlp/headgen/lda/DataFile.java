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
		FileWriter docMapWriter = null;
		FileWriter dataFileWriter = null;
		String text;
		try {
			docMapWriter = new FileWriter(new File(outFile.getParentFile(),
					LDAProbsLoader.DOCMAP));
			dataFileWriter = new FileWriter(outFile);

			docMapWriter.write(dir.list().length + "\n");
			dataFileWriter.write(dir.list().length + "\n");
			for (File f : dir.listFiles()) {
				try {
					docMapWriter.write(f.getName() + "\n");
					text = FileIO.readTextFile(f);
					dataFileWriter.write(RawToLDA.convert(text) + "\n");
				} catch (IOException e) {
					continue;
				}
			}
		} finally {
			if (dataFileWriter != null) {
				dataFileWriter.close();
			}
			if (docMapWriter != null) {
				docMapWriter.close();
			}
		}
	}

	public static void main(String[] args) throws IOException {
		File rawDir = new File("data/duc2003_raw");
		File out = new File("data/lda/LDA.dat");
		DataFile df = new DataFile(rawDir);
		df.createDataFile(out);
	}
}
