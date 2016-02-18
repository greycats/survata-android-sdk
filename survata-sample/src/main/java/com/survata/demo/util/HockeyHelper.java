package com.survata.demo.util;

import android.app.Activity;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

public class HockeyHelper {

    private static final String HOCKEYAPP_APPID = "c6f09a616bc645b4b5663b26f221f44b";

    public static void checkForCrashes(Activity activity) {
        CrashManager.register(activity, HOCKEYAPP_APPID);
    }

    public static void checkForUpdates(Activity activity) {
        UpdateManager.register(activity, HOCKEYAPP_APPID);
    }

    public static void unregisterUpdate() {
        UpdateManager.unregister();
    }
}
