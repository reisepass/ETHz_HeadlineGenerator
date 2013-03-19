package ethz.nlp.headgen.io;

import java.io.Serializable;

import edu.stanford.nlp.pipeline.Annotation;

public class ParsedDoc implements Serializable {
	private static final long serialVersionUID = 1L;

	private Annotation annotation;

	public ParsedDoc(Annotation annotation) {
		this.annotation = annotation;
	}

	public Annotation getAnnotation() {
		return annotation;
	}
}