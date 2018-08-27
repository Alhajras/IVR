package Alexandra_The_Bot;

import java.io.FileInputStream;
import java.util.Properties;

public class LoadXmlProperties {


	public LoadXmlProperties() {
		
	}

	Properties readProperties() throws Exception {
		Properties properties = new Properties();
		FileInputStream fis = new FileInputStream("configuration.xml");
		properties.loadFromXML(fis);

		return properties;
	}

}
