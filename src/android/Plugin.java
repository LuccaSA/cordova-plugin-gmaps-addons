package plugin.gmaps.addons;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;

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
    private GoogleApiClient _googleApiClient;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        _googleApiClient = new GoogleApiClient.Builder(cordova.getActivity()).addApi(Places.GEO_DATA_API).build();

        _googleApiClient.connect();

        super.initialize(cordova, webView);
    }

    @Override
    public void onDestroy() {
        _googleApiClient.disconnect();
        super.onDestroy();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("autocomplete")) {
            String query = args.getString(0);
            autocomplete(query, callbackContext);
            return true;

        } else if (action.equals("geocode")) {
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

    private void autocomplete(String query, final CallbackContext callbackContext) {
        if (query.isEmpty()) {
            callbackContext.error("Expected one non-empty string argument.");
        }

        PendingResult<AutocompletePredictionBuffer> result = Places.GeoDataApi
                .getAutocompletePredictions(_googleApiClient, query, null, null);
        AutocompletePredictionBuffer autocompletePredictions = result.await();

        JSONArray jsonResult = new JSONArray();

        Status status = autocompletePredictions.getStatus();
        if (status.isSuccess()) {
            for (AutocompletePrediction prediction : autocompletePredictions) {
                JSONObject place = new JSONObject();
                try {
                    place.put("placeId", prediction.getPlaceId());
                    place.put("fullText", prediction.getFullText(null));
                    place.put("primaryText", prediction.getPrimaryText(null));
                    place.put("secondaryText", prediction.getSecondaryText(null));

                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();
                }

                jsonResult.put(place);
            }

            callbackContext.success(jsonResult);
        } else {
            Log.d(TAG, status.getStatusMessage());
            callbackContext.error(status.getStatusMessage());
        }

        autocompletePredictions.release();
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