package org.spatialia.santa.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;

public class Post {

	public static String doPost(String url, String urlParameters)
			throws Exception {
		HttpURLConnection connection = (HttpURLConnection) getURL(url)
				.openConnection();
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setInstanceFollowRedirects(false);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		connection.setRequestProperty("charset", "utf-8");
		connection.setRequestProperty("Content-Length",
				"" + Integer.toString(urlParameters.getBytes().length));
		connection.setUseCaches(false);

		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		BufferedReader rd = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));

		String response = "";
		String line = null;
		while ((line = rd.readLine()) != null) {
			response += line;
		}
		rd.close();
		connection.disconnect();
		return response;
	}

	private static URL getURL(String url) throws Exception {
		URL u = new URL(url);
		if (url.endsWith("?")) {
			Field f = u.getClass().getDeclaredField("file");
			f.setAccessible(true);

			String file = (String) f.get(u);

			if (!file.endsWith("?")) {
				f.set(u, file + "?");
			}
		}
		return u;
	}
}
