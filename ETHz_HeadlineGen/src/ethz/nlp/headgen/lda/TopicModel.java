package ethz.nlp.headgen.lda;

import java.io.IOException;

import jgibblda.Estimator;
import jgibblda.Inferencer;
import jgibblda.LDACmdOption;
import ethz.nlp.headgen.util.ConfigFactory;

public class TopicModel {

	private TopicModel() {
	}

	public static void generateModel(LDAEstimatorConfig config) {
		Estimator estimator = new Estimator();
		estimator.init(setCmdOptions(config));

		estimator.estimate();
	}

	public static void inferNewModel(LDAInferenceConfig infConf, LDAEstimatorConfig estConf) {
		Inferencer inf = new Inferencer();
		inf.init(setCmdOptions(estConf, infConf));
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

	private static LDACmdOption setCmdOptions(LDAEstimatorConfig estConf, LDAInferenceConfig infConf) {
		LDACmdOption cmdOption = new LDACmdOption();
		if (infConf.getNumIters() != null) {
			cmdOption.niters = infConf.getNumIters();
		}
		if (infConf.getTWords() != null) {
			cmdOption.twords = infConf.getTWords();
		}
		cmdOption.dir = estConf.getModelDir();
		cmdOption.dfile = infConf.getDataFile();
		cmdOption.modelName = infConf.getModel();
		cmdOption.est = true;
		return cmdOption;
	}

	public static void main(String[] args) throws IOException {
		LDAEstimatorConfig estConf = ConfigFactory.loadConfiguration(
				LDAEstimatorConfig.class, LDAEstimatorConfig.DEFAULT);
		LDAInferenceConfig infConf = ConfigFactory.loadConfiguration(
				LDAInferenceConfig.class, LDAInferenceConfig.DEFAULT);

		TopicModel.inferNewModel(infConf, estConf);
	}
}
