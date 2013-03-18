package ethz.nlp.headgen;

import ethz.nlp.headgen.sum.Summerizer;

public class Doc implements Summerizer {

	public Doc(String docno, String docType, String textType, String cont) {
		super();
		this.docno = docno;
		this.docType = docType;
		this.textType = textType;
		this.cont = cont;
	}

	public String docno;
	public String docType;
	public String textType;
	public String cont;

	@Override
	public String toString() {
		return "Doc [docno=" + docno + ", docType=" + docType + ", textType="
				+ textType + "]";
	}

	@Override
	public String summary() {
		return null;
	}

}
