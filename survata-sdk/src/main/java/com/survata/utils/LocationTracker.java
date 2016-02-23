package com.survata.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public abstract class LocationTracker implements LocationListener {

    private static final String TAG = "LocationTracker";

    public static final long DEFAULT_MIN_TIME_BETWEEN_UPDATES = 5 * 60 * 1000;
    public static final float DEFAULT_MIN_METERS_BETWEEN_UPDATES = 100;
    private static Location sLocation;
    private LocationManager mLocationManagerService;
    private boolean mIsListening = false;
    private boolean mIsLocationFound = false;
    private Context mContext;

    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    public LocationTracker(@NonNull Context context) {
        this.mContext = context;

        this.mLocationManagerService = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (sLocation == null) {
            if (checkPermission()) {
                LocationTracker.sLocation = mLocationManagerService.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }
        if (sLocation == null) {
            if (checkPermission()) {
                LocationTracker.sLocation = mLocationManagerService.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }
        if (sLocation == null) {
            if (checkPermission()) {
                LocationTracker.sLocation = mLocationManagerService.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }
        }
    }

    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    public final void startListening() {
        if (!mIsListening) {
            Log.i(TAG, "startListening");
            // Listen for GPS Updates
            if (isGpsProviderEnabled(mContext) && checkPermission()) {
                mLocationManagerService.requestLocationUpdates(LocationManager.GPS_PROVIDER, DEFAULT_MIN_TIME_BETWEEN_UPDATES, DEFAULT_MIN_METERS_BETWEEN_UPDATES, this);
            } else {
                Log.d(TAG, "gps is not enabled");
            }
            // Listen for Network Updates
            if (isNetworkProviderEnabled(mContext) && checkPermission()) {
                mLocationManagerService.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, DEFAULT_MIN_TIME_BETWEEN_UPDATES, DEFAULT_MIN_METERS_BETWEEN_UPDATES, this);
            } else {
                Log.d(TAG, "network is not enabled");
            }
            // Listen for Passive Updates
            if (isPassiveProviderEnabled(mContext) && checkPermission()) {
                mLocationManagerService.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, DEFAULT_MIN_TIME_BETWEEN_UPDATES, DEFAULT_MIN_METERS_BETWEEN_UPDATES, this);
            } else {
                Log.d(TAG, "passive is not enabled");
            }
            mIsListening = true;

            // If user has set a timeout
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!mIsLocationFound && mIsListening) {
                        Log.i(TAG, "timeout, no location found");
                        stopListening();
                        onTimeout();
                    }
                }
            }, 10 * 1000);
        }
    }

    public final void stopListening() {
        if (mIsListening) {
            if (checkPermission()) {
                mLocationManagerService.removeUpdates(this);
            }
            mIsListening = false;
        }
    }

    @Override
    public final void onLocationChanged(@NonNull Location location) {
        Log.i(TAG, "onLocationChanged " + location);
        LocationTracker.sLocation = new Location(location);
        mIsLocationFound = true;
        onLocationFound(location);
    }

    public abstract void onLocationFound(@NonNull Location location);

    public abstract void onTimeout();

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Log.i(TAG, "onProviderDisabled");
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        Log.i(TAG, "onProviderEnabled");
    }

    @Override
    public void onStatusChanged(@NonNull String provider, int status, Bundle extras) {
        Log.i(TAG, "onStatusChanged");
    }

    public static boolean isGpsProviderEnabled(@NonNull Context context) {
        return isProviderEnabled(context, LocationManager.GPS_PROVIDER);
    }

    public static boolean isNetworkProviderEnabled(@NonNull Context context) {
        return isProviderEnabled(context, LocationManager.NETWORK_PROVIDER);
    }

    public static boolean isPassiveProviderEnabled(@NonNull Context context) {
        return isProviderEnabled(context, LocationManager.PASSIVE_PROVIDER);
    }

    private static boolean isProviderEnabled(@NonNull Context context, @NonNull String provider) {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(provider);
    }

    private boolean checkPermission() {
        return !(ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED);
    }

    public String getPostalCode(Context context, Location location) {
        Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                return address.getPostalCode();
            }

        } catch (IOException e) {
            Log.d(TAG, "fetch zipCode exception", e);
        }

        return "";
    }
}