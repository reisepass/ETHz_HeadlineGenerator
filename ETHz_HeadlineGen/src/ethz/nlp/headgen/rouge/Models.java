package ethz.nlp.headgen.rouge;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MODELS", propOrder = { "m" })
public class Models {

	@XmlElement(name = "M", required = true)
	protected List<M> m;

	public List<M> getM() {
		if (m == null) {
			m = new ArrayList<M>();
		}
		return this.m;
	}

}
