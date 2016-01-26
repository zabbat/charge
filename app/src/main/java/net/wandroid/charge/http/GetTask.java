package net.wandroid.charge.http;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by zabbat on 2016-01-26.
 */
public class GetTask extends AbstractHttpTask {

    public static final String AUTHORIZATION = "Authorization";

    private final String mToken;
    private final IGetCompleteListener mGetCompleteListener;

    private HttpException mHttpException;

    public GetTask(String token, IGetCompleteListener getCompleteListener) {
        mToken = token;
        mGetCompleteListener = getCompleteListener;
    }

    @Override
    protected String doInBackground(Void... params) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL("http://api.test.thenewmotion.com/v1/me");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setRequestProperty("Authorization", "Bearer " + mToken);

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
            UserInfo userInfo = gson.fromJson(result, UserInfo.class);
            mGetCompleteListener.onCompleted(null, userInfo);
        } else {
            mGetCompleteListener.onCompleted(mHttpException, null);
        }

    }

    public interface IGetCompleteListener {
        void onCompleted(HttpException httpException, UserInfo userInfo);
    }

    public static class UserInfo {
        private String lastName;
        private String firstName;

        public UserInfo(String lastName, String firstName) {
            this.lastName = lastName;
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getFirstName() {
            return firstName;
        }
    }
}
