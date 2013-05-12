package ethz.nlp.headgen.sum;

import ethz.nlp.headgen.Doc;

public interface Summerizer {
	public String summary();
	public void setDoc(Doc inD);
}
