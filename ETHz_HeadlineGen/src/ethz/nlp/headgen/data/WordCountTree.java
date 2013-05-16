package ethz.nlp.headgen.data;

import java.io.Serializable;

@SuppressWarnings("serial")
public class WordCountTree extends WordCountDummy implements Serializable {
	private WordCount max = new WordCount();
	private long total = 0;

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
