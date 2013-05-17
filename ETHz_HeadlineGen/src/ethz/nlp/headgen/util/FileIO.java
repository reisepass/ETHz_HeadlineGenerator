package ethz.nlp.headgen.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileIO {
	private static int count;

	private FileIO() {
	}

	public static final String readTextFile(File f) throws IOException {
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		String line;
		try {
			br = new BufferedReader(new FileReader(f));
			while ((line = br.readLine()) != null) {
				line = line.replaceAll("\\([^\\(]*\\)", "");
				sb.append(line + " ");
			}
		} finally {
			if (br != null) {
				br.close();
			}
		}

		return sb.toString().trim();
	}

	public static final void collapseTextFiles(File outFile, File... dirs)
			throws IOException {
		count = 0;
		FileWriter fw = null;
		try {
			fw = new FileWriter(outFile, true);
			for (File dir : dirs) {
				writeFile(fw, dir);
			}
		} finally {
			if (fw != null) {
				fw.close();
			}
		}
	}

	private static void writeFile(FileWriter fw, File file) throws IOException {
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				writeFile(fw, f);
			}
		} else {
			System.out
					.println(++count + " - Collapsing file " + file.getName());
			fw.write("\n" + readTextFile(file));
		}
	}

	public static void main(String[] args) throws IOException {
		collapseTextFiles(new File("data/all_raw"), new File("data/ap_raw"),
				new File("data/duc2003_raw"));
	}
}
