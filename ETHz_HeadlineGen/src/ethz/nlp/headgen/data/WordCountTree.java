package ethz.nlp.headgen.data;

import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.node.NodeFactory;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;

public class WordCountTree extends ConcurrentRadixTree<WordCount> {
	private WordCount max = new WordCount();
	private long total = 0;

	public WordCountTree() {
		this(new DefaultCharArrayNodeFactory());
	}

	public WordCountTree(NodeFactory nodeFactory) {
		super(nodeFactory);
	}

	public WordCount put(CharSequence key) {
		return putIfAbsent(key, new WordCount());
	}

	@Override
	public WordCount putIfAbsent(CharSequence key, WordCount value) {
		WordCount val = super.putIfAbsent(key, value);
		if (val != null) {
			val.increment();
			if (val.getCount() > max.getCount()) {
				max = val;
			}
		}
		total++;
		return val;
	}

	public int getMax() {
		return max.getCount();
	}

	public long getTotal() {
		return total;
	}
}
