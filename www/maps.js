var argscheck = require('cordova/argscheck'),
    utils = require('cordova/utils'),
    exec = require('cordova/exec');

var Maps = function () {
};

Maps.geocode = function (address, successCallback, errorCallback) {
    argscheck.checkArgs('S', 'Maps.geocode', arguments);
    exec(successCallback, errorCallback, "Maps", "geocode", [address]);
};

Maps.reverseGeocode = function (coords, successCallback, errorCallback) {
    argscheck.checkArgs('O', 'Maps.reverseGeocode', arguments);
    exec(successCallback, errorCallback, "Maps", "reverseGeocode", [coords]);
};

module.exports = Maps;
