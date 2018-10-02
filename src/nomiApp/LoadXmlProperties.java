package nomiApp;

import java.io.FileInputStream;
import java.util.Properties;

public class LoadXmlProperties {

	public LoadXmlProperties() {

	}

	Properties readProperties(String filePath) throws Exception {
		Properties properties = new Properties();
		FileInputStream fis = new FileInputStream(filePath);
		properties.loadFromXML(fis);
		return properties;
	}

}
