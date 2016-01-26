package net.wandroid.charge.http;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * Base class for http communication
 */
public abstract class AbstractHttpTask extends AsyncTask<Void,Void,String>{

    /**
     * Reads from an inputStream and return the result as a string.
     * @param inputStream inputStream to read from
     * @return the input as a string
     * @throws IOException
     */
    protected String getResponse(InputStream inputStream) throws IOException {
        String line;
        StringBuffer sb = new StringBuffer("");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }
        bufferedReader.close();
        return sb.toString();
    }

    /**
     * Exception when using http
     */
    public static class HttpException extends RuntimeException {
        public HttpException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }
    }

}
