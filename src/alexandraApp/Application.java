package alexandraApp;

import java.io.IOException;
import java.util.Properties;

import javax.sound.sampled.LineUnavailableException;

import StaticsData.Language;
import StaticsData.VoicesTTS;
import net.sourceforge.javaflacencoder.FLACFileWriter;
import microphone.*;
import recognizer.*;

/**
 * This is where all begins .
 * 
 * @author GOXR3PLUS
 *
 */
public class Application {

	private String googleKey;
	private Language language = Language.ENGLISH;
	private VoicesTTS voice = VoicesTTS.ENGLISH_MALE_1;
	private final MarryTTS tts;
	private final Microphone mic = new Microphone(FLACFileWriter.FLAC);
	private final GSpeechDuplex duplex;
	String oldText = "";

	/**
	 * Constructor
	 * 
	 * @throws Exception
	 */
	public Application() throws Exception {

		LoadXmlProperties lxp = new LoadXmlProperties();
		Properties properties = lxp.readProperties();

		googleKey = properties.getProperty("google.key");
		language = Language.valueOf(properties.getProperty("language"));
		voice = VoicesTTS.valueOf(properties.getProperty("voice"));

		tts = new MarryTTS(voice);
		duplex = new GSpeechDuplex(googleKey);
		duplex.setLanguage(language.getLang());

		duplex.addResponseListener(new GSpeechResponseListener() {

			public void onResponse(GoogleResponse googleResponse) {
				String output = "";

				// Get the response from Google Cloud
				output = googleResponse.getResponse();
				System.out.println(output);
				if (output != null) {
					tts.makeDecision(output);
				} else
					System.out.println("Output was null");
			}
		});
		startSpeechRecognition();
	}

	/**
	 * Starts the Speech Recognition
	 */
	public void startSpeechRecognition() {
		// Start a new Thread so our application don't lags
		new Thread(() -> {
			try {
				duplex.recognize(mic.getTargetDataLine(), mic.getAudioFormat());
			} catch (LineUnavailableException | InterruptedException | IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	/**
	 * Stops the Speech Recognition
	 */
	public void stopSpeechRecognition() {
		mic.close();
		System.out.println("Stopping Speech Recognition...." + " , Microphone State is:" + mic.getState());
	}

	public static void main(String[] args) throws Exception {
		new Application();
	}

}
