package ethz.nlp.headgen.rouge;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "P")
public class P {

	@XmlAttribute(name = "ID")
	protected String id;

	@XmlValue
	protected String fileName;

	public P() {
	}

	public P(String id, String fileName) {
		this.id = id;
		this.fileName = fileName;
	}

	public String getId() {
		return id;
	}

	public void setId(String value) {
		this.id = value;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
