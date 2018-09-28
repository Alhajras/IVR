package alexandraApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpClientConnection;
import org.apache.http.HttpHost;

import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectionRequest;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;

import org.apache.http.impl.conn.BasicHttpClientConnectionManager;

public class ComposerNovomind {

	private String showRoomNum = "showroom10";
	private String url;
	private String question = "main menu";
	private String cookie = "";
	HttpURLConnection connection;

	String ask(String question) throws IOException {

		StringBuffer content = new StringBuffer();
		if (!question.isEmpty())
			this.question = question;
		this.url = "https://" + showRoomNum + ".novomind.com/nmIQ/api/rest/ask/" + question.replace(" ", "%20");
		URL URLLink;
		try {
			URLLink = new URL(url);
			connection = (HttpURLConnection) URLLink.openConnection();
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setConnectTimeout(50000);
			connection.setRequestMethod("GET");
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

		connection.disconnect();
		return content.toString().split("\"response\"")[1].replaceAll("\"", "").replaceAll("\\\\n", "");
	}

	void closeTheConnection() {
		connection.disconnect();
	}
}
