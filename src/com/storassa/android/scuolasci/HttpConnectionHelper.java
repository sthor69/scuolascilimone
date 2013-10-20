package com.storassa.android.scuolasci;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpConnectionHelper {
	static HttpConnectionHelper helper;
	HttpURLConnection connection;
	HttpClient httpClient;
	CookieManager cookieManager;
	String[] result;
	
	private HttpConnectionHelper() {
		cookieManager = new CookieManager();
		CookieHandler.setDefault(cookieManager);
	}

	public static HttpConnectionHelper getHelper() {
		if (helper == null)
			helper = new HttpConnectionHelper();

		return helper;
	}

	public void openConnection(final HttpResultCallable callable, String username, String password) {

		result = new String[2];
		String urlParameters = "task=signin&accion=signin&textBoxUsername=";
		urlParameters += username;
		urlParameters += "&textBoxPassword=";
		urlParameters += password + "&buttonSubmit=";
		final String params = urlParameters;
		final URL url;
		try {
			url = new URL(
					"http://www.scuolascilimone.com/it/area-riservata/access/signin");
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			throw new RuntimeException(e1);
		}

		ExecutorService exec = Executors.newCachedThreadPool();
		exec.execute(new Runnable() {

			@Override
			public void run() {
				try {
					HttpURLConnection connection = (HttpURLConnection) url
							.openConnection();
					connection.setDoOutput(true);
					connection.setDoInput(true);
					connection.setInstanceFollowRedirects(false);
					connection.setRequestMethod("POST");
					connection.setRequestProperty("Content-Type",
							"application/x-www-form-urlencoded");
					connection.setRequestProperty("charset", "utf-8");
					connection.setRequestProperty("Content-Length", ""
							+ Integer.toString(params.getBytes().length));
					connection.setUseCaches(false);

					DataOutputStream wr = new DataOutputStream(connection
							.getOutputStream());
					wr.writeBytes(params);
					wr.flush();
					wr.close();
					connection.connect();
					BufferedInputStream in = new BufferedInputStream(connection
							.getInputStream());
					int responseCode = connection.getResponseCode();
					
					while (responseCode == 303) {
						
						String urlString = connection.getHeaderField("Location");
						URL redirectedUrl = new URL(urlString);
						connection = (HttpURLConnection) redirectedUrl
								.openConnection();
						connection.setDoOutput(true);
						connection.setDoInput(true);
						connection.setInstanceFollowRedirects(false);
						connection.setRequestMethod("GET");
						connection.setRequestProperty("Content-Type",
								"application/x-www-form-urlencoded");
						connection.setRequestProperty("charset", "utf-8");
						connection.setUseCaches(false);

						connection.connect();
						responseCode = connection.getResponseCode();
					}
					
					final int finalResponseCode = responseCode;

					in = new BufferedInputStream(connection
							.getInputStream());

					java.util.Scanner s = new java.util.Scanner(in)
							.useDelimiter("\\A");

					StringBuilder builder = new StringBuilder();
					while (s.hasNext())
						builder.append(s.next());

					result[0] = Integer.toString(finalResponseCode);
					result[1] = builder.toString();
					
					callable.resultAvailable(result);

				} catch (IOException e) {
					throw new RuntimeException(e);
				} finally {
//					connection.disconnect();
				}

			}
		});
	}

	public String openGenericConnection(String localUrl)
			throws ClientProtocolException, IOException {
		httpClient = new DefaultHttpClient();
		try {
			HttpGet httpget = new HttpGet(localUrl);

			// Create a response handler
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String responseBody = httpClient.execute(httpget, responseHandler);

			return responseBody;

		} finally {
			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			httpClient.getConnectionManager().shutdown();
		}
	}

	public HttpClient getGenericClient() {
		return httpClient;
	}

}
