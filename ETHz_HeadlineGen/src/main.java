
import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.trees.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.util.CoreMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
 

public class main {
	
	public Doc fileRead(String fileName){
		try {
			 
			File fXmlFile = new File(fileName);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
		 
			//optional, but recommended
			//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();
		 
			//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
		 
			NodeList main = doc.getElementsByTagName(doc.getDocumentElement().getNodeName());
			NodeList docno = doc.getElementsByTagName("DOCNO");
			NodeList docTy = doc.getElementsByTagName("DOCTYPE");
			NodeList type  = doc.getElementsByTagName("TXTTYPE");
			NodeList text  = doc.getElementsByTagName("TEXT");
			;
			Doc inputDoc = new Doc(docno.item(0).getTextContent(),docTy.item(0).getTextContent(),type.item(0).getTextContent(),text.item(0).getTextContent());
			
			//System.out.println("Checking text content of doc:\n\n"+inputDoc.toString());
		 
			
			return inputDoc;
		    } catch (Exception e) {
			e.printStackTrace();
			return null;
		    }
		  }
	
	
		
	
	
	public main() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Annotation nlpTest(String data){
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
	    Properties props = new Properties();
	    props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
	    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	    
	    // read some text in the text variable
	    String text = data;
	    
	    // create an empty Annotation just with the given text
	    Annotation document = new Annotation(text);
	    
	    // run all Annotators on this text
	    pipeline.annotate(document);
	    
	    // these are all the sentences in this document
	    // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
	    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	    
	    for(CoreMap sentence: sentences) {
	      // traversing the words in the current sentence
	      // a CoreLabel is a CoreMap with additional token-specific methods
	      for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
	        // this is the text of the token
	        String word = token.get(TextAnnotation.class);
	        // this is the POS tag of the token
	        String pos = token.get(PartOfSpeechAnnotation.class);
	        // this is the NER label of the token
	        String ne = token.get(NamedEntityTagAnnotation.class);       
	        
	        System.out.print(word+"("+pos+")["+ne+"]");
	      }System.out.println();

	      // this is the parse tree of the current sentence
	      Tree tree = sentence.get(TreeAnnotation.class);

	      // this is the Stanford dependency graph of the current sentence
	      SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
	      int a =1+1; 
	    }

	    // This is the coreference link graph
	    // Each chain stores a set of mentions that link to each other,
	    // along with a method for getting the most representative mention
	    // Both sentence and token offsets start at 1!
	    Map<Integer, CorefChain> graph = 
	      document.get(CorefChainAnnotation.class);
	    
	    return document;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Just gonna mess around in here for a while
		int maxSummaryLength=100;  // in characters	 //TODO should be retrieved from args	
		main t = new main();
		
		
		//TODO parameterize this with input form args ^
		Doc test=t.fileRead("APW19981022.0269");
		t.nlpTest(test.cont);
		
		
		
		
		Properties props = new Properties();
	    //props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		props.put("annotators", "tokenize, ssplit, pos");
	    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	    String text = test.cont;
	    Annotation document = new Annotation(text);
	    pipeline.annotate(document);
		Extractor feat=new Extractor(document);
		feat.runAll();
		
		Summerizer naiveSumm = new FirstSentSum(test,document,maxSummaryLength);
	
		int a = 1+1;
		
	}

	

}
