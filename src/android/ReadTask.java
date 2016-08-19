package com.cleemy.maps;

import android.os.AsyncTask;
import android.util.Log;

public class ReadTask extends AsyncTask<String, Void, String> {
    private ICallBackListener _listener;

    public ReadTask(ICallBackListener listener) {
        _listener = listener;
    }

    @Override
    protected String doInBackground(String... url) {
        String data = "";
        try {
            HttpConnection http = new HttpConnection();
            data = http.readUrl(url[0]);
        } catch (Exception e) {
            Log.d("Background Task", e.toString());
        }
        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        new ParserTask(_listener).execute(result);
    }
}
