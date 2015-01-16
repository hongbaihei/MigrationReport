package com.cib.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 
 */
public class Propertiesconfiguration {
	private final static String APP_PROPERTIES_FILE = "resources/system.properties";

	protected final static Log logger = LogFactory
			.getLog(Propertiesconfiguration.class);

	protected static Properties p = null;

	static {
		init();
	}

	/**
	 * init
	 */
	protected static void init() {
		InputStream in = null;
		try {
			in = Propertiesconfiguration.class.getClassLoader()
					.getResourceAsStream(APP_PROPERTIES_FILE);
			if (in != null) {
				if (p == null)
					p = new Properties();
				p.load(in);
			}
		} catch (IOException e) {
			logger.error("load " + APP_PROPERTIES_FILE
					+ " into Constants error!");
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.error("close " + APP_PROPERTIES_FILE + " error!");
				}
			}
		}
	}

	/**
	 * 
	 * @param key
	 *            property key.
	 * @param defaultValue
	 */
	protected static String getProperty(String key, String defaultValue) {
		return p.getProperty(key, defaultValue);
	}

	/**
	 * 
	 * @param key
	 *            property key.
	 */
	public static String getStringProperty(String key) {
		return p.getProperty(key);
	}
}
