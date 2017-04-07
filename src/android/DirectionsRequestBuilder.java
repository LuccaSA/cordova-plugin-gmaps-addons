package plugin.gmaps.addons;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidParameterException;
import java.util.Iterator;

public class DirectionsRequestBuilder {

    public String execute(Activity activity, JSONArray waypoints, JSONObject routeParams) {
        int waypointsLength = waypoints.length();

        JSONObject query = routeParams;

        if (waypointsLength < 2) {
            throw new InvalidParameterException("There must be at least two waypoints");
        }
        try {
            String encoding = "UTF-8";

            query.put("origin", URLEncoder.encode(waypoints.getString(0), encoding));
            query.put("destination", URLEncoder.encode(waypoints.getString(waypointsLength - 1), encoding));

            for (int i = 1; i < waypointsLength - 1; i++) {
                String existingWaypoints = query.has("waypoints") ? (String) query.get("waypoints") : "";
                query.put("waypoints", existingWaypoints + "|" + URLEncoder.encode(waypoints.getString(i), encoding));
            }

            Iterator<String> iterator = routeParams.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                query.put(key, routeParams.get(key));
            }

            return getQueryString(query);

        } catch (UnsupportedEncodingException e) {
            Log.e(Plugin.TAG, e.getMessage());
            e.printStackTrace();

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
