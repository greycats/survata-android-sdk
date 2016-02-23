package com.survata.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.survata.utils.Logger;

public abstract class RequestManager {

    private static final String TAG = "RequestManager";

    protected Request<?> mCurrentRequest;

    public abstract Request createRequest();

    public void makeRequest(Context context) {
        mCurrentRequest = createRequest();

        if (mCurrentRequest == null) {
            Logger.d(TAG, "mCurrentRequest is null");
            return;
        }

        Networking networking = Networking.getInstance();
        RequestQueue requestQueue = networking.getRequestQueue(context);
        if (requestQueue == null) {
            Logger.d(TAG, "RequestQueue is null");
            return;
        }
        requestQueue.add(mCurrentRequest);
    }
}
