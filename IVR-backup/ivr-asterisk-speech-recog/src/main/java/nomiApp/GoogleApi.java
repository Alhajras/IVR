package nomiApp;

import com.google.cloud.speech.v1p1beta1.RecognitionConfig;
import com.google.cloud.speech.v1p1beta1.RecognitionConfig.AudioEncoding;
//import com.google.protobuf.ByteString;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

public class GoogleApi {

	public String POSTRequest(String... args) throws IOException {
		String responseText = "";
		/*** The path to the audio file to transcribe */
		String fileName = args[0];
		// System.out.println(encodeFileToBase64Binary(fileName));

		/*** Builds the sync recognize request */
		RecognitionConfig config = RecognitionConfig.newBuilder().setEncoding(AudioEncoding.FLAC)
				.setSampleRateHertz(16000).setLanguageCode(args[2]).build();

		/*** Manually creating the JSON object */
		final String POST_PARAMS = "{\r\n" + "  \"config\": {\r\n" + "      \"encoding\": \"" + config.getEncoding()
				+ "\",\r\n" + "      \"sampleRateHertz\": " + config.getSampleRateHertz() + ",\r\n"
				+ "      \"languageCode\": \"" + config.getLanguageCode() + "\",\r\n"
				+ "      \"enableWordTimeOffsets\": false\r\n" + "  },\r\n" + "  \"audio\": {\r\n"
				+ "      \"content\":\"" + encodeFileToBase64Binary(fileName) + "\"\r\n" + "  }\r\n" + "}";
		// System.out.println(POST_PARAMS);

		/** Starting the connection with the iHELP server */
		URL obj = new URL("https://speech.googleapis.com/v1/speech:recognize?key=" + args[1]);
		HttpURLConnection postConnection = (HttpURLConnection) obj.openConnection();
		postConnection.setRequestMethod("POST");
		postConnection.setRequestProperty("Content-Type", "application/json");
		postConnection.setDoOutput(true);
		OutputStream os = postConnection.getOutputStream();
		os.write(POST_PARAMS.getBytes());
		os.flush();
		os.close();
		// System.out.println("POST Response Code : " + responseCode);
		// System.out.println("POST Response Message : " +
		// postConnection.getResponseMessage());
		BufferedReader in = new BufferedReader(new InputStreamReader(postConnection.getInputStream()));
		String inputLine;

		while ((inputLine = in.readLine()) != null) {
			if (inputLine.contains("transcript"))
				responseText = inputLine.split("\"transcript\": ")[1].replaceAll("\"", "").replaceAll(",", "");
		}
		// System.out.println(responseText);
		in.close();
		// print result
		// System.out.println(response.toString());
		return responseText;
	}

	/** This function is responsible for encoding the given file into base64 */
	private static String encodeFileToBase64Binary(String fileName) throws IOException {
		File file = new File(fileName);
		byte[] encoded = Base64.encodeBase64(FileUtils.readFileToByteArray(file));
		return new String(encoded, StandardCharsets.US_ASCII);

	}

}