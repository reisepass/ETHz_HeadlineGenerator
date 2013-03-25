package ethz.nlp.headgen.rouge;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ROUGE-EVAL", propOrder = { "evals" })
@XmlRootElement(name = "ROUGE-EVAL")
public class RougeEval {

	@XmlElement(name = "EVAL", required = true)
	protected List<Eval> evals;

	@XmlAttribute(name = "version")
	protected String version = "1.0";

	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersion() {
		return version;
	}

	public List<Eval> getEvals() {
		if (evals == null) {
			evals = new ArrayList<Eval>();
		}
		return this.evals;
	}

	public void write(String outFile) throws IOException {
		write(new File(outFile));
	}

	public void write(File outFile) throws IOException {
		try {
			JAXBContext context = JAXBContext.newInstance(this.getClass());

			Marshaller marshaller = context.createMarshaller();

			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

			marshaller.marshal(this, outFile);
		} catch (JAXBException e) {
			throw new IOException(e);
		}
	}
}
