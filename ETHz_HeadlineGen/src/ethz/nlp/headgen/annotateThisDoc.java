package ethz.nlp.headgen;

import java.util.Properties;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class annotateThisDoc {
  public  	Properties  props;
	public  StanfordCoreNLP pipeline;
	public annotateThisDoc() {
		// TODO Auto-generated constructor stub
			props = new Properties();
			props.put("annotators", "tokenize");
			pipeline = new StanfordCoreNLP(props);
		
	}
	public  Annotation anoThis(String cont){
		
		Annotation anoOut = new Annotation(cont);
		
		
		pipeline.annotate(anoOut);
		return anoOut;
	}

}
