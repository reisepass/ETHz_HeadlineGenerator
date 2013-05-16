package ethz.nlp.headgen.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

@SuppressWarnings("serial")
public class SerializableWrapper implements Serializable {
	private Serializable o;

	public SerializableWrapper(Serializable o) {
		this.o = o;
	}

	public void save(String fileName) throws IOException {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(
					new File(fileName)));
			oos.writeObject(o);
		} finally {
			if (oos != null) {
				oos.close();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T readObject(String filename) throws IOException {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(new File(filename)));
			return (T) ois.readObject();
		} catch (ClassNotFoundException e) {
			throw new IOException(e);
		} finally {
			if (ois != null) {
				ois.close();
			}
		}
	}
}
