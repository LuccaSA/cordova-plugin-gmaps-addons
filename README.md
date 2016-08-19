# Cordova GMaps Addons

Addons to the [cordova-plugin-googlemaps](https://github.com/mapsplugin/cordova-plugin-googlemaps) plugin exposing a `gmaps.addons` object with :
- Address autocomplete
- Revese geocoding from `place_id`
- Directions

## Installation

```
cordova plugin add cordova-plugin-gmaps-addons
```

## Methods

- `autocomplete(queryString, successCallback, errorCallback)` - Given a query string, returns the possible locations `{placeId: xx, fullText: xx, primaryText: xx, secondaryText: xx}`
- `geocode(placeId, successCallback, errorCallback)` - Given a Google `place_id`, returns the coordinate `{lat: xxx, lng: xxx}`
- `directions(waypoints, routeParams, successCallback, errorCallback)` - Given waypoints and params, return an object with the distance (in meters) and waypoints (in coordinate format) `{distance: 111, waypoints: [...]}`

