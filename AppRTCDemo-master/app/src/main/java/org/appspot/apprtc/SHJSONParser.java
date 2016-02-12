package org.appspot.apprtc;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 *  SHJSONParser
 *  isho <isho@unist.ac.kr>
 *  2015. 11. 13..
 */

/*
    {
        ...
        new SHJSONParser().setCallback(callback).execute(URL);
    }

    private SHJSONParserCallback callback = new SHJSONParserCallback() {
        @Override
        public void onResult(JSONObject json, int parserTag) {
            try {
                if (json == null || (Integer)json.get("result") != 0) {
                    //TODO: ERROR
                    return;
                }

                //TODO: Processing...

            } catch (JSONException e) {
                //e.printStackTrace();
                //TODO: ERROR
            }
        }

        @Override
        public void exceptionOccured(Exception e) {
        }

        @Override
        public void cancelled() {
        }
    };
*/

public class SHJSONParser extends AsyncTask<String, Void, JSONObject> {
    private SHJSONParserCallback callback;
    private Exception occuredException;
    private int _tag = 0;

    public SHJSONParser setCallback(SHJSONParserCallback callback) {
        this.callback = callback;
        return this;
    }
    public SHJSONParser setTag(int tag) {
        _tag = tag;
        return this;
    }

    protected JSONObject doInBackground(String... args) {
        DefaultHttpClient client = new DefaultHttpClient();
        try {
            String URL = args[0];
            HttpGet post = new HttpGet(URL);

				/* 지연시간 최대 7초 */
            HttpParams params = client.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 7000);
            HttpConnectionParams.setSoTimeout(params, 7000);

				/* 데이터 보낸 뒤 서버에서 데이터를 받아오는 과정 */
            HttpResponse response = client.execute(post);
            BufferedReader bufreader = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent(), "utf-8"));

            String line;
            String result = "";
            while ((line = bufreader.readLine()) != null) {
                result += line;
            }

            System.out.println(result);

            return new JSONObject(result);
        }catch (Exception ex) {
            client.getConnectionManager().shutdown();    // 연결 지연 종료
            Log.e("www", "exception occured while doing in background: " + ex.getMessage(), ex);
            this.occuredException = ex;
        }

        return null;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        if (isCancelled()) {
            if (callback != null)
                callback.cancelled();
            return;
        }
        if (isExceptionOccured()) {
            if (callback != null)
                callback.exceptionOccured(occuredException);
            return;
        }

        if (callback != null)
            callback.onResult(result, _tag);
    }
    private boolean isExceptionOccured() {
        return occuredException != null;
    }
}
