package com.example.locationpinned;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class FindAddressHelper {

    private Geocoder geocoder;

    public FindAddressHelper(Context context) {
        this.geocoder = new Geocoder(context, Locale.getDefault());
    }
    // Geocoder method to get Address by passing latitude and logitude
    public String getAddress(double latitude, double longitude) {
        try {
            List addressList = geocoder.getFromLocation(latitude, longitude, 1);
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

