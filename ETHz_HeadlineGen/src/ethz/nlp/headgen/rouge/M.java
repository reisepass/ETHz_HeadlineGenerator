package ethz.nlp.headgen.rouge;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "M")
public class M {
	@XmlAttribute(name = "ID")
	protected String id;

	public String getID() {
		return id;
	}

	public void setID(String value) {
		this.id = value;
	}
}
