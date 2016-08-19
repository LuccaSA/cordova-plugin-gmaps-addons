package com.cleemy.maps;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;

import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.Iterator;

public class Plugin extends CordovaPlugin implements ICallBackListener<JSONObject> {

    public static final String TAG = "Cleemy-AutoComplete";
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

        } else if (action.equals("geocode")) {
            String placeId = args.getString(0);
            geocode(placeId, callbackContext);
            return true;

        } else if (action.equals("directions")) {
            JSONArray waypoints = args.getJSONArray(0);
            JSONObject routeParams = args.getJSONObject(1);
            getDirections(waypoints, routeParams, callbackContext);
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
        }
        else {
            Log.d(TAG, status.getStatusMessage());
            callbackContext.error(status.getStatusMessage());
        }

        callbackContext.success(jsonResult);
        autocompletePredictions.release();
    }

    private void geocode(String placeId, CallbackContext callbackContext) {
        if (placeId.isEmpty()) {
            callbackContext.error("Expected one non-empty string argument.");
        }

        PendingResult<PlaceBuffer> result = Places.GeoDataApi.getPlaceById(_googleApiClient, placeId);
        PlaceBuffer placeGeocodes = result.await();

        JSONObject geocode = new JSONObject();
        Status status = placeGeocodes.getStatus();
        if (status.isSuccess()) {
            LatLng placeLatLng = placeGeocodes.get(0).getLatLng();

            try {
                geocode.put("lat", placeLatLng.latitude);
                geocode.put("lng", placeLatLng.longitude);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, status.getStatusMessage());
            callbackContext.error(status.getStatusMessage());
        }

        callbackContext.success(geocode);
        placeGeocodes.release();
    }

    private void getDirections(JSONArray waypoints, JSONObject routeParams, CallbackContext callbackContext) {
        String params = new RequestBuilder().execute(waypoints, routeParams);
        String url = "https://maps.googleapis.com/maps/api/directions/json?" + params;

        Log.d(TAG, "Asynchronously downloading directions");
        ReadTask downloadTask = new ReadTask(this);
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