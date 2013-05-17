package ethz.nlp.headgen.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

	public static void main(String[] args) {
		XMLNews200.writeXML(new File("data/newsspace200.xml"));
		int a = 1 + 1;
		// System.out.println(output.toString());
	}

	public static void writeXML(File f) {
		FileWriter fw = null;
		try {
			fw = new FileWriter(new File("data/news200_raw"));
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document docIN = dBuilder.parse(f);

			docIN.getDocumentElement().normalize();

			NodeList title = docIN.getElementsByTagName("title");

			NodeList descr = docIN.getElementsByTagName("description");

			for (int i = 0; i < title.getLength() && i < descr.getLength(); i++) {
				Node curTitle = title.item(i);
				Node curDesk = descr.item(i);

				String content = curTitle.getTextContent() + " "
						+ curDesk.getTextContent();
				content = content.replace("\\", " ");
				content = content.replaceAll("\\([^\\(]*\\)", "");
				content = content.replaceAll("\n", " ");
				
				fw.write(content);
				if ((i < title.getLength()-1 && i < descr.getLength()-1)) {
					fw.write("\n");
				}

				if (i % 100 == 0) {
					System.out.println(i);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static ArrayList<Doc> readXML(File f) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document docIN = dBuilder.parse(f);

			// optional, but recommended
			// read this -
			// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			docIN.getDocumentElement().normalize();

			NodeList title = docIN.getElementsByTagName("title");

			NodeList descr = docIN.getElementsByTagName("description");

			ArrayList<Doc> outDoc = new ArrayList<Doc>();
			Random ran = new Random();

			for (int i = 0; i < title.getLength() && i < descr.getLength(); i++) {
				Node curTitle = title.item(i);
				Node curDesk = descr.item(i);
				outDoc.add(new Doc(curTitle.getTextContent() + " "
						+ curDesk.getTextContent()));

			}

			return outDoc;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static ArrayList<Doc> readXML(File f, int limit) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document docIN = dBuilder.parse(f);

			// optional, but recommended
			// read this -
			// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			docIN.getDocumentElement().normalize();

			NodeList title = docIN.getElementsByTagName("title");

			NodeList descr = docIN.getElementsByTagName("description");

			ArrayList<Doc> outDoc = new ArrayList<Doc>();
			Random ran = new Random();

			for (int i = 0; i < title.getLength() && i < descr.getLength()
					&& i < limit; i++) {
				Node curTitle = title.item(i);
				Node curDesk = descr.item(i);
				outDoc.add(new Doc(curTitle.getTextContent() + " "
						+ curDesk.getTextContent()));

			}

			return outDoc;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
