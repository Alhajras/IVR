package agi.script;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Pattern;

import org.asteriskjava.fastagi.AgiChannel;
import org.asteriskjava.fastagi.AgiException;
import org.asteriskjava.fastagi.AgiRequest;
import org.asteriskjava.fastagi.BaseAgiScript;

import StaticsData.Language;
import StaticsData.StatesMachine;
import StaticsData.VoicesTTS;
import lombok.Getter;
import lombok.Setter;
import nomiApp.ComposerNovomind;
import nomiApp.GoogleApi;
import nomiApp.MarryTTS;

/**
 * The Getter and Setter annotation are provided by the Lombok project, it
 * generates an automatic getters and setters for all variables
 */
@Setter
@Getter
public class HelloAgiScript extends BaseAgiScript {
	/** The variables of the HelloAgiScript class */
	private Properties languageMessages;
	private String statusFilePath;
	private String googleAPIFilePath;
	private String googleKey;
	private String knowlowadgeBaseUrl;
	private Language language = Language.ENGLISH;
	private VoicesTTS voice = VoicesTTS.ENGLISH_MALE_1;
	private float gainValue;
	private MarryTTS tts;
	private boolean historyRecord;
	private String historyFilePath;
	private GoogleApi api;
	private ComposerNovomind cn;
	private String PIN;
	private String customerServiceNumber;
	StatesMachine statesMachine = StatesMachine.START;
	private String uniqueID = UUID.randomUUID().toString();
	private short numberOfSilence = 0;

	public void service(AgiRequest request, AgiChannel channel) throws AgiException {
		/** Answer the channel... */

		answer();
		cn = new ComposerNovomind();
		api = new GoogleApi();
		tts = new MarryTTS(voice, gainValue);
		// testingGoogleAPIPerformance();
		/** Checking the current state-machine of the program */
		while (true) {
			try {
				/**
				 * This is a hack to copy the voice file in the Asterisk voice path in order to
				 * read it, you can delete this line but the 'astdatadir' property in the
				 * asterisk.conf file should be changed from the default value astdatadir =>
				 * /var/lib/asterisk, to the desired path. Look at :
				 * https://wiki.asterisk.org/wiki/display/AST/Directory+and+File+Structure
				 */
				exec("System", "cp -a /media/sf_SharedFolderWinLinux/marryTTSOutput/8k/. /var/lib/asterisk/sounds/en");

				switch (statesMachine.getIndex()) {
				case 1:
					record();
					break;

				case 2:
					playback();
					break;

				case 3:
					convertFile();
					break;

				case 4:
					processingRequest();
					break;

				case 5:
					callCustomerService();
					break;

				case 6:
					start();
					break;

				case 7:
					checkingAuthentication();
					break;

				case 8:
					ChangingLanguage();
					break;

				default:
					// If any error has been occurred we just close the connection
					hangup();
					break;
				}
			} catch (Exception e) {
				tts.speak(languageMessages.getProperty("I_did_not_find_your_request"), uniqueID);
				hangup();
				e.printStackTrace();
			}
		}
	}

	/**
	 * This function is for changing the language from the standard English to the
	 * German when the user choose to change
	 */
	private void ChangingLanguage() {
		tts.getTts().setVoice(VoicesTTS.GERMAN_MALE_1.getVoice());
		this.language = Language.GERMAN;
		/**
		 * Since only the English knowlowdgeBase does exist, this line is commented out,
		 * in the future a German version must be implemented and changing the next line
		 * to something like https://showroom11.novomind.com/nmIQ/api/rest/ask/
		 */
		// this.knowlowadgeBaseUrl = "";#

		tts.speak("Ich habe die sprache verändert, was kann ich für Sie tun?", uniqueID);
		statesMachine = StatesMachine.PLAYBACK;
	}

