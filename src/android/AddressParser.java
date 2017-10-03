package plugin.gmaps.addons;

import android.location.Address;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import java.lang.Exception;

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

        List<String> addressLines = this.getAddressLines(address); 
        jsonResult.put("formattedAddress", TextUtils.join(", ", addressLines));

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

    private List<String> getAddressLines(Address address) {

        List<String> addressLines = new ArrayList<String>();

        int addressLinesCount = address.getMaxAddressLineIndex() + 1;
        // +1 fixes off-by-one error for Android 7+ https://stackoverflow.com/a/45325913

        for (int i = 0; i < addressLinesCount; i++) {
            
            try {
                String addressLine = address.getAddressLine(i);
                addressLines.add(addressLine);
            } catch(Exception e) { }
        }

        return addressLines;
    }
}
