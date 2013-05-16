package ethz.nlp.headgen.sum;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import ethz.nlp.headgen.Doc;
import ethz.nlp.headgen.data.CorpusCounts;
import ethz.nlp.headgen.data.TF_IDF;
import ethz.nlp.headgen.xml.XMLNews200;

public class Std_TF_IDF extends ArticleTopicNGramSum implements Summerizer {
  
	protected ArrayList<Doc> corpus;
	/**
	 * This Will automatically use the news200 corpus 
	 * @param doc
	 * @param summaryLength
	 */
	public Std_TF_IDF(Doc doc, int summaryLength) {
		super(doc, summaryLength);
		// TODO Auto-generated constructor stub
		corpus=XMLNews200.readXML(new File("data/newsspace200.xml"),200);
	}
	public Std_TF_IDF(Doc doc, int summaryLength, ArrayList<Doc> corpusForIDF){
		super(doc, summaryLength);
		this.corpus=corpusForIDF;
	}
	
	
	public String summary() {
		StringBuilder strBld = new StringBuilder();
		String out="######################################3";
		
		CorpusCounts idfCounts = CorpusCounts.generateCounts(corpus); //Un-lematized 
		
		Scanner scanner = new Scanner(doc.cont).useDelimiter(" ");
	
		TreeMap<Double, String> tfidf_byValue = new TreeMap<Double,String>();
		TreeMap<String, Double> tfidf_byName  = new TreeMap<String, Double>();
		
		while(scanner.hasNext()){
			String curWord = scanner.next();
			double relFreq=TF_IDF.calc(curWord, this.doc, idfCounts);
			tfidf_byName.put(curWord, relFreq);
			tfidf_byValue.put(relFreq,curWord);
		}
		scanner.close();
		
		ArrayList<Map.Entry<Double,String>> outList = new ArrayList<Map.Entry<Double,String>>();
		Map.Entry<Double, String> curEntry = tfidf_byValue.lastEntry();
		while(strBld.toString().length()<this.sumLeng){
			strBld.append(curEntry.getValue());
			outList.add(curEntry);
			curEntry=tfidf_byValue.floorEntry(curEntry.getKey());
		
		}
		
		return strBld.toString();
		
	}
	
	public static void main(String[] args){
		File inp = new File("data/duc2003_raw/APW19980605.0039");
		
		
		Doc text = new Doc();
	}

}
