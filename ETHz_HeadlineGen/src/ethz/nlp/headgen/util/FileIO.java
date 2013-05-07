package ethz.nlp.headgen.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileIO {
	private FileIO() {
	}

	public static final String readTextFile(File f) throws IOException {
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		String line;
		try {
			br = new BufferedReader(new FileReader(f));
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
		} finally {
			if (br != null) {
				br.close();
			}
		}

		return sb.toString();
	}
}
