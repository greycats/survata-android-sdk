package com.survata.network;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class Networking {

    private volatile static RequestQueue sRequestQueue;

    public static RequestQueue getRequestQueue(Context context) {
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
