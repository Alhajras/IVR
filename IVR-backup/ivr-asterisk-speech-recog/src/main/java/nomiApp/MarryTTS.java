package nomiApp;

import java.io.IOException;

import StaticsData.VoicesTTS;
import lombok.Getter;
import lombok.Setter;
import marytts.TextToSpeech;

//import marytts.signalproc.effects.JetPilotEffect; this is for sounds effects if needed
@Getter
@Setter
public class MarryTTS {

	private final TextToSpeech tts = new TextToSpeech();
	private float voiceGain;

	public MarryTTS(VoicesTTS voice, float voiceGain) {
		// ---------------MaryTTS Configuration-----------------------------
		//
		this.voiceGain = voiceGain;
		tts.setVoice(voice.getVoice()); // Male USA // Best English voice

		// JetPilotEffect to add sound effects
		// JetPilotEffect jetPilotEffect = new JetPilotEffect(); this is for sounds
		// effects if needed
		// jetPilotEffect.setParams("amount:100"); this is for sounds effects if needed
	}

	/**
	 * Calls the MaryTTS to say the given text
	 * 
	 * @param text
	 */
	public void speak(String text, String callerId) {
		if (!tts.isSpeaking())
			try {
				tts.speak(text, voiceGain, true, false, callerId);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

}
