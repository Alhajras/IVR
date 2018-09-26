package alexandraApp;

import java.io.IOException;

import StaticsData.VoicesTTS;
import marytts.TextToSpeech;
import marytts.signalproc.effects.JetPilotEffect;

public class MarryTTS {

	private final TextToSpeech tts = new TextToSpeech();
	private static boolean writingFlag = true;

	public MarryTTS(VoicesTTS voice) {
		// ---------------MaryTTS Configuration-----------------------------
		//
		tts.setVoice(voice.getVoice()); // male USA // best english voice

		// JetPilotEffect
		JetPilotEffect jetPilotEffect = new JetPilotEffect(); // epic fun!!!
		jetPilotEffect.setParams("amount:100");
	}

	/**
	 * Calls the MaryTTS to say the given text
	 * 
	 * @param text
	 */
	public void speak(String text) {
		if (writingFlag == false)
			return;

		writingFlag = false;
		// Check if it is already speaking
		if (!tts.isSpeaking())
			try {
				tts.speak(text, 2.0f, true, false);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		writingFlag = true;
	}

}
