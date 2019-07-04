package fr.epita.quiz.services;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class ConfigurationService {

	private static ConfigurationService instance;
	
	private static final String defaultConfigFileLocation = "conf.properties";

	Properties props = new Properties();
	boolean init = false;
	
	private ConfigurationService() {
		// properties loading
		try {
			File confFile = new File("conf.properties");
			FileInputStream ips = new FileInputStream(confFile);
			props.load(ips);
			init =true;
		} catch (Exception e) {
			init=false;
		}

	}

	public static ConfigurationService getInstance() {
		if (instance == null) {
			instance = new ConfigurationService();
		}
		return instance;
	}
	
	public boolean isInit() {
		return init;
	}

	public String getConfigurationValue(String key, String defaultValue) {
		return props.getProperty(key, defaultValue);
	}
	public String getConfigurationValue(ConfigEntry key, String defaultValue) {
		return props.getProperty(key.getKey(), defaultValue);
	}

}
