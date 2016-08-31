package plugin.gmaps.addons;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class DirectionsReadTask extends AsyncTask<String, Void, String> {
    private ICallBackListener _listener;

    public DirectionsReadTask(ICallBackListener listener) {
        _listener = listener;
    }

    @Override
    protected String doInBackground(String... url) {
        String data = "";
        try {
            HttpConnection http = new HttpConnection();
            data = http.readUrl(url[0]);
        } catch (Exception e) {
            Log.d("HTTP call error", e.toString());
        }
        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        try {
            _listener.callback(new JSONObject(result));

        } catch (JSONException e) {
            Log.d("Error parsing direction", e.toString());
        }
    }
}
