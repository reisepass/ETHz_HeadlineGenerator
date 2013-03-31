package ethz.nlp.headgen.xml;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import ethz.nlp.headgen.Doc;

public class XMLDoc {
	public static Doc readXML(String f) {
		return readXML(new File(f));
	}

	public static Doc readXML(File f) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(f);

			// optional, but recommended
			// read this -
			// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			NodeList docno = doc.getElementsByTagName("DOCNO");
			NodeList docTy = doc.getElementsByTagName("DOCTYPE");
			NodeList type = doc.getElementsByTagName("TXTTYPE");
			NodeList text = doc.getElementsByTagName("TEXT");

			Doc inputDoc = new Doc(docno.item(0).getTextContent(), docTy
					.item(0).getTextContent(), type.item(0).getTextContent(),
					text.item(0).getTextContent(), f);

			return inputDoc;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
