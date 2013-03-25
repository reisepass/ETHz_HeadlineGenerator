package ethz.nlp.headgen.rouge;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "INPUT-FORMAT")
public class InputFormat {

	@XmlAttribute(name = "TYPE")
	protected String type = "SPL";

	public String getType() {
		return type;
	}

	public void setType(String value) {
		this.type = value;
	}
}
