package plugin.gmaps.addons;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.json.JSONException;
import org.json.JSONObject;

public class DirectionsReadTask extends AsyncTask<String, Void, String> {
    private ICallBackListener _listener;
    private CallbackContext _callbackContext;

    public DirectionsReadTask(ICallBackListener listener, CallbackContext callbackContext) {
        _listener = listener;
        _callbackContext = callbackContext;
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
            _listener.callback(new JSONObject(result), _callbackContext);

        } catch (JSONException e) {
            Log.d("Error parsing direction", e.toString());
        }
    }
}
