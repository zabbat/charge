package net.wandroid.charge.http;

import android.support.annotation.Nullable;

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PostTask extends AbstractHttpTask {
    public static final String OAUTH2_ACCESS_URL = "http://api.test.thenewmotion.com/oauth2/access_token";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String AUTHORIZATION = "Authorization";
    public static final String APPLICATION_URL_ENCODED = "application/x-www-form-urlencoded";
    public static final String BASIC_BASE64_SECRET = "Basic " + "dGVzdF9jbGllbnRfaWQ6dGVzdF9jbGllbnRfc2VjcmV0=";
    public static final String GRANT_TYPE = "grant_type";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String ENCODING = "UTF-8";
    public static final String POST = "POST";
    private final String mEmail;
    private final String mPassword;
    private final IPostCompletionListener mPostCompletionListener;
    private HttpException mHttpException;

    public PostTask(String email, String password, IPostCompletionListener postCompletionListener) {
        this.mEmail = email;
        this.mPassword = password;
        mPostCompletionListener = postCompletionListener;
    }

    @Override
    protected String doInBackground(Void... params) {

        HttpURLConnection connection = null;
        try {
            URL url = new URL(OAUTH2_ACCESS_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(POST);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty(CONTENT_TYPE, APPLICATION_URL_ENCODED);
            //test_client_id:test_client_secretbase64: invalid input
            connection.setRequestProperty(AUTHORIZATION, BASIC_BASE64_SECRET);

            // add data params
            OutputStream os = connection.getOutputStream();
            Map<String, String> data = new HashMap<>();
            data.put(GRANT_TYPE, "password");
            data.put(USERNAME, mEmail);
            data.put(PASSWORD, mPassword);
            String dataString = getDataString(data);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, ENCODING));
            bufferedWriter.write(dataString);
            bufferedWriter.flush();
            bufferedWriter.close();
            os.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String responseString = getResponse(connection.getInputStream());
                return responseString;
            } else {
                throw new IOException("Response code is not HTTP_OK: " + responseCode);
            }

        } catch (IOException e) {
            mHttpException = new HttpException(e.getMessage(), e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (mHttpException == null) {
            Gson gson = new Gson();
            AuthResponse authResponse = gson.fromJson(result, AuthResponse.class);
            mPostCompletionListener.onComplete(mHttpException, authResponse);
        } else {
            mPostCompletionListener.onComplete(mHttpException, null);
        }

    }

    private String getDataString(Map<String, String> data) {
        if (data.isEmpty()) {
            throw new IllegalArgumentException("Data cannot be empty");
        }
        StringBuilder sb = new StringBuilder("");
        for (String name : data.keySet()) {
            sb.append(name).append("=").append(data.get(name)).append("&");
        }

        //remove trailing '&'
        sb.deleteCharAt(sb.lastIndexOf("&"));
        return sb.toString();
    }

    public interface IPostCompletionListener {
        void onComplete(@Nullable HttpException httpException, AuthResponse authResponse);
    }


    public static class AuthResponse {
        private String token_type;
        private String access_token;
        private String expires_in;
        private String refresh_token;

        public AuthResponse(String token_type, String access_token, String expires_in, String refresh_token) {
            this.token_type = token_type;
            this.access_token = access_token;
            this.expires_in = expires_in;
            this.refresh_token = refresh_token;
        }

        public String getTokenType() {
            return token_type;
        }

        public String getAccessToken() {
            return access_token;
        }

        public String getExpiresIn() {
            return expires_in;
        }

        public String getRefreshToken() {
            return refresh_token;
        }
    }
}
