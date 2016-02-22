package com.survata.network;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

// TODO: instead of making the queue a single static variable, make "Networking" a singleton,
// a much more common pattern
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
