package net.wandroid.charge.http;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;


public class GetTask extends AbstractHttpTask {

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final String GET = "GET";
    public static final String HTTP_API_TEST_THENEWMOTION_COM_V1_ME = "http://api.test.thenewmotion.com/v1/me";

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
            URL url = new URL(HTTP_API_TEST_THENEWMOTION_COM_V1_ME);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(GET);
            connection.setDoInput(true);
            connection.setRequestProperty(AUTHORIZATION, BEARER + mToken);

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
        /**
         * Callback when the task has finished
         * @param httpException Will be null if no error occured,
         * @param userInfo the retreived UserInfo. Will be null if there is an error
         */
        void onCompleted(HttpException httpException, UserInfo userInfo);
    }

    /**
     * UserInfo clas for Json Conversion
     * This class has not implmented all members as they are not used for this application
     */
    public static class UserInfo implements Serializable {
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
