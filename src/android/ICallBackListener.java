package plugin.gmaps.addons;


import org.apache.cordova.CallbackContext;

public interface ICallBackListener<T> {
    void callback(T param, CallbackContext callbackContext);
}
