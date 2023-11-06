package com.example.locationpinned;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class GeocoderHelper {

    private Geocoder geocoder;

    public GeocoderHelper(Context context) {
        this.geocoder = new Geocoder(context, Locale.getDefault());
    }
    // Geocoder method to get Address by passing latitude and longitude
    public String getAddress(double latitude, double longitude) {
        try {
            // call geocoder get from location method and only return first result
            List addressList = geocoder.getFromLocation(latitude, longitude, 1);

            // if address list is not empty build address string
            // and return address for latitude and longitude
            if (addressList != null && addressList.size() > 0) {
                Address address = (Address)
                addressList.get(0);
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i));
                }

                return sb.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}