	/**
	 * Start recording the user-voice and saving it by a unique name which uses the
	 * ID of the caller. The file is stored in WAV format however it ccan be changed
	 * to the following formats “GSM”, “ALAW”, “G722”, “SIREN7”, “SIREN14”, “SLN16”.
	 * We wait for 2 seconds of silence from the user to detect the end of the
	 * request
	 */
	private void record() {
		try {
			exec("Record", "asterisk-recording" + uniqueID + ":wav,2");
			statesMachine = StatesMachine.CONVERTFILE;
		} catch (AgiException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Playing the response of Nomi which has a unique file name "marryTTS" + //
	 * uniqueID
	 */
	public void playback() throws Exception {
		streamFile("marryTTS" + uniqueID);
		statesMachine = StatesMachine.RECORD;
	}

	/**
	 * Converting the audio file from the .wav format to .flac, since the googleAPI
	 * requirement for better performance are "encoding":"FLAC", "sampleRateHertz":
	 * 16000. look at:
	 * https://cloud.google.com/speech-to-text/docs/quickstart-protocol
	 */
	private void convertFile() {
		long startTime = System.currentTimeMillis();
		try {
			exec("System", "sox /var/lib/asterisk/sounds/asterisk-recording" + uniqueID
					+ ".wav --rate 16k --bits 16 /media/sf_SharedFolderWinLinux/asteriskOutput/asterisk-recording"
					+ uniqueID + ".flac");
			statesMachine = StatesMachine.PROCESSING_REQUEST;
			long stopTime = System.currentTimeMillis();
			writingIntoFileStatistics("SOXConversion took:" + (stopTime - startTime),
					"D:\\IVR\\SharedFolderWinLinux\\statistics.txt");
		} catch (AgiException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Here were the heavy lifting is happening, this function is sending the
	 * encodedFile as a JSON object to the googleAPI, after that it sends the result
	 * from GoogleAPI to Nomi to answer it.
	 */
	public void processingRequest() throws Exception {
		long startTime = System.currentTimeMillis();

		String googleSTTService = "";
		String nomiResponse;
		try {

			googleSTTService = api.POSTRequest(googleAPIFilePath + "asterisk-recording" + uniqueID + ".flac", googleKey,
					language.getLang());
			long stopTime = System.currentTimeMillis();
			writingIntoFileStatistics("googleSTTService took:" + (stopTime - startTime),
					"D:\\IVR\\SharedFolderWinLinux\\statistics.txt");
			writingIntoFileStatistics(
					"Number of words: " + googleSTTService.split(" ").length + " ;took:" + (stopTime - startTime),
					"D:\\IVR\\SharedFolderWinLinux\\googleAPIWords.txt");
			System.err.println("google: " + googleSTTService.replaceAll("[-+.^:,]", ""));
			if (!googleSTTService.isEmpty()) {
				startTime = System.currentTimeMillis();
				nomiResponse = cn.ask(googleSTTService.replaceAll("[-+.^:,]", ""), knowlowadgeBaseUrl);
				stopTime = System.currentTimeMillis();
				writingIntoFileStatistics("Nomi took:" + (stopTime - startTime),
						"D:\\IVR\\SharedFolderWinLinux\\statistics.txt");
				System.err.println("nomi: " + nomiResponse);
				if (!nomiResponse.isEmpty()) {
					tts.speak(nomiResponse, uniqueID);
				}

			} else {
				tts.speak(languageMessages.getProperty("I_did_not_find_your_request"), uniqueID);
				if (numberOfSilence++ == 2)
					hangup();
			}

		} catch (Exception e) {
			tts.speak(languageMessages.getProperty("goodbye"), uniqueID);
			hangup();
			e.printStackTrace();
		}

		/**
		 * Here the service can be extended for more subRoutine
		 */
		if (Pattern.matches(".*(customer(|s) service(|s)).*", googleSTTService))
			/** Calling the customer service if the user wants */
			statesMachine = StatesMachine.CALL_CUSTOMER_SERVICE;
		else if (Pattern.matches(".*(authentication(|s) need(|ed)).*", googleSTTService)) {
			/** Creating a Two-factor authentication when the service is critical */
			statesMachine = StatesMachine.CHECKING_AUTHENTICATION;
		} else if (Pattern.matches(".*(German|deutsch).*", googleSTTService)) {
			/** Creating a Two-factor authentication when the service is critical */
			statesMachine = StatesMachine.CHANGING_LANGUAGE;
		} else
			statesMachine = StatesMachine.PLAYBACK;

	}

	/**
	 * Calling any given number, where it can be adjusted by the configuration.xml
	 * file
	 */
	private void callCustomerService() {
		try {
			exec("Dial", "SIP/" + customerServiceNumber);
		} catch (AgiException e) {
			e.printStackTrace();
		}
	}

	/** The first state-machine for the program, where it sends a request to Nomi */
	public void start() throws Exception {
		String nomiResponse;
		nomiResponse = cn.ask("main menu", knowlowadgeBaseUrl);
		// System.out.println("Status 6: " + nomiResponse);
		tts.speak(nomiResponse, uniqueID);
		statesMachine = StatesMachine.PLAYBACK;
	}

	/**
	 * Establishing the Two-factor authentication, where an SMS is send to the
	 * caller Phone with a valid PIN number for security reasons
	 */
	public void checkingAuthentication() throws Exception {

		int numberOfTries = 0;
		PIN = getVariable("PIN");
		try {
			tts.speak(languageMessages.getProperty("authentication_needed"), uniqueID);
			while (true) {
				exec("System", "cp -a /media/sf_SharedFolderWinLinux/marryTTSOutput/8k/. /var/lib/asterisk/sounds/en");
				streamFile("marryTTS" + uniqueID);
				exec("Record", "asterisk-recording" + uniqueID + ":wav,2");
				exec("System", "sox /var/lib/asterisk/sounds/asterisk-recording" + uniqueID
						+ ".wav --rate 16k --bits 16 /media/sf_SharedFolderWinLinux/asteriskOutput/asterisk-recording"
						+ uniqueID + ".flac");
				String googleSTTService = api.POSTRequest(googleAPIFilePath + "asterisk-recording" + uniqueID + ".flac",
						googleKey, language.getLang());
				System.err.println("the pin is : " + PIN + " google pin : " + googleSTTService);
				if (Integer.parseInt(googleSTTService.replaceAll(" ", "")) == (Integer.parseInt(PIN))) {
					tts.speak(languageMessages.getProperty("Your_entry_is_correct"), uniqueID);
					cn.ask("VALID", knowlowadgeBaseUrl);
					break;
				} else {
					numberOfTries++;
					tts.speak(languageMessages.getProperty("invalid_pin"), uniqueID);
					if (numberOfTries == 3) {
						hangup();
						break;
					}
				}
			}

		} catch (AgiException e) {
			e.printStackTrace();
		}
		statesMachine = StatesMachine.PLAYBACK;
	}

	/** Function to generate a unique PIN number */
	public String generatePIN() {
		int x = (int) (Math.random() * 9);
		x = x + 1;
		String randomPIN = (x + "") + (((int) (Math.random() * 1000)) + "");
		if (randomPIN.length() < 4)
			randomPIN += "6";
		return randomPIN;
	}

	/**
	 * This function is to test the performance of the sub-code and stream it into a
	 * file
	 */
	public static void writingIntoFileStatistics(String content, String path) {
		try {
			Files.write(Paths.get(path), (content + "/n;").getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
