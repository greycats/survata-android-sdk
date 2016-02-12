package com.survata.network;

import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class Networking {

    private volatile static RequestQueue sRequestQueue;

    @NonNull
    public static RequestQueue getRequestQueue(@NonNull Context context) {
        RequestQueue requestQueue = sRequestQueue;
        if (requestQueue == null) {
            synchronized (Networking.class) {
                requestQueue = sRequestQueue;
                if (requestQueue == null) {
                    requestQueue = Volley.newRequestQueue(context);
                    sRequestQueue = requestQueue;
                    requestQueue.start();
                }
            }
        }
        return requestQueue;
    }

}
