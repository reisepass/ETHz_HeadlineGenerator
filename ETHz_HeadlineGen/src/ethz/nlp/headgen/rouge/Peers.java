package ethz.nlp.headgen.rouge;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PEERS", propOrder = { "p" })
public class Peers {

	@XmlElement(name = "P", required = true)
	protected List<P> p;

	public List<P> getP() {
		if (p == null) {
			p = new ArrayList<P>();
		}
		return this.p;
	}

}
