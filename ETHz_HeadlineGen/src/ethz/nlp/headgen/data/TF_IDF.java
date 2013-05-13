package ethz.nlp.headgen.data;

import ethz.nlp.headgen.Doc;

public class TF_IDF {
	private TF_IDF() {
	}

	public static double calc(String word, Doc d, CorpusCounts c) {
		double docCount = d.wordCounts.getValueForExactKey(word).getCount();
		double maxCount = d.wordCounts.getMax();
		double numDocs = c.getNumDocs();
		double docAppearanceCount = c.getDocAppearanceCounts()
				.getValueForExactKey(word).getCount();
		return (docCount / maxCount)
				* (Math.log(numDocs / (1 + docAppearanceCount)));
	}

	public static void main(String[] args) {
		/*
		 * // System.out.println("Generating corpus word counts"); //
		 * CorpusCounts counts = CorpusCounts.generateCounts(m.documents); //
		 * System.out.println("--TF-IDF Values--"); // SortedSet<String>
		 * sortedVals = new TreeSet<String>( // new Comparator<String>() { //
		 * 
		 * @Override // public int compare(String o1, String o2) { // double
		 * val1 = Double.parseDouble(o1.substring(o1 // .lastIndexOf(":") + 1));
		 * // double val2 = Double.parseDouble(o2.substring(o2 //
		 * .lastIndexOf(":") + 1)); // return Double.compare(val2, val1); // }
		 * // }); // String word; // double val; // for (CoreLabel token :
		 * m.documents.get(0).annotation // .get(TokensAnnotation.class)) { //
		 * word = token.getString(TextAnnotation.class); // val =
		 * TF_IDF.calc(word, m.documents.get(0), counts); // sortedVals.add(word
		 * + ":" + val); // } // for (String s : sortedVals) { //
		 * System.out.println("\t" + s); // }
		 */
	}
}
