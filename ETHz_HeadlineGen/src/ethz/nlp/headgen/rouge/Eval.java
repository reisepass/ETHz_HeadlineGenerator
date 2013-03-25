package ethz.nlp.headgen.rouge;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EVAL", propOrder = { "peerRoot", "modelRoot", "inputFormat",
		"peers", "models" })
public class Eval {
	@XmlElement(name = "PEER-ROOT", required = true)
	protected String peerRoot;
	@XmlElement(name = "MODEL-ROOT", required = true)
	protected String modelRoot;
	@XmlElement(name = "INPUT-FORMAT", required = true)
	protected InputFormat inputFormat = new InputFormat();
	@XmlElement(name = "PEERS", required = true)
	protected Peers peers;
	@XmlElement(name = "MODELS", required = true)
	protected Models models;
	@XmlAttribute(name = "ID")
	protected String id;

	public String getPeerRoot() {
		return peerRoot;
	}

	public void setPeerRoot(String value) {
		this.peerRoot = value;
	}

	public String getModelRoot() {
		return modelRoot;
	}

	public void setModelRoot(String value) {
		this.modelRoot = value;
	}

	public InputFormat getInputFormat() {
		return inputFormat;
	}

	public void setInputFormat(InputFormat value) {
		this.inputFormat = value;
	}

	public Peers getPeers() {
		return peers;
	}

	public void setPeers(Peers value) {
		this.peers = value;
	}

	public Models getModels() {
		return models;
	}

	public void setModels(Models value) {
		this.models = value;
	}

	public String getID() {
		return id;
	}

	public void setID(String value) {
		this.id = value;
	}
}
