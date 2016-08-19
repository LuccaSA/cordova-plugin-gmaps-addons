package com.cleemy.maps;

import android.os.AsyncTask;

import org.json.JSONObject;

public class ParserTask extends AsyncTask<String, Integer, JSONObject> {

    private ICallBackListener _listener;

    public ParserTask(ICallBackListener listener) {
        _listener = listener;
    }

    @Override
    protected JSONObject doInBackground(String... jsonData) {

        JSONObject jObject;
        JSONObject routeInfos = new JSONObject();

        try {
            jObject = new JSONObject(jsonData[0]);
            PathJSONParser parser = new PathJSONParser();
            routeInfos = parser.parse(jObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return routeInfos;
    }

    @Override
    protected void onPostExecute(JSONObject route) {
        _listener.callback(route);
    }
}