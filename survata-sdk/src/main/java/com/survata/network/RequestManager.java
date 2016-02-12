package com.survata.network;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

public abstract class RequestManager {

    private static final String TAG = "RequestManager";

    @Nullable
    protected Request<?> mCurrentRequest;

    public abstract Request createRequest();

    public void makeRequest(Context context) {
        mCurrentRequest = createRequest();
        RequestQueue requestQueue = Networking.getRequestQueue(context);
        if (requestQueue == null) {
            Log.d(TAG, "RequestQueue is null");
            return;
        }
        requestQueue.add(mCurrentRequest);
    }
}
