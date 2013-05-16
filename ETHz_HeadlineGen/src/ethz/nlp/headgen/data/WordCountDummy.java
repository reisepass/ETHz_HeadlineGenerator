package ethz.nlp.headgen.data;

import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;

public class WordCountDummy extends ConcurrentRadixTree<WordCount> {

	public WordCountDummy() {
		super(new DefaultCharArrayNodeFactory());
	}

}
