package ethz.nlp.headgen.xml;


import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ethz.nlp.headgen.Doc;

public class XMLNews200 {
	public static ArrayList<Doc> XMLNews200() {
		return readXML(new File("data/newsspace200.xml"));
	}
	public static ArrayList<Doc> XMLNews200(String f) {
		return readXML(new File(f));
	}

	public static void main(String[] args){
		ArrayList<Doc> output = XMLNews200.readXML(new File("data/newsspace200.xml"));
		int a = 1+1;
		//System.out.println(output.toString());
	}
	
	public static ArrayList<Doc> readXML(File f) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document docIN = dBuilder.parse(f);
			
			
			// optional, but recommended
			// read this -
			// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			docIN.getDocumentElement().normalize();

			NodeList title =  docIN.getElementsByTagName("title");
							
			NodeList descr  = docIN.getElementsByTagName("description");
					
			ArrayList<Doc> outDoc = new ArrayList<Doc>();
			Random ran = new Random();
			
			for (int i=0; i < title.getLength()&&i < descr.getLength(); i++) {
			    Node curTitle = title.item(i);
			    Node curDesk  = descr.item(i);
			    outDoc.add(new Doc(curTitle.getTextContent()+" "+curDesk.getTextContent()));
			    
			    
			    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		 
				// root elements
				Document writeDoc = docBuilder.newDocument();
				Element rootElement = writeDoc.createElement("DOC");
				
				writeDoc.appendChild(rootElement);
		 
				String name = "XMLNews_"+i;
				
				Element num = writeDoc.createElement("DOCNO");
				num.appendChild(writeDoc.createTextNode(name));
				rootElement.appendChild(num);
				
				
				Element news = writeDoc.createElement("DOCTYPE");
				news.appendChild(writeDoc.createTextNode("NEWS"));
				rootElement.appendChild(news);
				
				Element newsW = writeDoc.createElement("DOCTYPE");
				newsW.appendChild(writeDoc.createTextNode("NEWSWIRE"));
				rootElement.appendChild(newsW);
				
				String content = curTitle.getTextContent()+" "+curDesk.getTextContent();
				content = content.replace("\\", "");
				content = content.replaceAll("\\([^\\(]*\\)", "");
				Element text = writeDoc.createElement("TEXT");
				text.appendChild(writeDoc.createTextNode(content));
				rootElement.appendChild(text);
				
				
				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(writeDoc);
				File empty = new File("data/news200/"+name+".xml");
				empty.createNewFile();
				StreamResult result = new StreamResult(empty);
		 
				// Output to console for testing
				// StreamResult result = new StreamResult(System.out);
		 
				transformer.transform(source, result);
		 
				System.out.println("File saved!");
				
				
				
			}
			

			return outDoc;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
