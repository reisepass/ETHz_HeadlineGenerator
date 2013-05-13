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
		int numFiles = getNumFiles(dir);

		FileWriter docMapWriter = null;
		FileWriter dataFileWriter = null;
		try {
			docMapWriter = new FileWriter(new File(outFile.getParentFile(),
					LDAProbsLoader.DOCMAP));
			dataFileWriter = new FileWriter(outFile);

			docMapWriter.write(numFiles + "\n");
			dataFileWriter.write(numFiles + "\n");
			writeFiles(dir, docMapWriter, dataFileWriter);
		} finally {
			if (dataFileWriter != null) {
				dataFileWriter.close();
			}
			if (docMapWriter != null) {
				docMapWriter.close();
			}
		}
	}

	private void writeFiles(File dir, FileWriter docMapWriter,
			FileWriter dataFileWriter) {
		for (File f : dir.listFiles()) {
			if (f.isDirectory()) {
				writeFiles(f, docMapWriter, dataFileWriter);
			} else {
				try {
					docMapWriter.write(f.getPath() + "\n");
					dataFileWriter.write(RawToLDA.convert(FileIO
							.readTextFile(f)) + "\n");
				} catch (IOException e) {
					continue;
				}
			}
		}
	}

	private int getNumFiles(File dir) {
		int count = 0;
		for (File f : dir.listFiles()) {
			if (f.isDirectory()) {
				count += getNumFiles(f);
			} else {
				count++;
			}
		}
		return count;
	}

	public static void main(String[] args) throws IOException {
		File rawDir = new File("data/raw");
		File out = new File("data/lda/test/newdocs.dat");
		DataFile df = new DataFile(rawDir);
		df.createDataFile(out);
	}
}
