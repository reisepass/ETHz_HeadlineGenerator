package ethz.nlp.headgen.lda;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DataFileFromCollapsed {
	private int count = 0, numFiles;
	private File collapsedTextFile;

	public DataFileFromCollapsed(File collapsedTextFile) {
		this.collapsedTextFile = collapsedTextFile;
	}

	public void createDataFile(String outFile) throws IOException {
		createDataFile(new File(outFile));
	}

	public void createDataFile(File outFile) throws IOException {
		numFiles = getNumFiles(collapsedTextFile);

		FileWriter dataFileWriter = null;
		try {
			dataFileWriter = new FileWriter(outFile);

			dataFileWriter.write(numFiles + "\n");
			writeFile(collapsedTextFile, dataFileWriter);
		} finally {
			if (dataFileWriter != null) {
				dataFileWriter.close();
			}
		}
	}

	private void writeFile(File file, FileWriter dataFileWriter)
			throws IOException {
		BufferedReader br = null;
		String line;
		try {
			br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {
				if (++count % 100 == 0) {
					System.out
							.println("Writing file " + count + "/" + numFiles);
				}
				dataFileWriter.write(RawToLDA.convert(line) + "\n");
			}
		} finally {
			if (br != null) {
				br.close();
			}
		}
	}

	private int getNumFiles(File file) throws IOException {
		int count = 0;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			while (br.readLine() != null) {
				count++;
			}
		} finally {
			if (br != null) {
				br.close();
			}
		}
		return count;
	}

	public static void main(String[] args) throws IOException {
		File collapsed = new File("data/all_raw");
		File out = new File("data/lda/LDA2.dat");
		DataFileFromCollapsed df = new DataFileFromCollapsed(collapsed);
		df.createDataFile(out);
	}
}
