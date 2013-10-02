package com.storassa.android.scuolasci;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpConnectionHelper {
    static HttpConnectionHelper helper;
    HttpURLConnection connection;
    CookieManager cookieManager;

    private HttpConnectionHelper() throws MalformedURLException {
        cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
    }

    public static HttpConnectionHelper getHelper()
            throws MalformedURLException {
        if (helper == null)
            helper = new HttpConnectionHelper();

        return helper;
    }

    public String openConnection(String username, String password) {
        try {
            String urlParameters = "task=signin&accion=signin&textBoxUsername=";
            urlParameters += username;
            urlParameters += "&textBoxPassword=";
            urlParameters += password;

            URL url = new URL(
                    "http://www.scuolascilimone.com/it/area-riservata/access/signin");

            HttpURLConnection connection = (HttpURLConnection) url
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

            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
            BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
            
            java.util.Scanner s = new java.util.Scanner(in).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return "ERROR:\n" + e.getStackTrace();
        }
    }

    public String openGenericConnection(String localUrl) throws ClientProtocolException, IOException {
        HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpGet httpget = new HttpGet(localUrl);

            // Create a response handler
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String responseBody = httpclient.execute(httpget, responseHandler);
            
            return responseBody;

        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
    }

}
