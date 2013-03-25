package ethz.nlp.headgen.rouge;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "P")
public class P {

	@XmlAttribute(name = "ID")
	protected String id;

	public String getId() {
		return id;
	}

	public void setId(String value) {
		this.id = value;
	}

}
