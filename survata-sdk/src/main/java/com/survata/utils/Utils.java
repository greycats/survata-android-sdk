package com.survata.utils;

import android.content.Context;

import com.survata.R;

public class Utils {

    private static final String TAG = "Utils";

    private static final String USER_AGENT = "%s Survata/Android/%s";

    public static String getUserAgent(Context context) {
        String packageName = "Unknown";
        String versionName = "Unknown";
        try {
            packageName = context.getPackageName();
            versionName = context.getPackageManager().getPackageInfo(packageName, 0).versionName;
        } catch (Exception e) {
            Logger.d(TAG, "exception", e);
        }

        String currentUserAgent = context.getString(R.string.app_name) + "/" + packageName;
        return String.format(USER_AGENT, currentUserAgent, versionName);
    }
}
