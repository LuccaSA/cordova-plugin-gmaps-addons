package plugin.gmaps.addons;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidParameterException;
import java.util.Iterator;

public class DirectionsRequestBuilder {

    public String execute(Activity activity, JSONArray waypoints, JSONObject routeParams) {
        int waypointsLength = waypoints.length();

        JSONObject query = routeParams;

        try {
            ApplicationInfo appliInfo = activity.getPackageManager().getApplicationInfo(activity.getPackageName(), PackageManager.GET_META_DATA);
            String API_KEY = appliInfo.metaData.getString("com.google.android.geo.API_KEY");
            query.put("key", API_KEY);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (waypointsLength < 2) {
            throw new InvalidParameterException("There must be at least two waypoints");
        }
        try {
            query.put("origin", waypoints.getString(0));
            query.put("destination", waypoints.getString(waypointsLength - 1));

            for (int i = 1; i < waypointsLength - 1; i++) {
                String existingWaypoints = (String) query.get("waypoints");

                query.put("waypoints", existingWaypoints + "|" + waypoints.getString(i));
            }

            Iterator<String> iterator = routeParams.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                query.put(key, routeParams.get(key));
            }

            return getQueryString(query);

        } catch (JSONException e) {
            Log.e(Plugin.TAG, e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    private String getQueryString(JSONObject object) {
        StringBuilder sb = new StringBuilder();

        try {
            Iterator<String> keys = object.keys();
            while (keys.hasNext()) {
                String key = keys.next();

                sb.append(key)
                        .append("=")
                        .append((String) object.get(key))
                        .append("&");
            }

        } catch (JSONException e) {
            Log.e(Plugin.TAG, e.getMessage());
            e.printStackTrace();
        }

        return sb.toString();
    }
}
