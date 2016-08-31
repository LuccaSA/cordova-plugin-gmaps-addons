# Cordova GMaps Addons

Addons to the [cordova-plugin-googlemaps](https://github.com/mapsplugin/cordova-plugin-googlemaps) plugin exposing a `gmaps.addons` object with :
- Address autocomplete
- Geocoding and reverse geocoding in a more consistant manner than the `cordova-plugin-googlemaps` plugin
- Directions

## Installation

```
cordova plugin add cordova-plugin-gmaps-addons
```

## Methods

### Autocomplete

`autocomplete(queryString, successCallback, errorCallback)`

eg: `maps.addons.autocomplete('place du ch', (res) => console.log(res), (err) => console.log(err))`

Given a query string, returns an array of possible locations.

#### Input

| Attr           | Type       | Details                                                |
| -------------- |:----------:| -------------------------------------------------------|
| queryString    | `string`   | The query string to auto complete - eg: `place du cha` |
| successCallback| `function` |                  Success callback                      |
| errorCallback  | `function` |                    Error Callback                      |

#### Output

eg: `[{placeId: xx, fullText: xx, primaryText: xx, secondaryText: xx}, ...]`

The success callback receives an array of objects with the following properties :

| Attr          | Type     | Details                                                  |
| ------------- |:--------:| ---------------------------------------------------------|
| placeId       | `string` | The Google place Id                                      |
| fullText      | `string` | Address full text - eg: `Place du Châtelet, 75001 Paris` |
| primaryText   | `string` | Address first / primary part - eg: `Place du Châtelet`   |
| secondaryText | `string` | Address second part - eg: `75001 Paris`                  |


### Geocode & Reverse geocode
- `reverseGeocode({lat: xxx, lng: xxx}, successCallback, errorCallback)`
- `geocode('place du châtelet', successCallback, errorCallback)`

eg:
- `maps.addons.reverseGeocode({lat: 48.883364, lng: 2.327081}, (res) => console.log(res), (err) => console.log(err))`
- `maps.addons.geocode('Place du Châtelet, Paris', (res) => console.log(res), (err) => console.log(err))`

Given a coordinate object / address string, returns an array of addresses objects (specified below).

#### Input

| Method         | Attr            | Type       | Details                                                             |
| -------------- | --------------- |:----------:| --------------------------------------------------------------------|
| reverseGeocode | coordinate      | `Object`   | The coordinate object to geocode in the form `{lat: xxx, lng: xxx}` |
| geocode        | address         | `string`   | The address to reverse geocode - eg: `Place du Châtelet, Paris`     |
| both           | successCallback | `function` | Success callback                                                    |
| both           | errorCallback   | `function` | Error Callback                                                      |

#### Output

The success callback receives **an array of objects** with the following properties :

| Attr             | Type     | Details                                                                                 |
| ---------------- |:--------:| ----------------------------------------------------------------------------------------|
| id               | `string` | An id made of the concatenation of the latitude and lagitude - eg: `48.883364,2.327081` |
| formattedAddress | `string` | Address full text - eg: `Place du Châtelet, 75001 Paris`                                |
| town             | `string` | City - eg: `Paris`                                                                      |
| countryName      | `string` | Country name - eg: `France`                                                             |
| countryIsoCode   | `string` | Ccountry ISO code - eg: `FR`                                                            |
| point            | `string` | Coordinate - eg: `{latitude: 48.883364, longitude: 2.327081}`                           |

### Directions
`directions(waypoints, routeParams, successCallback, errorCallback)`

eg: `maps.addons.directions(['place du châtelet, Paris', 'place des ternes, Paris'], {}, (res) => console.log(res), (err) => console.log(err))`

Given waypoints and params, return a Google directions object.

The default [Google Maps Directions API parameters](https://developers.google.com/maps/documentation/directions/intro?hl=fr#RequestParameters) are used by default.

#### Input

| Attr           | Type            | Details                                                                               |
| -------------- |:---------------:| --------------------------------------------------------------------------------------|
| waypoints      | `Array<string>` | An array of waypoints - eg: `['place du châtelet, Paris', 'place des ternes, Paris']` |
| routeParams    | `Object`        | The route params - eg: `{ units: imperial }`                                          |
| successCallback| `function`      | Success callback                                                                      |
| errorCallback  | `function`      | Error Callback                                                                        |

#### Output

Output the same exact JSON as the [JSON Google Maps Directions API](https://developers.google.com/maps/documentation/directions/intro?hl=fr#DirectionsResponses).

