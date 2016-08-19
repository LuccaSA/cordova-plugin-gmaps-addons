package com.cleemy.maps;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class PathJSONParser {

    public JSONObject parse(JSONObject jObject) {
        JSONObject route = new JSONObject();
        JSONArray waypoints = new JSONArray();
        int distance = 0; // in meters

        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;
        try {
            jRoutes = jObject.getJSONArray("routes");

            // Traversing all routes
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");

                // Traversing all legs
                Integer legsLength = jLegs.length();
                for (int j = 0; j < legsLength; j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                    distance += ((JSONObject) jLegs.get(j)).getJSONObject("distance").getInt("value");

                    // Traversing all steps
                    Integer stepsLength = jSteps.length();
                    for (int k = 0; k < stepsLength; k++) {
                        waypoints.put(((JSONObject) jSteps.get(k)).get("start_location"));

                        // For the final point, add end_location as well to close the path
                        if (k == stepsLength - 1 && j == legsLength - 1) {
                            waypoints.put(((JSONObject) jSteps.get(k)).get("end_location"));
                        }
                    }
                }
            }

            route.put("waypoints", waypoints);
            route.put("distance", distance);

        } catch (JSONException e) {
            e.printStackTrace();

        } catch (Exception e) {
        }

        return route;
    }
}