package net.wandroid.charge.http;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by zabbat on 2016-01-26.
 */
public abstract class AbstractHttpTask extends AsyncTask<Void,Void,String>{

    protected String getResponse(InputStream inputStream) throws IOException {
        String line;
        StringBuffer sb = new StringBuffer("");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }
        bufferedReader.close();
        //TODO: close streams
        return sb.toString();
    }

    public static class HttpException extends RuntimeException {
        public HttpException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }
    }

}
