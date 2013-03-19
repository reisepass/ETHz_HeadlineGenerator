package ethz.nlp.headgen;

public interface Config {
	public static final String DEFAULT = "conf/default.conf";

	public String getAnnotators();

	public String getDocType();
}
