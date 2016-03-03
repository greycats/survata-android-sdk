package com.survata.utils;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import net.jcip.annotations.ThreadSafe;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

@ThreadSafe
public abstract class LocationTracker implements LocationListener {

    private static final String TAG = "LocationTracker";

    private static final long DEFAULT_MIN_TIME_BETWEEN_UPDATES = 5 * 60 * 1000;
    private static final long DEFAULT_TIMEOUT = 5 * 1000;
    private static final float DEFAULT_MIN_METERS_BETWEEN_UPDATES = 100;

    private static final long INTERVAL_TIME = 24 * 60 * 60 * 1000;
    private static final String ZIP_CODE = "ZIP_CODE";
    private static final String ZIP_CODE_DATE = "ZIP_CODE_DATE";

    private LocationManager mLocationManagerService;
    private final Context mContext;
    private volatile boolean mIsListening = false;
    private volatile boolean mIsLocationFound = false;

    public abstract void onLocationFound(@NonNull String zipCode);

    public abstract void onLocationFoundFailed();

    public LocationTracker(@NonNull Context context) {
        this.mContext = context;
    }

    public void start() {
        String zipCode = getCachedZipCode(mContext);

        if (zipCode != null) {
            onLocationFound(zipCode);
            return;
        }

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // You need to ask the user to enable the permissions
            Logger.e(TAG, "need permission: " + Manifest.permission.ACCESS_FINE_LOCATION + ", " + Manifest.permission.ACCESS_COARSE_LOCATION);

            onLocationFoundFailed();
        } else {
            this.mLocationManagerService = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            startListening();
        }
    }

    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    public synchronized final void startListening() {
        if (!mIsListening) {
            Log.i(TAG, "startListening");
            // Listen for GPS Updates
            if (isGpsProviderEnabled(mContext) && checkPermission()) {
                mLocationManagerService.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        DEFAULT_MIN_TIME_BETWEEN_UPDATES, DEFAULT_MIN_METERS_BETWEEN_UPDATES, this);
            } else {
                Log.d(TAG, "gps is not enabled");
            }
            // Listen for Network Updates
            if (isNetworkProviderEnabled(mContext) && checkPermission()) {
                mLocationManagerService.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        DEFAULT_MIN_TIME_BETWEEN_UPDATES, DEFAULT_MIN_METERS_BETWEEN_UPDATES, this);
            } else {
                Log.d(TAG, "network is not enabled");
            }
            // Listen for Passive Updates
            if (isPassiveProviderEnabled(mContext) && checkPermission()) {
                mLocationManagerService.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER,
                        DEFAULT_MIN_TIME_BETWEEN_UPDATES, DEFAULT_MIN_METERS_BETWEEN_UPDATES, this);
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
                        onLocationFoundFailed();
                    }
                }
            }, DEFAULT_TIMEOUT);
        }
    }

    public synchronized final void stopListening() {
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
        if (mIsListening) {
            mIsLocationFound = true;
            stopListening();

            String zipCode = getPostalCode(mContext, location);
            onLocationFound(zipCode);
            saveCachedZipCode(mContext, zipCode);
        }
    }

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

    private static boolean isGpsProviderEnabled(@NonNull Context context) {
        return isProviderEnabled(context, LocationManager.GPS_PROVIDER);
    }

    private static boolean isNetworkProviderEnabled(@NonNull Context context) {
        return isProviderEnabled(context, LocationManager.NETWORK_PROVIDER);
    }

    private static boolean isPassiveProviderEnabled(@NonNull Context context) {
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

    private static String getPostalCode(Context context, Location location) {
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

    private void saveCachedZipCode(Context context, String zipCode) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ZIP_CODE, zipCode);
        editor.putLong(ZIP_CODE_DATE, System.currentTimeMillis());
        editor.apply();
    }

    private String getCachedZipCode(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        long date = sharedPreferences.getLong(ZIP_CODE_DATE, -1);

        if (date != -1 && System.currentTimeMillis() - date < INTERVAL_TIME) {
            return sharedPreferences.getString(ZIP_CODE, null);
        }
        return null;
    }
}