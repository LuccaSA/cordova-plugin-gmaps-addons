var argscheck = require('cordova/argscheck'),
    utils = require('cordova/utils'),
    exec = require('cordova/exec');

var Maps = function() {
};

Maps.autocomplete = function(queryString, successCallback, errorCallback) {
	argscheck.checkArgs('S', 'Maps.autocomplete', arguments);
    exec(successCallback, errorCallback, "Maps", "autocomplete", [queryString]);
};

Maps.geocode = function(placeId, successCallback, errorCallback) {
	argscheck.checkArgs('S', 'Maps.geocode', arguments);
    exec(successCallback, errorCallback, "Maps", "geocode", [placeId]);
};

Maps.directions = function(waypoints, routeParams, successCallback, errorCallback) {
	argscheck.checkArgs('AO', 'Maps.directions', arguments);
    exec(successCallback, errorCallback, "Maps", "directions", [waypoints, routeParams]);
};


module.exports = Maps;
