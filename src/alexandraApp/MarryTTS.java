package alexandraApp;

import java.io.IOException;

import StaticsData.VoicesTTS;
import marytts.TextToSpeech;
import marytts.signalproc.effects.JetPilotEffect;

public class MarryTTS {

	private final TextToSpeech tts = new TextToSpeech();

	public MarryTTS(VoicesTTS voice) {
		// ---------------MaryTTS Configuration-----------------------------
		//
		tts.setVoice(voice.getVoice()); // Male USA // Best English voice

		// JetPilotEffect to add sound effects
		JetPilotEffect jetPilotEffect = new JetPilotEffect();
		jetPilotEffect.setParams("amount:100");
	}

	/**
	 * Calls the MaryTTS to say the given text
	 * 
	 * @param text
	 */
	public void speak(String text) {
		if (!tts.isSpeaking())
			try {
				tts.speak(text, 2.0f, true, false);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

}
