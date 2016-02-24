package com.survata.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.content.ContextCompat;

public class Geocode {

    private static final String TAG = "Geocode";

    private LocationTracker mLocationTracker;

    public interface GeocodeCallback {
        void onZipCodeFind(String zipCode);
    }

    public void get(final Context context, final GeocodeCallback geocodeCallback) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // You need to ask the user to enable the permissions
            Logger.e(TAG, "need permission");

            geocodeCallback.onZipCodeFind("");
        } else {
            mLocationTracker = new LocationTracker(context) {
                @Override
                public void onLocationFound(Location location) {
                    Logger.e(TAG, "onLocationFound " + location);
                    mLocationTracker.stopListening();
                    geocodeCallback.onZipCodeFind(mLocationTracker.getPostalCode(context, location));
                }

                @Override
                public void onTimeout() {
                    Logger.e(TAG, "onLocationFound timeout");

                    geocodeCallback.onZipCodeFind("");
                }
            };
            mLocationTracker.startListening();
        }
    }
}
