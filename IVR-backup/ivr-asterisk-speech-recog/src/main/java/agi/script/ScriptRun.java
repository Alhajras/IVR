package agi.script;

import java.util.Properties;

import org.asteriskjava.fastagi.DefaultAgiServer;

import StaticsData.Language;
import StaticsData.VoicesTTS;
import nomiApp.LoadXmlProperties;

public class ScriptRun {

	public static void main(String[] args) throws Exception {

		new Thread() {
			public void run() {
				HelloAgiScript agi = new HelloAgiScript();
				try {
					initializingProperties(agi);

					DefaultAgiServer srv = new DefaultAgiServer(agi);
					srv.setPort(4567);

					srv.startup();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();

	}

	private static void initializingProperties(HelloAgiScript agi) throws Exception {
		LoadXmlProperties lxp = new LoadXmlProperties();
		Properties properties = lxp.readProperties("configuration.xml");
		agi.setGoogleKey(properties.getProperty("google.key"));
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
