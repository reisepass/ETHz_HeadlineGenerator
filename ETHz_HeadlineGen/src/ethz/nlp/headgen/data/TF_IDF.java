package ethz.nlp.headgen.data;

import java.io.IOException;

import ethz.nlp.headgen.Doc;
import ethz.nlp.headgen.io.SerializableWrapper;

public class TF_IDF {
	private TF_IDF() {
	}

	public static double calc(String word, Doc d, CorpusCounts c) {
		double docCount;
		if (d.getWordCount().getValueForExactKey(word) == null)
			docCount = 0;
		else
			docCount = d.getWordCount().getValueForExactKey(word).getCount();
		double maxCount = d.getWordCount().getMax();
		double numDocs = c.getNumDocs();
		double docAppearanceCount;

		if (c.getDocAppearanceCounts().getValueForExactKey(word) == null)
			docAppearanceCount = 0;
		else
			docAppearanceCount = c.getDocAppearanceCounts()
					.getValueForExactKey(word).getCount();
		return (docCount / maxCount)
				* (Math.log(numDocs / (1 + docAppearanceCount)));
	}

	public static void main(String[] args) throws IOException {
		 CorpusCounts counts = CorpusCounts
		 .generateCountsFromCollapsed("data/all_raw");
		 SerializableWrapper sw = new SerializableWrapper(counts);
		 sw.save("data/all_raw_counts");
//		CorpusCounts counts = SerializableWrapper
//				.readObject("data/all_raw_counts");
//		System.out.println(counts.getNumDocs());
//		System.out.println(counts.getDocAppearanceCounts().getMax());

		// System.out.println("Generating corpus word counts");
		// CorpusCounts counts = CorpusCounts.generateCounts(m.documents);
		// System.out.println("--TF-IDF Values--");
		// SortedSet<String> sortedVals = new TreeSet<String>(
		// new Comparator<String>() {
		//
		// @Override
		// public int compare(String o1, String o2) {
		// double val1 = Double.parseDouble(o1.substring(o1
		// .lastIndexOf(":") + 1));
		// double val2 = Double.parseDouble(o2.substring(o2
		// .lastIndexOf(":") + 1));
		// return Double.compare(val2, val1);
		// }
		// });
		// String word;
		// double val;
		// for (CoreLabel token : m.documents.get(0).annotation
		// .get(TokensAnnotation.class)) {
		// word = token.getString(TextAnnotation.class);
		// val = TF_IDF.calc(word, m.documents.get(0), counts);
		// sortedVals.add(word + ":" + val);
		// }
		// for (String s : sortedVals) {
		// System.out.println("\t" + s);
		// }
	}
}
