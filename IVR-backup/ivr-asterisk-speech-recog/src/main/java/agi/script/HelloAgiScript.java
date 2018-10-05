package agi.script;

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

@Setter
@Getter
public class HelloAgiScript extends BaseAgiScript {

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

	public void service(AgiRequest request, AgiChannel channel) throws AgiException {
		// Answer the channel...
		answer();
		cn = new ComposerNovomind();
		api = new GoogleApi();
		try {
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		tts = new MarryTTS(voice, gainValue);
		while (true) {
			try {
				exec("System", "cp -a /media/sf_SharedFolderWinLinux/marryTTSOutput/8k/. /var/lib/asterisk/sounds/en");
				switch (statesMachine.getIndex()) {
				case 1:
					case1();
					break;

				case 2:
					case2();
					break;

				case 3:
					case3();
					break;

				case 4:
					case4();
					break;

				case 5:
					case5();
					break;

				case 6:
					case6();
					break;

				case 7:
					case7();
					break;

				default:
					break;
				}
			} catch (Exception e) {
				tts.speak(languageMessages.getProperty("I_did_not_find_your_request"), uniqueID);
				hangup();
				e.printStackTrace();
			}
		}
	}

	private void case1() {
		try {
			exec("Record", "asterisk-recording" + uniqueID + ":wav,2");
			statesMachine = StatesMachine.CONVERTFILE;
		} catch (AgiException e) {
			e.printStackTrace();
		}

	}

	public void case2() throws Exception {
		streamFile("marryTTS" + uniqueID);
		statesMachine = StatesMachine.RECORD;
	}

	private void case3() {
		try {
			exec("System", "sox /var/lib/asterisk/sounds/asterisk-recording" + uniqueID
					+ ".wav --rate 16k --bits 16 /media/sf_SharedFolderWinLinux/asteriskOutput/asterisk-recording"
					+ uniqueID + ".flac");
			statesMachine = StatesMachine.PROCESSING_REQUEST;
		} catch (AgiException e) {
			e.printStackTrace();
		}

	}

	public void case4() throws Exception {
		String googleSTTService = "";
		String nomiResponse;
		try {

			googleSTTService = api.POSTRequest(googleAPIFilePath + "asterisk-recording" + uniqueID + ".flac", googleKey,
					language.getLang());

			System.err.println("google: " + googleSTTService.replaceAll("[-+.^:,]", ""));
			if (!googleSTTService.isEmpty()) {
				nomiResponse = cn.ask(googleSTTService.replaceAll("[-+.^:,]", ""), knowlowadgeBaseUrl);
				System.err.println("nomi: " + nomiResponse);
				if (!nomiResponse.isEmpty()) {
					tts.speak(nomiResponse, uniqueID);
				}

			} else
				tts.speak(languageMessages.getProperty("I_did_not_find_your_request"), uniqueID);

		} catch (Exception e) {
			tts.speak(languageMessages.getProperty("I_did_not_find_your_request"), uniqueID);
			e.printStackTrace();
		}
		if (Pattern.matches(".*(customer(|s) service(|s)).*", googleSTTService))
			statesMachine = StatesMachine.CALL_CUSTOMER_SERVICE;
		else if (Pattern.matches(".*(authentication(|s) need(|ed)).*", googleSTTService)) {
			statesMachine = StatesMachine.CHECKING_AUTHENTICATION;
		} else
			statesMachine = StatesMachine.PLAYBACK;

	}

	private void case5() {
		try {
			exec("Dial", "SIP/" + customerServiceNumber);
		} catch (AgiException e) {
			e.printStackTrace();
		}
	}

	public void case6() throws Exception {
		String nomiResponse;
		nomiResponse = cn.ask("main menu", knowlowadgeBaseUrl);
		// System.out.println("Status 6: " + nomiResponse);
		tts.speak(nomiResponse, uniqueID);
		statesMachine = StatesMachine.PLAYBACK;
	}

	public void case7() throws Exception {

		int numberOfTries = 0;
		PIN = generatePIN();
		setVariable("PIN", PIN);
		try {
			exec("Gosub", "sending-PIN", "${EXTEN:1}", "1");
			tts.speak(languageMessages.getProperty(
					"This service needs an authentication code, please say the four PIN code digits after the beep"),
					uniqueID);
			while (true) {
				exec("System", "cp -a /media/sf_SharedFolderWinLinux/marryTTSOutput/8k/. /var/lib/asterisk/sounds/en");
				streamFile("marryTTS" + uniqueID);
				exec("Record", "asterisk-recording" + uniqueID + ":wav,2");
				String googleSTTService = api.POSTRequest(googleAPIFilePath + "asterisk-recording" + uniqueID + ".flac",
						googleKey, language.getLang());
				if (googleSTTService.equals(PIN)) {
					tts.speak(languageMessages.getProperty("Your entry is correct."), uniqueID);
					cn.ask("VALID", knowlowadgeBaseUrl);
					break;
				} else {
					numberOfTries++;
					tts.speak(languageMessages.getProperty("Your entry is invalid.Please repeate the PIN number"),
							uniqueID);
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

	public String generatePIN() {
		int x = (int) (Math.random() * 9);
		x = x + 1;
		String randomPIN = (x + "") + (((int) (Math.random() * 1000)) + "");
		return randomPIN;
	}

}
