package ethz.nlp.headgen.conf;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Properties;

public class ConfigFactory implements InvocationHandler {
	private Properties properties;

	private ConfigFactory(Properties properties) {
		this.properties = properties;
	}

	public static <T> T loadConfiguration(Class<T> configClass, URL url)
			throws IOException {
		return loadConfiguration(configClass,
				new File(URLDecoder.decode(url.getFile(), "UTF-8")));
	}

	public static <T> T loadConfiguration(Class<T> configClass,
			String configFile) throws IOException {
		return loadConfiguration(configClass, new File(configFile));
	}

	@SuppressWarnings("unchecked")
	public static <T> T loadConfiguration(Class<T> configClass, File configFile)
			throws IOException {
		Properties properties = loadProperties(configFile);

		return (T) Proxy.newProxyInstance(configClass.getClassLoader(),
				new Class[] { configClass }, new ConfigFactory(properties));
	}

	private static Properties loadProperties(File configFile)
			throws IOException {
		FileInputStream fis = null;
		Properties properties;
		try {
			fis = new FileInputStream(configFile);
			properties = new Properties();
			properties.load(fis);
		} finally {
			if (fis != null) {
				fis.close();
			}
		}
		return properties;
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		String methodName = method.getName();

		if (methodName.startsWith("get")) {
			Class<?> returnType = method.getReturnType();
			return castReturn(returnType, getProperty(methodName.substring(3)));
		} else if (methodName.startsWith("is")) {
			return new Boolean(getProperty(methodName.substring(2)));
		} else {
			return null;
		}
	}

	private <T> Object castReturn(Class<T> returnType, String result) {
		if (returnType == Integer.class || returnType == int.class) {
			return new Integer(result);
		} else if (result == null) {
			return null;
		} else {
			return result.toString();
		}
	}

	private String getProperty(String key) {
		return properties.getProperty(key.toLowerCase());
	}
}
