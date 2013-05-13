package ethz.nlp.headgen.lda;

public interface LDAInferenceConfig extends LDAConfig {
	public static final String DEFAULT = "conf/lda-inferenceModel.conf";

	public String getModel();
}
