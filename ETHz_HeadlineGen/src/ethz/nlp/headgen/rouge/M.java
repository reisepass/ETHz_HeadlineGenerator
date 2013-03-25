package ethz.nlp.headgen.rouge;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "M")
public class M {
	@XmlAttribute(name = "ID")
	protected String id;

	@XmlValue
	protected String fileName;

	public M() {
	}

	public M(String id, String fileName) {
		this.id = id;
		this.fileName = fileName;
	}

	public String getID() {
		return id;
	}

	public void setID(String value) {
		this.id = value;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
