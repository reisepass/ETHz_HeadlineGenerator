package ethz.nlp.headgen.data;

import java.io.Serializable;

public class WordCount implements Serializable {
	private int count;

	public WordCount() {
		this(1);
	}

	public WordCount(int count) {
		this.count = count;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void increment() {
		count++;
	}
}
