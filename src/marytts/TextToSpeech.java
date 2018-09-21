package marytts;

import java.io.File;

import java.io.IOException;

import java.io.UnsupportedEncodingException;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;

import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;
import marytts.modules.synthesis.Voice;
import marytts.signalproc.effects.AudioEffect;
import marytts.signalproc.effects.AudioEffects;

/**
 * @author GOXR3PLUS
 *
 */
public class TextToSpeech {
	private AudioPlayer tts;
	private MaryInterface marytts;
	private static boolean iAmBusy = false;

	/**
	 * Constructor
	 */
	public TextToSpeech() {
		try {
			marytts = new LocalMaryInterface();

		} catch (MaryConfigurationException ex) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
		}
	}

	// ----------------------GENERAL
	// METHODS---------------------------------------------------//

	/**
	 * Transform text to speech
	 * 
	 * @param text
	 *            The text that will be transformed to speech
	 * @param daemon
	 *            <br>
	 *            <b>True</b> The thread that will start the text to speech Player
	 *            will be a daemon Thread <br>
	 *            <b>False</b> The thread that will start the text to speech Player
	 *            will be a normal non daemon Thread
	 * @param join
	 *            <br>
	 *            <b>True</b> The current Thread calling this method will
	 *            wait(blocked) until the Thread which is playing the Speech finish
	 *            <br>
	 *            <b>False</b> The current Thread calling this method will continue
	 *            freely after calling this method
	 * @throws UnsupportedEncodingException
	 */
	public void speak(String text, float gainValue, boolean daemon, boolean join) throws UnsupportedEncodingException {
		if (iAmBusy)
			return;
		iAmBusy = true;
		// Stop the previous player
		stopSpeaking();

		try (AudioInputStream audio = marytts.generateAudio(text)) {

			String fileNameSource = "D:\\IVR\\SharedFolderWinLinux\\marryTTSOutput\\16k\\marryTTS.wav";
			String fileNameDestinaion = "D:\\IVR\\SharedFolderWinLinux\\marryTTSOutput\\8k\\marryTTS.wav";
			try {
				File file = new File(fileNameSource);
				AudioSystem.write(audio, Type.WAVE, file);
				fileDownSampler(fileNameSource, fileNameDestinaion);

			} catch (Exception e) {
				e.printStackTrace();
			}

			// Player is a thread(threads can only run one time) so it can be
			// used has to be initiated every time
			tts = new AudioPlayer();
			tts.setAudio(audio);
			tts.setGain(gainValue);
			tts.setDaemon(daemon);
			tts.start();
			if (join)
				tts.join();

		} catch (SynthesisException ex) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, "Error saying phrase.", ex);
		} catch (IOException ex) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, "IO Exception", ex);
		} catch (InterruptedException ex) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, "Interrupted ", ex);
			tts.interrupt();
		}
		iAmBusy = false;
	}

	/**
	 * Returns true if the MaryTTS is speaking or false if not
	 * 
	 * @return
	 */
	public boolean isSpeaking() {
		return tts != null && tts.isAlive();
	}

	/**
	 * Stop the MaryTTS from Speaking
	 */
	public void stopSpeaking() {
		// Stop the previous player
		if (tts != null)
			tts.cancel();
	}

	// ----------------------GETTERS---------------------------------------------------//

	/**
	 * Available voices in String representation
	 * 
	 * @return The available voices for MaryTTS
	 */
	public Collection<Voice> getAvailableVoices() {
		return Voice.getAvailableVoices();
	}

	/**
	 * @return the marytts
	 */
	public MaryInterface getMarytts() {
		return marytts;
	}

	/**
	 * Return a list of available audio effects for MaryTTS
	 * 
	 * @return
	 */
	public List<AudioEffect> getAudioEffects() {
		return StreamSupport.stream(AudioEffects.getEffects().spliterator(), false).collect(Collectors.toList());
	}

	// ----------------------SETTERS---------------------------------------------------//

	/**
	 * Change the default voice of the MaryTTS
	 * 
	 * @param voice
	 */
	public void setVoice(String voice) {
		marytts.setVoice(voice);
	}

	/**
	 * Function to down-sample an audioFile from 16khz to 8khz
	 * 
	 * @param voice
	 */

	public static void fileDownSampler(String source, String destination)
			throws InterruptedException, UnsupportedAudioFileException, IOException {
		System.out.println("Converting the file into 8khz ");
		File file = new File(source);
		File output = new File(destination);

		AudioInputStream ais;
		AudioInputStream eightKhzInputStream = null;
		ais = AudioSystem.getAudioInputStream(file);
		AudioFormat sourceFormat = ais.getFormat();
		if (ais.getFormat().getSampleRate() == 16000f) {
			AudioFileFormat sourceFileFormat = AudioSystem.getAudioFileFormat(file);
			AudioFileFormat.Type targetFileType = sourceFileFormat.getType();

			AudioFormat targetFormat = new AudioFormat(sourceFormat.getEncoding(), 8000f,
					sourceFormat.getSampleSizeInBits(), sourceFormat.getChannels(), sourceFormat.getFrameSize(), 8000f,
					sourceFormat.isBigEndian());
			if (!AudioSystem.isFileTypeSupported(targetFileType)
					|| !AudioSystem.isConversionSupported(targetFormat, sourceFormat)) {
				throw new IllegalStateException("Conversion not supported!");
			}
			eightKhzInputStream = AudioSystem.getAudioInputStream(targetFormat, ais);
			int nWrittenBytes = 0;

			nWrittenBytes = AudioSystem.write(eightKhzInputStream, targetFileType, output);
			System.out.println("nWrittenBytes: " + nWrittenBytes);
		}
	}

}
