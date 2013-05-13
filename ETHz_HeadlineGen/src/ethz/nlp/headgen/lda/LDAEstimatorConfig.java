package ethz.nlp.headgen.lda;


public interface LDAEstimatorConfig extends LDAConfig {
	public static final String DEFAULT = "conf/lda-baseModel.conf";

	/**
	 * @return The value of alpha, hyper-parameter of LDA. The default value of
	 *         alpha is 50 / K (K is the the number of topics). See
	 *         [Griffiths04] for a detailed discussion of choosing alpha and
	 *         beta values.
	 */
	public Double getAlpha();

	/**
	 * @return The value of beta, also the hyper-parameter of LDA. Its default
	 *         value is 0.1
	 */
	public Double getBeta();

	/**
	 * @return The number of topics. Its default value is 100. This depends on
	 *         the input dataset. See [Griffiths04] and [Blei03] for a more
	 *         careful discussion of selecting the number of topics.
	 */
	public Integer getNumTopics();

	/**
	 * @return The step (counted by the number of Gibbs sampling iterations) at
	 *         which the LDA model is saved to hard disk. The default value is
	 *         200.
	 */
	public Integer getSavestep();
}
