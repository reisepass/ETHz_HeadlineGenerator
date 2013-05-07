package ethz.nlp.headgen.lda;

import java.io.File;
import java.io.IOException;

import jgibblda.Estimator;
import jgibblda.Inferencer;
import jgibblda.LDACmdOption;
import ethz.nlp.headgen.util.ConfigFactory;

public class TopicModel {
	private LDAProbs ldaProbs;

	private TopicModel() {
	}

	public static void generateModel(LDAEstimatorConfig config) {
		LDACmdOption cmdOptions = setCmdOptions(config);
		Estimator estimator = new Estimator();
		estimator.init(cmdOptions);

		estimator.estimate();
	}

	public static TopicModel loadModel(LDAEstimatorConfig config) {
		File modelDir = new File(config.getModelDir());
		return null;
	}

	public static TopicModel loadModel(LDAInferenceConfig config) {
		return null;
	}

	public static void inferNewModel(LDAInferenceConfig config) {
		LDACmdOption cmdOptions = setCmdOptions(config);
		Inferencer inf = new Inferencer();
		inf.init(cmdOptions);
		inf.inference();
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
		cmdOption.est = true;
		return cmdOption;
	}

	private static LDACmdOption setCmdOptions(LDAInferenceConfig config) {
		LDACmdOption cmdOption = new LDACmdOption();
		if (config.getNumIters() != null) {
			cmdOption.niters = config.getNumIters();
		}
		if (config.getTWords() != null) {
			cmdOption.twords = config.getTWords();
		}
		cmdOption.dir = config.getModelDir();
		cmdOption.dfile = config.getDataFile();
		cmdOption.modelName = config.getModel();
		cmdOption.inf = true;
		return cmdOption;
	}

	public static void main(String[] args) throws IOException {
		LDAEstimatorConfig config = ConfigFactory.loadConfiguration(
				LDAEstimatorConfig.class, "./conf/lda-baseModel.conf");

		TopicModel.generateModel(config);
	}
}
