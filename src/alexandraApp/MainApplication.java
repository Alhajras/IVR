package alexandraApp;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

import javax.sound.sampled.LineUnavailableException;

import org.apache.tomcat.util.http.fileupload.IOUtils;

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
public class MainApplication {

	private String statusFilePath;
	private String googleAPIFilePath;
	private String googleKey;
	private Language language = Language.ENGLISH;
	private VoicesTTS voice = VoicesTTS.ENGLISH_MALE_1;
	private final MarryTTS tts;

	/**
	 * Constructor
	 * 
	 * @throws Exception
	 */
	public MainApplication() throws Exception {

		LoadXmlProperties lxp = new LoadXmlProperties();
		GoogleApi api = new GoogleApi();
		Properties properties = lxp.readProperties();

		googleKey = properties.getProperty("google.key");
		language = Language.valueOf(properties.getProperty("language"));
		voice = VoicesTTS.valueOf(properties.getProperty("voice"));
		statusFilePath = properties.getProperty("file.status");
		googleAPIFilePath = properties.getProperty("google.file");

		tts = new MarryTTS(voice);

		while (true) {
			int status = Integer.parseInt(readingFile(statusFilePath));
			switch (status) {
			case 4:

				try {
					String googleSTTService = api.POSTRequest(googleAPIFilePath, googleKey);
					if (!googleSTTService.isEmpty())

						tts.speak(googleSTTService);
					else
						tts.speak("I could not find anything of what you said, can you repeat please.");
					writingIntoFile("2", statusFilePath);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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

	public static void writingIntoFile(String content, String path) {

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
			bw.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		new MainApplication();
	}

}
