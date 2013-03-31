package ethz.nlp.headgen.io;

public interface IOConfig {
	public static final String DEFAULT = "conf/io.conf";

	public String getRawDir();

	public String getParsedDir();

	public String getOutputDir();
	
	public String getModelDir();
}
