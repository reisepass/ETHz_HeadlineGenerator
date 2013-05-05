package ethz.nlp.headgen.lda;

/**
 * Configuration settings for using the JGibbLDA library.
 * 
 * @see <a
 *      href="http://jgibblda.sourceforge.net/#_2.2._Command_Line_&_Input_Parameter">http://jgibblda.sourceforge.net/</a>
 * @author jared
 * 
 */
public interface LDAConfig {
	/**
	 * @return The number of Gibbs sampling iterations. The default value is
	 *         2000.
	 */
	public Integer getNumIters();

	/**
	 * @return The number of most likely words for each topic. The default value
	 *         is zero. If you set this parameter a value larger than zero,
	 *         e.g., 20, JGibbLDA will print out the list of top 20 most likely
	 *         words per each topic each time it save the model to hard disk
	 *         according to the parameter savestep above.
	 */
	public Integer getTWords();

	/**
	 * @return The model directory
	 */
	public String getModelDir();

	/**
	 * @return The input training data file.
	 */
	public String getDataFile();
}
