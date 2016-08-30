package plugin.gmaps.addons;

import android.location.Address;

import org.json.JSONException;
import org.json.JSONObject;

public class AddressParser {
    public JSONObject parse(Address address) throws JSONException {
        JSONObject jsonResult = new JSONObject();

        StringBuilder formattedAddress = new StringBuilder("");
        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
            formattedAddress.append(address.getAddressLine(i)).append(", ");
        }
        jsonResult.put("formattedAddress", formattedAddress.toString());

        jsonResult.put("countryIsoCode", address.getCountryCode());
        jsonResult.put("countryName", address.getCountryName());
        jsonResult.put("town", address.getLocality());
        jsonResult.put("id", address.getLatitude() + "," + address.getLongitude());

        JSONObject point = new JSONObject();
        point.put("latitude", address.getLatitude());
        point.put("longitude", address.getLongitude());
        jsonResult.put("point", point);

        return jsonResult;
    }
}
