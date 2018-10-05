package nomiApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/** This class is responsible for fetching the answer from Nomi */
public class ComposerNovomind {

	private String url;
	private String cookie = "";
	HttpURLConnection connection;

	public String ask(String question, String knowlowadgeBaseUrl) throws IOException {
		StringBuffer content = new StringBuffer();
		if (question.isEmpty())
			question = "main menu";
		/** Replacing the whie space with %20 for the GET HTTP request */
		this.url = knowlowadgeBaseUrl + question.replace(" ", "%20");
		URL URLLink;
		try {
			URLLink = new URL(url);
			connection = (HttpURLConnection) URLLink.openConnection();
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setConnectTimeout(50000);
			connection.setRequestMethod("GET");
			/**
			 * It is crucial to save the cookie to maintain the session even if the user
			 * Closed by mistake he can come back to the same place he left
			 */
			if (!cookie.isEmpty())
				connection.setRequestProperty("Cookie", cookie);
			connection.connect();
			if (cookie.isEmpty())
				cookie = connection.getHeaderField("Set-Cookie");

			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				if (inputLine.contains("response")) {
					content.append(inputLine);
				}

			}
			in.close();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "I could not find anything of what you said, can you repeat please.";
		}

		return content.toString().split("\"response\"")[1].replaceAll("\"", "").replaceAll("\\\\n", "");
	}

	/** Ending the session */
	void closeTheConnection() {
		connection.disconnect();
	}
}
