package ethz.nlp.headgen.rouge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class RougeResults {
	public static final Pattern NGRAM_PATTERN = Pattern
			.compile(".*ROUGE-\\d.*");
	public static final Pattern W_PATTERN = Pattern.compile(".*ROUGE-W.*");
	public static final Pattern L_PATTERN = Pattern.compile(".*ROUGE-L.*");
	public static final Pattern R_PATTERN = Pattern.compile(".*_R:.*");
	public static final Pattern P_PATTERN = Pattern.compile(".*_P:.*");
	public static final Pattern F_PATTERN = Pattern.compile(".*_F:.*");

	private List<Double> ngramAverage_R = new ArrayList<Double>();
	private List<Double> ngramAverage_P = new ArrayList<Double>();
	private List<Double> ngramAverage_F = new ArrayList<Double>();
	private double averageL_R;
	private double averageL_P;
	private double averageL_F;
	private double averageW_R;
	private double averageW_P;
	private double averageW_F;

	private RougeResults() {
	}

	public static RougeResults parseResults(String string) {
		RougeResults results = new RougeResults();

		BufferedReader br = new BufferedReader(new StringReader(string));
		String line;
		try {
			while ((line = br.readLine()) != null) {
				parseLine(line, results);
			}
		} catch (IOException e) {
			// Shouldn't happen reading from a StringReader
			throw new RuntimeException(e);
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// Again, this shouldn't happen
				throw new RuntimeException(e);
			}
		}
		return results;
	}

	private static void parseLine(String line, RougeResults results) {
		if (NGRAM_PATTERN.matcher(line).matches()) {
			if (P_PATTERN.matcher(line).matches()) {
				results.ngramAverage_P.add(getVal(line));
			} else if (R_PATTERN.matcher(line).matches()) {
				results.ngramAverage_R.add(getVal(line));
			} else if (F_PATTERN.matcher(line).matches()) {
				results.ngramAverage_F.add(getVal(line));
			}
		} else if (W_PATTERN.matcher(line).matches()) {
			if (P_PATTERN.matcher(line).matches()) {
				results.averageW_P = getVal(line);
			} else if (R_PATTERN.matcher(line).matches()) {
				results.averageW_R = getVal(line);
			} else if (F_PATTERN.matcher(line).matches()) {
				results.averageW_F = getVal(line);
			}
		} else if (L_PATTERN.matcher(line).matches()) {
			if (P_PATTERN.matcher(line).matches()) {
				results.averageL_P = getVal(line);
			} else if (R_PATTERN.matcher(line).matches()) {
				results.averageL_R = getVal(line);
			} else if (F_PATTERN.matcher(line).matches()) {
				results.averageL_F = getVal(line);
			}
		}
	}

	private static Double getVal(String line) {
		int start, end;
		start = line.indexOf("0.");
		end = line.indexOf(" (");
		return Double.parseDouble(line.substring(start, end));
	}

	public int maxNgrams() {
		return ngramAverage_F.size();
	}

	public double getNgramAvgP(int n) {
		return getVal(ngramAverage_P, n - 1);
	}

	public double getNgramAvgR(int n) {
		return getVal(ngramAverage_R, n - 1);
	}

	public double getNgramAvgF(int n) {
		return getVal(ngramAverage_F, n - 1);
	}

	private double getVal(List<Double> list, int n) {
		return (n < maxNgrams() ? list.get(n) : -1);
	}

	public double getAvgL_P() {
		return averageL_P;
	}

	public double getAvgL_R() {
		return averageL_R;
	}

	public double getAvgL_F() {
		return averageL_F;
	}

	public double getAvgW_P() {
		return averageW_P;
	}

	public double getAvgW_R() {
		return averageW_R;
	}

	public double getAvgW_F() {
		return averageW_F;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i <= maxNgrams(); i++) {
			sb.append("ROUGE-" + i + " Average_P: " + getNgramAvgP(i) + "\n");
			sb.append("ROUGE-" + i + " Average_R: " + getNgramAvgR(i) + "\n");
			sb.append("ROUGE-" + i + " Average_F: " + getNgramAvgF(i) + "\n");
		}
		sb.append("ROUGE-L Average_P: " + getAvgL_P() + "\n");
		sb.append("ROUGE-L Average_R: " + getAvgL_R() + "\n");
		sb.append("ROUGE-L Average_F: " + getAvgL_F() + "\n");
		sb.append("ROUGE-W Average_P: " + getAvgW_P() + "\n");
		sb.append("ROUGE-W Average_R: " + getAvgW_R() + "\n");
		sb.append("ROUGE-W Average_F: " + getAvgW_F() + "\n");
		return sb.toString();
	}
}
