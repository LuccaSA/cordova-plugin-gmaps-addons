package plugin.gmaps.addons;

import android.location.Address;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AddressParser {
    public JSONArray parse(List<Address> addresses) throws JSONException {
        JSONArray jsonResult = new JSONArray();

        for (Address address : addresses) {
            jsonResult.put(parse(address));
        }

        return jsonResult;
    }

    private JSONObject parse(Address address) throws JSONException {
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
        point.put("lat", address.getLatitude());
        point.put("lng", address.getLongitude());
        jsonResult.put("point", point);

        return jsonResult;
    }
}
