package plugin.gmaps.addons;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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

import java.util.Iterator;
import java.util.Locale;

public class Plugin extends CordovaPlugin implements ICallBackListener<JSONObject> {

    public static final String TAG = "GMAPS-ADDONS";
    private GoogleApiClient _googleApiClient;
    private CallbackContext _callbackContext;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        _googleApiClient = new GoogleApiClient
                .Builder(cordova.getActivity())
                .addApi(Places.GEO_DATA_API)
                .build();

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
        _callbackContext = callbackContext;

        if (action.equals("autocomplete")) {
            String query = args.getString(0);
            autocomplete(query, callbackContext);
            return true;

        } else if (action.equals("reverseGeocode")) {
            String address = args.getString(0);
            reverseGeocode(address, callbackContext);
            return true;

        } else if (action.equals("geocode")) {
            JSONObject coords = args.getJSONObject(0);
            geocode(coords, callbackContext);
            return true;

        } else if (action.equals("directions")) {
            JSONArray waypoints = args.getJSONArray(0);
            JSONObject routeParams = args.getJSONObject(1);
            directions(waypoints, routeParams, callbackContext);
            return true;
        }
        return false;
    }

    private void autocomplete(String query, final CallbackContext callbackContext) {
        if (query.isEmpty()) {
            callbackContext.error("Expected one non-empty string argument.");
        }

        PendingResult<AutocompletePredictionBuffer> result = Places.GeoDataApi.getAutocompletePredictions(_googleApiClient, query, null, null);
        AutocompletePredictionBuffer autocompletePredictions = result.await();

        JSONArray jsonResult = new JSONArray();

        Status status = autocompletePredictions.getStatus();
        if (status.isSuccess()) {
            Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
            while (iterator.hasNext()) {
                AutocompletePrediction prediction = iterator.next();

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
        }
        else {
            Log.d(TAG, status.getStatusMessage());
            callbackContext.error(status.getStatusMessage());
        }

        autocompletePredictions.release();
    }

    private void reverseGeocode(String address, CallbackContext callbackContext) {
        Geocoder geocoder = new Geocoder(cordova.getActivity(), Locale.getDefault());

        try {
            Address detailedAddress = geocoder.getFromLocationName(address, 1).get(0);

            JSONObject jsonResult = new AddressParser().parse(detailedAddress);

            callbackContext.success(jsonResult);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            callbackContext.error(e.getMessage());
        }
    }

    private void geocode(JSONObject coords, CallbackContext callbackContext) {
        Geocoder geocoder = new Geocoder(cordova.getActivity(), Locale.getDefault());

        try {
            Double lat = Double.parseDouble(coords.get("lat").toString());
            Double lng = Double.parseDouble(coords.get("lng").toString());


            Address address = geocoder.getFromLocation(lat, lng, 1).get(0);
            JSONObject jsonResult = new AddressParser().parse(address);

            callbackContext.success(jsonResult);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            callbackContext.error(e.getMessage());
        }
    }

    private void directions(JSONArray waypoints, JSONObject routeParams, CallbackContext callbackContext) {
        String params = new DirectionsRequestBuilder().execute(cordova.getActivity(), waypoints, routeParams);
        String url = "https://maps.googleapis.com/maps/api/directions/json?" + params;

        Log.d(TAG, "Asynchronously downloading directions");
        DirectionsReadTask downloadTask = new DirectionsReadTask(this);
        downloadTask.execute(url);

        PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
        pluginResult.setKeepCallback(true);

        callbackContext.sendPluginResult(pluginResult);
    }

    public void callback(JSONObject route) {
        Log.d(TAG, "Route callback fired");

        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, route);
        pluginResult.setKeepCallback(false);

        _callbackContext.sendPluginResult(pluginResult);
    }
}