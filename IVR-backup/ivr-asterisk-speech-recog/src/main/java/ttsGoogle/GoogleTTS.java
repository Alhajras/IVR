package ttsGoogle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import interfaces.TTSInterface;
import synthesiser.SynthesiserV2;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

/**
 * This is where all begins .
 * 
 * @author GOXR3PLUS
 *
 */
public class GoogleTTS implements TTSInterface {

	// Create a Synthesizer instance
	SynthesiserV2 synthesizer;

	/**
	 * Constructor
	 */
	public GoogleTTS() {
		synthesizer = new SynthesiserV2("AIzaSyBOti4mM-6x9WDnZIjIeyEU21OpBXqWBgw");
	}

	/**
	 * Calls the MaryTTS to say the given text
	 * 
	 * @param text
	 */
	public void speak(String text, String callerId) {
		// Create a new Thread because JLayer is running on the current Thread and will
		// make the application to lag
		try {
			// Create a JLayer instance
			AdvancedPlayer player = new AdvancedPlayer(synthesizer.getMP3Data(text));

			File file = new File("D:\\IVR\\SharedFolderWinLinux\\marryTTSOutput\\8k\\marryTTS" + callerId + ".mp3");
			try {
				InputStream in = synthesizer.getMP3Data(text);
				OutputStream out = new FileOutputStream(file);
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				out.close();
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			// player.play();
			//
			// System.out.println("Successfully got back synthesizer data");

		} catch (IOException | JavaLayerException e) {

			e.printStackTrace(); // Print the exception ( we want to know , not hide below our finger , like many
									// developers do...)

		}

	}
}
