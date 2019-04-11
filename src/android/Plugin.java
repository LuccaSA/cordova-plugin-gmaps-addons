package plugin.gmaps.addons;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;

import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.List;
import java.util.Locale;

public class Plugin extends CordovaPlugin {

    public static final String TAG = "GMAPS-ADDONS";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("geocode")) {
            String address = args.getString(0);
            geocode(address, callbackContext);
            return true;

        } else if (action.equals("reverseGeocode")) {
            JSONObject coords = args.getJSONObject(0);
            reverseGeocode(coords, callbackContext);
            return true;

        }
        return false;
    }

    private void geocode(String address, CallbackContext callbackContext) {
        Geocoder geocoder = new Geocoder(cordova.getActivity(), Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 5);

            JSONArray jsonResult = new AddressParser().parse(addresses);

            callbackContext.success(jsonResult);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            callbackContext.error(e.getMessage());
        }
    }

    private void reverseGeocode(JSONObject coords, CallbackContext callbackContext) {
        Geocoder geocoder = new Geocoder(cordova.getActivity(), Locale.getDefault());

        try {
            Double lat = Double.parseDouble(coords.get("lat").toString());
            Double lng = Double.parseDouble(coords.get("lng").toString());

            List<Address> addresses = geocoder.getFromLocation(lat, lng, 5);
            JSONArray jsonResult = new AddressParser().parse(addresses);

            callbackContext.success(jsonResult);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            callbackContext.error(e.getMessage());
        }
    }
}