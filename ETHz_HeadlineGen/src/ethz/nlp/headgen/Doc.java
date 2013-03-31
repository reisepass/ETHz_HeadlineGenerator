package ethz.nlp.headgen;

import java.io.File;
import java.util.List;

import edu.stanford.nlp.pipeline.Annotation;
import ethz.nlp.headgen.sum.Summerizer;

public class Doc implements Summerizer {

	public Doc(String docno, String docType, String textType, String cont,
			File f) {
		super();
		this.docno = docno;
		this.docType = docType;
		this.textType = textType;
		this.cont = cont;
		this.f = f;
	}

	public String docno;
	public String docType;
	public String textType;
	public String cont;
	public File f;
	public Annotation annotation;
	public String summary;
	public List<String> models;

	@Override
	public String toString() {
		return "Doc [docno=" + docno + ", docType=" + docType + ", textType="
				+ textType + "]";
	}

	@Override
	public String summary() {
		return null;
	}

	public String getParentDirName() {
		return f.getParentFile().getName();
	}

	public String getAnotFileName() {
		return f.getName() + ".parsed";
	}
}
