package ethz.nlp.headgen.lda;

import java.io.IOException;

import ethz.nlp.headgen.util.ConfigFactory;
import jgibblda.Estimator;
import jgibblda.LDACmdOption;
import jgibblda.Model;

public class TopicModel {

	private Model model;

	private TopicModel(Model model) {
		this.model = model;
	}

	public static void generateModel(LDAEstimatorConfig config) {
		LDACmdOption cmdOptions = setCmdOptions(config);
		cmdOptions.est = true;
		Estimator estimator = new Estimator();
		estimator.init(cmdOptions);
		
		estimator.estimate();
	}

	public static TopicModel loadModel() {
		return null;
	}

	public Model getModel() {
		return model;
	}

	public Model inferNewModel(LDAEstimatorConfig config) {
		return null;
	}

	private static LDACmdOption setCmdOptions(LDAEstimatorConfig config) {
		LDACmdOption cmdOption = new LDACmdOption();
		if (config.getAlpha() != null) {
			cmdOption.alpha = config.getAlpha();
		}
		if (config.getBeta() != null) {
			cmdOption.beta = config.getBeta();
		}
		if (config.getNumIters() != null) {
			cmdOption.niters = config.getNumIters();
		}
		if (config.getNumTopics() != null) {
			cmdOption.K = config.getNumTopics();
		}
		if (config.getTWords() != null) {
			cmdOption.twords = config.getTWords();
		}
		if (config.getSavestep() != null) {
			cmdOption.savestep = config.getSavestep();
		}
		cmdOption.dir = config.getModelDir();
		cmdOption.dfile = config.getDataFile();
		return cmdOption;
	}

	public static void main(String[] args) throws IOException {
		LDAEstimatorConfig config = ConfigFactory.loadConfiguration(
				LDAEstimatorConfig.class, "./conf/lda.conf");

		TopicModel.generateModel(config);
	}
}
