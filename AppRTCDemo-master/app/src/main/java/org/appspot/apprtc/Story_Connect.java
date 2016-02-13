package org.appspot.apprtc;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Donghyun Na on 2016-02-13.
 */


public class Story_Connect extends AsyncTask <String, Void, String>{


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        String sum = "";

        ConnectActivity call = new ConnectActivity();
        //call.connectToRoom(true,10000,"357357357");
        return sum;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if(result != null){
            Log.d("ASYNC", "result = " + result);
        }

    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

}
