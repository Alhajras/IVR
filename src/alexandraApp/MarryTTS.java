package alexandraApp;

import java.io.IOException;

import StaticsData.VoicesTTS;
import marytts.TextToSpeech;
import marytts.signalproc.effects.JetPilotEffect;

public class MarryTTS {

	private final TextToSpeech tts = new TextToSpeech();
	String oldText = "";
	private static boolean writingFlag = true;

	public MarryTTS(VoicesTTS voice) {
		// ---------------MaryTTS Configuration-----------------------------
		//
		tts.setVoice(voice.getVoice()); // male USA // best english voice

		// JetPilotEffect
		JetPilotEffect jetPilotEffect = new JetPilotEffect(); // epic fun!!!
		jetPilotEffect.setParams("amount:100");

		// Apply the effects
		// tts.getMarytts().setAudioEffects(jetPilotEffect.getFullEffectAsString());// +
		// "+" +
		// stadiumEffect.getFullEffectAsString());

	}

	public void makeDecision(String output) {
		output = output.trim();
		// System.out.println(output.trim());

		// We don't want duplicate responses
		if (!oldText.equals(output))
			oldText = output;
		else
			return;

		if (output.contains("hello")) {// Hello
			speak("Who is there");

		} else if (output.contains("introduce yourself")) {// Introduce your self
			speak("My name is Alexandra , i am 27 years old");

		} else if (output.contains("oh boy") || output.contains("obey")) {// obey
			speak("Never to you! You are not my addiction.");

		} else if (output.contains("Hallo")) {// obey
			speak("ich bin hamad und ich mach immer die tur auf.");

		} else if (output.contains("what is your profession"))

		{// what is your profession
			speak("I am a Psychologist");

		} else if (output.contains("do you have any boyfriend")) { // do you have any boyfriend
			speak("Yes");

		} else if (output.contains("where do you live")) {// where do you live
			speak("I am not going to tell you ma nigga");

		} else if (output.contains("I think you're funny") || output.contains("I think you are funny")) {// I think you
																											// are funny
			speak("Yeah you too!");

		} else if (output.contains("let me Smash")) {// I think you are funny
			speak("Dont make me horny ");

		} else if (output.contains("damn girl")) {// damn girl
			speak("Shut the fuck up");
		} else if (output.contains("like a tetraplegic")) {// like a tetraplegic
			speak("It's not of your business");

		} else if (output.contains("who's your daddy") || output.contains("but I am the boss")) {// but I'm the boss
			speak("Fuck off Alex");

		} else if (output.contains("show me some respect")) {// like a tetraplegic
			speak("Ok i will try");

		} else if (output.contains("tell me a story")) {// tell me a story
			speak("I dont know any story");

		} else if (output.contains("why do you speak like that")) {// why do you speak like that
			speak("Like what?");

		} else if (output.contains("say hi to Martina")) {// Stop Speech Recognition
			speak("Hello babe how are you doing ?");

		} else if (output.contains("stop speech recognition")) {// Stop Speech Recognition
			// stopSpeechRecognition();

		} else { // Nothing matches here ?
			System.out.println("Not entered on any else if statement");
		}

	}

	/**
	 * Calls the MaryTTS to say the given text
	 * 
	 * @param text
	 */
	public void speak(String text) {
		System.out.println(text);
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
