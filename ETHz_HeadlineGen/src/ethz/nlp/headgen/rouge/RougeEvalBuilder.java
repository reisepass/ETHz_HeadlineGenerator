package ethz.nlp.headgen.rouge;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import ethz.nlp.headgen.Doc;

public class RougeEvalBuilder {
	private String peerDir, modelDir;
	private RougeEval rouge = new RougeEval();

	public RougeEvalBuilder(String peerDir, String modelDir) {
		this.peerDir = peerDir;
		this.modelDir = modelDir;
	}

	public void addEvals(Map<Doc, String[]> evalMap) {
		for (Doc d : evalMap.keySet()) {
			addEval(d.f.getName(), evalMap.get(d));
		}
	}

	public void addEval(String peer, String... models) {
		List<Eval> evals = rouge.getEvals();

		Eval e = new Eval();
		e.setID("" + evals.size());
		e.setPeerRoot(peerDir);
		e.setModelRoot(modelDir);
		e.setPeers(genPeers(peer));
		e.setModels(genModels(models));

		evals.add(e);
	}

	public void addEval(List<String> peers, List<String> models) {
		List<Eval> evals = rouge.getEvals();

		Eval e = new Eval();
		e.setID("" + evals.size());
		e.setPeerRoot(peerDir);
		e.setModelRoot(modelDir);
		e.setPeers(genPeers(peers));
		e.setModels(genModels(models));

		evals.add(e);
	}

	public void write(String outFile) throws IOException {
		rouge.write(outFile);
	}

	public void write(File outFile) throws IOException {
		rouge.write(outFile);
	}

	private Models genModels(String... models) {
		int count = 1;
		Models res = new Models();
		for (String m : models) {
			res.getM().add(new M("" + count++, m));
		}
		return res;
	}

	private Models genModels(List<String> models) {
		int count = 1;
		Models res = new Models();
		for (String m : models) {
			res.getM().add(new M("" + count++, m));
		}
		return res;
	}

	private Peers genPeers(String... peers) {
		int count = 1;
		Peers res = new Peers();
		for (String p : peers) {
			res.getP().add(new P("" + count++, p));
		}
		return res;
	}

	private Peers genPeers(List<String> peers) {
		int count = 1;
		Peers res = new Peers();
		for (String p : peers) {
			res.getP().add(new P("" + count++, p));
		}
		return res;
	}
}
