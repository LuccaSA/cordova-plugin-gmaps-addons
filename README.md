# Cordova GMaps Addons

Addons to the [cordova-plugin-googlemaps](https://github.com/mapsplugin/cordova-plugin-googlemaps) plugin exposing a `gmaps.addons` object with :
- Address autocomplete
- Geocoding and reverse geocoding in a more consistant manner than the `cordova-plugin-googlemaps` plugin

## Installation

```
cordova plugin add cordova-plugin-gmaps-addons
```

## Methods

### Geocode & Reverse geocode
- `reverseGeocode({lat: xxx, lng: xxx}, successCallback, errorCallback)`
- `geocode('place du châtelet', successCallback, errorCallback)`

eg:
- `maps.addons.reverseGeocode({lat: 48.883364, lng: 2.327081}, (res) => console.log(res), (err) => console.log(err))`
- `maps.addons.geocode('Place du Châtelet, Paris', (res) => console.log(res), (err) => console.log(err))`

Given a coordinate object / address string, returns an array of addresses objects (specified below).

#### Input

| Method         | Attr            |    Type    | Details                                                             |
| -------------- | --------------- | :--------: | ------------------------------------------------------------------- |
| reverseGeocode | coordinate      |  `Object`  | The coordinate object to geocode in the form `{lat: xxx, lng: xxx}` |
| geocode        | address         |  `string`  | The address to reverse geocode - eg: `Place du Châtelet, Paris`     |
| both           | successCallback | `function` | Success callback                                                    |
| both           | errorCallback   | `function` | Error Callback                                                      |

#### Output

The success callback receives **an array of objects** with the following properties :

| Attr             |   Type   | Details                                                                                 |
| ---------------- | :------: | --------------------------------------------------------------------------------------- |
| id               | `string` | An id made of the concatenation of the latitude and lagitude - eg: `48.883364,2.327081` |
| formattedAddress | `string` | Address full text - eg: `Place du Châtelet, 75001 Paris`                                |
| town             | `string` | City - eg: `Paris`                                                                      |
| countryName      | `string` | Country name - eg: `France`                                                             |
| countryIsoCode   | `string` | Ccountry ISO code - eg: `FR`                                                            |
| point            | `string` | Coordinate - eg: `{lat: 48.883364, lng: 2.327081}`                                      |
