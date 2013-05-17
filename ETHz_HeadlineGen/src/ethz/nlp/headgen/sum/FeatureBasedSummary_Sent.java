
package ethz.nlp.headgen.sum;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;
import ethz.nlp.headgen.Doc;  

public class FeatureBasedSummary_Sent extends FeatureBasedSummary implements Summerizer{
	
	public FeatureBasedSummary_sent(Doc doc, int length, Feature... features) {
		super(Doc doc, int length, features);
	}

	public String summary() {
		Annotation ano = doc.getAno();
		List<CoreMap> sentences = ano.get(SentencesAnnotation.class);
		String bestSentence="";
		double bestScore = -10000000;  
		for(CoreMap sentence : sentences ){
			double sentScore=0;
			int sentLength=0;
			String sentPrint="";
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				String wrd = token.get(TextAnnotation.class);
				String pos = token.get(PartOfSpeechAnnotation.class);
				sentLength=sentLength+wrd.length();
				sentScore=sentScore+scoreWord(wrd);
				sentPrint=sentPrint+wrd;
			}
			double perCharacterScore= sentScore/sentLength;
			if(sentLength>this.sumLeng){  //Pruning off score for sentences that will have to be shortend later 
				int dif= sentLenght-this.sumLeng;
				sentScore = sentScore - perCharacterScore*dif;
			}
			if(sentScore>bestScore){
				bestScore = sentScore;
				bestSentence = sentPrint; 
			}
		}
		
		if(bestSentence.equals("")){
			return "ERROR"; // should not be possible to get here 
		}   
		else
			return bestSentence; //TODO this sentence may need to be pruned 
	
	
	
	}


}
