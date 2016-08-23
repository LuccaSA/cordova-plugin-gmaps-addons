package plugin.gmaps.addons;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidParameterException;
import java.util.Iterator;

public class DirectionsRequestBuilder {

    public String execute(JSONArray waypoints, JSONObject routeParams) {
        int waypointsLength = waypoints.length();

        JSONObject query = routeParams;

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

            if (routeParams.has("unit")) {
                query.put("unit", routeParams.get("unit"));
            }

            if (routeParams.has("avoid")) {
                query.put("avoid", routeParams.get("avoid"));
            }

            return getQueryString(query);

        }catch (JSONException e) {
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