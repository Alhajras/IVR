package nomiApp;

import java.io.BufferedWriter;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Pattern;

import StaticsData.Language;
import StaticsData.VoicesTTS;

/**
 * This is the main application of the IVR .
 * 
 * @author GOXR3PLUS
 *
 */
public class MainApplication {

	private static Properties languageMessages;
	private static String statusFilePath;
	private static String googleAPIFilePath;
	private static String googleKey;
	private static String knowlowadgeBaseUrl;
	private static Language language = Language.ENGLISH;
	private static VoicesTTS voice = VoicesTTS.ENGLISH_MALE_1;
	private static float gainValue;
	private final MarryTTS tts;
	private static boolean historyRecord;
	private static String historyFilePath;

	/**
	 * Constructor
	 * 
	 * @throws Exception
	 */
	public MainApplication() throws Exception {

		ComposerNovomind cn = new ComposerNovomind();
		GoogleApi api = new GoogleApi();
		initializingProperties();
		tts = new MarryTTS(voice, gainValue);
		// get milliseconds.

		String googleSTTService = "";
		String nomiResponse;

		while (true) {
			int status = Integer.parseInt(readingFile(statusFilePath));
			switch (status) {
			case 4:
				long startTime = System.currentTimeMillis();
				try {

					googleSTTService = api.POSTRequest(googleAPIFilePath, googleKey, language.getLang());

					System.err.println("google: " + googleSTTService.replaceAll("[-+.^:,]", ""));
					if (!googleSTTService.isEmpty()) {
						nomiResponse = cn.ask(googleSTTService.replaceAll("[-+.^:,]", ""), knowlowadgeBaseUrl);
						System.err.println("nomi: " + nomiResponse);
						if (!nomiResponse.isEmpty()) {
							tts.speak(nomiResponse);
						}

					} else
						tts.speak(languageMessages.getProperty("I_did_not_find_your_request"));

				} catch (Exception e) {
					// TODO Auto-generated catch block
					tts.speak(languageMessages.getProperty("I_did_not_find_your_request"));
					e.printStackTrace();
				}
				if (Pattern.matches(".*(customer(|s) service(|s)).*", googleSTTService))
					writingIntoFile("5", statusFilePath, false);
				else
					writingIntoFile("2", statusFilePath, false);

				long stopTime = System.currentTimeMillis();
				System.err.println("the time in milliseconds is " + (stopTime - startTime));
				break;

			case 6:
				nomiResponse = cn.ask("main menu", knowlowadgeBaseUrl);
				// System.out.println("Status 6: " + nomiResponse);
				tts.speak(nomiResponse);
				writingIntoFile("2", statusFilePath, false);
				break;

			default:
				break;
			}
		}

	}

	public static String readingFile(String path) {
		StringBuilder sb = new StringBuilder();
		try {
			Scanner in = new Scanner(new FileReader(path));
			while (in.hasNext()) {
				sb.append(in.next());
			}
			in.close();
		} catch (Exception e) {
			System.err.println("An error has been occured while reading the file: " + path);
		}
		if (sb.toString().isEmpty())
			return "1";
		return sb.toString().replaceAll(" ", "");
	}

	public static void writingIntoFile(String content, String path, boolean append) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
			bw.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void initializingProperties() throws Exception {
		LoadXmlProperties lxp = new LoadXmlProperties();
		Properties properties = lxp.readProperties("configuration.xml");

		googleKey = properties.getProperty("google.key");
		language = Language.valueOf(properties.getProperty("language"));
		voice = VoicesTTS.valueOf(properties.getProperty("tts.voice"));
		gainValue = Float.parseFloat(properties.getProperty("tts.gain"));
		statusFilePath = properties.getProperty("file.status");
		googleAPIFilePath = properties.getProperty("google.file");
		knowlowadgeBaseUrl = properties.getProperty("knowlowadgeBaseUrl");
		historyFilePath = properties.getProperty("historyFilePath");
		historyRecord = Boolean.parseBoolean(properties.getProperty("historyRecord"));

		languageMessages = lxp
				.readProperties("src/LanguagesScripts/messages_" + language.getLang().replaceAll("-", "_") + ".xml");

	}

	public static void main(String[] args) throws Exception {
		new MainApplication();
	}

}
