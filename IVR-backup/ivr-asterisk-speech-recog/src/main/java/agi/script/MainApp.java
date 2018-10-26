package agi.script;

import java.util.Properties;

import org.asteriskjava.fastagi.DefaultAgiServer;

import StaticsData.Language;
import StaticsData.VoicesTTS;
import nomiApp.LoadXmlProperties;

public class MainApp {

	public static void main(String[] args) throws Exception {

		/**
		 * This thread is establishing an AGI service on port 4567, to connect and
		 * listen to any AGI request from the Asterisk
		 */
		new Thread() {
			public void run() {
				HelloAgiScript agi = new HelloAgiScript();
				try {
					initializingProperties(agi);
					DefaultAgiServer srv = new DefaultAgiServer(agi);
					srv.setPort(4567);
					srv.startup();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();

		/**
		 * This code is to create a new port listener for a separate customer, and for
		 * each new customer or caller we need to add a new listener
		 */
//		 new Thread() {
//		 public void run() {
//		 HelloAgiScriptText agiText = new HelloAgiScriptText();
//		 try {
//		 DefaultAgiServer srv = new DefaultAgiServer(agiText);
//		 srv.setPort(4577);
//		 srv.startup();
//		 } catch (Exception e) {
//		 e.printStackTrace();
//		 }
//		 }
//		 }.start();

	}

	/**
	 * Reading the configuration.xml file in order to initialize the properties of
	 * the IVR
	 */
	private static void initializingProperties(HelloAgiScript agi) throws Exception {
		LoadXmlProperties lxp = new LoadXmlProperties();
		Properties properties = lxp.readProperties("configuration.xml");
		agi.setGoogleKey(properties.getProperty("google.key"));
		agi.setTtsTechnology(properties.getProperty("ttsTechnology"));
		agi.setLanguage(Language.valueOf(properties.getProperty("language")));
		agi.setVoice(VoicesTTS.valueOf(properties.getProperty("tts.voice")));
		agi.setGainValue(Float.parseFloat(properties.getProperty("tts.gain")));
		agi.setStatusFilePath(properties.getProperty("file.status"));
		agi.setGoogleAPIFilePath(properties.getProperty("google.file"));
		agi.setKnowlowadgeBaseUrl(properties.getProperty("knowlowadgeBaseUrl"));
		agi.setHistoryFilePath(properties.getProperty("historyFilePath"));
		agi.setHistoryRecord(Boolean.parseBoolean(properties.getProperty("historyRecord")));
		agi.setCustomerServiceNumber(properties.getProperty("customerServiceNumber"));
		agi.setLanguageMessages(lxp.readProperties("src/main/java/LanguagesScripts/messages_"
				+ agi.getLanguage().getLang().replaceAll("-", "_") + ".xml"));

	}

}
